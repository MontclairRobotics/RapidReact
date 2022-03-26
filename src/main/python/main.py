#! /usr/bin/etc python3
import sys
import os
import time
import math
import cv2
import json

import numpy as np

from networktables import *
from cscore import CameraServer

from enum import Enum
from threading import Condition


########################
# region # Constants
########################
VERSION = "0.6.0"

LOW_BLUE = (190 / 2, 100, 50)
HIGH_BLUE = (220 / 2, 255, 240)

LOW_RED = (115, 50, 100)
HIGH_RED = (160, 255, 255)

CONTOUR_DRAW_COL = (255, 255, 255)
CONTOUR_DRAW_WIDTH = 3
CONTOUR_DRAW_CROSS_SIZE = 10
########################
# endregion
########################


########################
# region # Classes and Helpers
########################
def clear_cons():
    return


class ContourInfo:

    @staticmethod
    def find_contours(image, *args, **kwargs):
        return [
            ContourInfo(c, image)
            for c in cv2.findContours(image, *args, **kwargs)[0]
        ]

    @staticmethod
    def draw_contours(image, contours, color, thickness, cross_size, *args, **kwargs):
        cv2.drawContours(image, [c.contour for c in contours], -1, color, thickness, *args, **kwargs)
        for c in contours:
            cx, cy = c.center
            cv2.line(image, (cx + cross_size, cy), (cx - cross_size, cy), color, thickness)
            cv2.line(image, (cx, cy + cross_size), (cx, cy - cross_size), color, thickness)

    def _copy_from(self, other: 'ContourInfo'):
        self.contour = other.contour
        self.perimeter = other.perimeter
        self.area = other.area
        self.mean = other.mean
        self.moments = other.moments
        self.circularity = other.circularity
        self.center = other.center
        self.has_center = other.has_center
        self.mask = other.mask

    def __init__(self, contour, src_img):

        self.contour = contour
        self.perimeter = cv2.arcLength(contour, True)
        self.area = cv2.contourArea(contour)

        self.moments = cv2.moments(contour)

        self.circularity = 4*math.pi*self.area/(self.perimeter**2) if self.perimeter != 0 else 0

        self.has_center = self.moments['m00'] != 0
        self.center = (
            int(self.moments['m10']/self.moments['m00']),
            int(self.moments['m01']/self.moments['m00'])
        ) if self.has_center else (-1, -1)

        self.mask = np.zeros(src_img.shape, np.uint8)
        cv2.drawContours(self.mask, [contour], -1, 255, -1)

        self.mean = cv2.mean(src_img, mask=self.mask)

########################
# endregion
########################


def main():

    clear_cons()

    print('#' * 50)
    print('# Starting frc script:')
    print('#' * 50)
    print('')
    
    ###################################
    # Camera settings
    ###################################
    with open('/boot/frc.json') as f:
        config = json.load(f)
    ipt_camera = config['cameras'][0]

    width_config = ipt_camera['width']
    height_config = ipt_camera['height']

    #print("the width_config is " + str(width_config))

    ###################################
    # NetworkTables
    ###################################
    NetworkTables.initialize(server='10.5.55.2')
    network_table_inited = [False]

    network_cond = Condition()

    def listener(connected, info):
        with network_cond:
            network_table_inited[0] = True
            network_cond.notify()
    
    NetworkTables.addConnectionListener(listener, immediateNotify=True)

    with network_cond:
        if not network_table_inited[0]:
            print("Waiting for network tables!")
            network_cond.wait()
    
    print("Connected!")

    data_table = NetworkTables.getTable('Vision')

    proto_ver_entry = data_table.getEntry('__ver')

    xs_entry = data_table.getEntry('Xs')
    ys_entry = data_table.getEntry('Ys')
    angles_entry = data_table.getEntry('Angles')

    current_team_entry = data_table.getEntry('CurrentTeam')

    ###################################
    # Capture data
    ###################################
    camera_server = CameraServer.getInstance()

    ipt_cam = camera_server.startAutomaticCapture(dev=0, name='Camera Vision')
    other_cam = camera_server.startAutomaticCapture(dev=2, name='Shooter Vision')

    input_stream = camera_server.getVideo(camera=ipt_cam)
    other_stream = camera_server.getVideo(camera=other_cam)
    output_stream = camera_server.putVideo('Highlight Out', width_config * 2, height_config)
    
    base_img = np.zeros(shape=(width_config, height_config, 3), dtype=np.uint8)

    ###################################
    # Main Function
    ###################################
    while True:

        #print('Executing frame!')

        # Get camera data
        frame_time, frame = input_stream.grabFrame(base_img)

        if frame_time == 0:
            time.sleep(0.01)
            continue

        height, width = frame.shape[1::-1]

        current_team = current_team_entry.getString('red')
        print(f'Current team: {current_team!r}')

        # Preprocessing
        if current_team.lower() == 'red':
            convert = cv2.cvtColor(frame, cv2.COLOR_RGB2HSV)
            mask = cv2.inRange(convert, LOW_RED, HIGH_RED)
        else:
            convert = cv2.cvtColor(frame, cv2.COLOR_BGR2HSV)
            mask = cv2.inRange(convert, LOW_BLUE, HIGH_BLUE)

        detectable = cv2.blur(mask, (width_config // 40, height_config // 40))
        _, detectable = cv2.threshold(detectable, 100, 255, cv2.THRESH_BINARY_INV)
        detectable_bordered = cv2.copyMakeBorder(detectable, 1, 1, 1, 1, cv2.BORDER_CONSTANT, value=255)

        # Get contours
        contours = [
            c for c in ContourInfo.find_contours(
                detectable_bordered, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE
            )
            if c.area > width_config * height_config * 0.03 * 0.03 and c.circularity > 0.5 and c.mean[0] < 127
        ]

        # if len(contours) == 0:
        #     print("No balls!")
        # else:
        #     print("Balls!")

        # Draw into debug output stream
        output = frame
        ContourInfo.draw_contours(
            output, contours, CONTOUR_DRAW_COL, CONTOUR_DRAW_WIDTH, CONTOUR_DRAW_CROSS_SIZE
        )
        output = cv2.copyMakeBorder(output, 0, 0, width_config // 2, 0, cv2.BORDER_CONSTANT, value=(255,255,255))
        output = cv2.putText(output, current_team, (20, 50), cv2.FONT_HERSHEY_SIMPLEX, 
                   1, (0,0,0), 1, cv2.LINE_AA)

        output_stream.putFrame(output)

        # Update NetworkTables
        angles_entry.setDoubleArray([2 * c.center[0] / width - 1 for c in contours])
        xs_entry.setDoubleArray([c.center[0] for c in contours])
        ys_entry.setDoubleArray([c.center[1] for c in contours])
        proto_ver_entry.setString(VERSION)


if __name__ == '__main__':
    main()
