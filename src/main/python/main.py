#! /usr/bin/etc python3
from datetime import datetime
import sys
import os
import time
import math
import cv2
import json

import numpy as np

from networktables import *

from cscore import CameraServer
from threading import Condition


########################
# region # Constants
########################
VERSION = "1.1.0"

LOW_BLUE = (190 / 2, 100, 50)
HIGH_BLUE = (220 / 2, 255, 240)

LOW_RED = (115, 50, 100)
HIGH_RED = (160, 255, 255)

BLUR_FACTOR = 1 / 20
BLUR_THRESHOLD = 100

CONTOUR_DRAW_COL = (255, 255, 255)
CONTOUR_DRAW_WIDTH = 2
CONTOUR_DRAW_CROSS_SIZE = 5

COLOR_STRIPE_WIDTH_FACTOR = 0.1
########################
# endregion
########################


########################
# region # Classes and Helpers
########################
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
    NetworkTables.initialize(server="10.5.55.2") #change in future: "roborio-555-frc.local"
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
    
    time.sleep(1)
    print("Connected!")

    data_table = NetworkTables.getTable('Vision')
    sd_table = NetworkTables.getTable('SmartDashboard')

    proto_ver_entry = data_table.getEntry('__ver')

    xs_entry = data_table.getEntry('Xs')
    ys_entry = data_table.getEntry('Ys')
    angles_entry = data_table.getEntry('Angles')
    areas_entry = data_table.getEntry('Areas')
    circularities_entry = data_table.getEntry('Circularities')
    perimeters_entry = data_table.getEntry('Perimeters')
    is_writing_entry = data_table.getEntry('IsWriting')
    counter_entry = data_table.getEntry('Counter')
    counter = 0

    min_area_entry = sd_table.getEntry('MinArea')
    min_circularity_entry = sd_table.getEntry('MinCircularity')
    min_area_entry.setDefaultDouble(0.1 ** 2)
    min_circularity_entry.setDefaultDouble(0.5)

    current_team_entry = data_table.getEntry('CurrentTeam')

    ###################################
    # Capture data
    ###################################
    cs = CameraServer.getInstance()

    ipt_cam = cs.startAutomaticCapture(dev=0, name='Camera Vision')
    other_cam = cs.startAutomaticCapture(dev=2, name='Shooter Vision')

    input_stream = cs.getVideo(camera=ipt_cam)
    other_stream = cs.getVideo(camera=other_cam)

    output_stream = cs.putVideo('Vision Output', width_config * 2, height_config)
    
    base_img = np.zeros(shape=(height_config, width_config, 3), dtype=np.uint8)

    ###################################
    # Main Functionality
    ###################################
    while True:

        # Get camera data
        frame_time, frame = input_stream.grabFrame(base_img)

        if frame_time == 0:
            time.sleep(0.01)
            continue

        # Dimension information
        real_width, real_height = frame.shape[1::-1]
        max_area = real_width * real_height

        min_area = min_area_entry.getDouble(0.1 ** 2)
        min_circularity = min_circularity_entry.getDouble(0.5)

        # Get current alliance
        current_team = current_team_entry.getString('Red')

        # Preprocessing
        if current_team == 'Red':
            convert = cv2.cvtColor(frame, cv2.COLOR_RGB2HSV)
            mask = cv2.inRange(convert, LOW_RED, HIGH_RED)
        else:
            convert = cv2.cvtColor(frame, cv2.COLOR_BGR2HSV)
            mask = cv2.inRange(convert, LOW_BLUE, HIGH_BLUE)

        _, detectable = cv2.threshold(
            cv2.blur(
                mask, #mask
                (
                    int(real_width * BLUR_FACTOR), #x
                    int(real_height * BLUR_FACTOR) #y
                )
            ), 
            BLUR_THRESHOLD, 255, cv2.THRESH_BINARY_INV
        )

        detectable_bordered = cv2.copyMakeBorder(
            detectable, 1, 1, 1, 1, cv2.BORDER_CONSTANT, value=BLUR_THRESHOLD-1
        )

        # Get contours
        contours = [
            c for c in ContourInfo.find_contours(
                detectable_bordered, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE
            )
            if 1 >= c.area / max_area > min_area and c.circularity > min_circularity and c.mean[0] < 127
        ]

        # Draw into output stream
        output = frame
        ContourInfo.draw_contours(
            output, contours, CONTOUR_DRAW_COL, CONTOUR_DRAW_WIDTH, CONTOUR_DRAW_CROSS_SIZE
        )
        stripe_w = int(real_width * COLOR_STRIPE_WIDTH_FACTOR / 2)
        output = cv2.copyMakeBorder(
            output, 
            0, 0, stripe_w, stripe_w, 
            cv2.BORDER_CONSTANT, 
            value=(0,0,255) if current_team.lower() == 'red' else (255,0,0)
        )

        # mlcena@montclair.blah.blah.blah

        output_stream.putFrame(output)

        # Update NetworkTables
        is_writing_entry.setBoolean(True)
        circularities_entry.setDoubleArray([c.circularity for c in contours])
        perimeters_entry.setDoubleArray([c.perimeter for c in contours])
        angles_entry.setDoubleArray([2 * c.center[0] / real_width - 1 for c in contours])
        areas_entry.setDoubleArray([c.area / max_area for c in contours])
        xs_entry.setDoubleArray([c.center[0] for c in contours])
        ys_entry.setDoubleArray([c.center[1] for c in contours])
        proto_ver_entry.setString(VERSION)
        counter_entry.setDouble(counter)
        is_writing_entry.setBoolean(False)

        counter += 1


if __name__ == '__main__':
    main()
