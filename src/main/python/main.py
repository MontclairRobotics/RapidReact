#! /usr/bin/python3
import time
import math
import cv2

import numpy as np

from networktables import *
from cscore import *

from enum import StrEnum


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
# region # Classes
########################
class TeamColor(StrEnum):
    RED = "Red"
    BLUE = "Blue"


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

    ###################################
    # NetworkTables
    ###################################
    data_table = NetworkTables.getTable('Vision')
    smart_dashboard = NetworkTables.getTable('SmartDashboard')

    proto_ver_entry = data_table.getEntry('__ver')

    xs_entry = data_table.getEntry('Xs')
    ys_entry = data_table.getEntry('Ys')
    angles_entry = data_table.getEntry('Angles')

    current_team_entry = smart_dashboard.getEntry('CurrentTeam')

    ###################################
    # Capture data
    ###################################
    CameraServer.startAutomaticCapture()

    input_stream = CameraServer.getVideo()
    width = int(input_stream.get(cv2.CAP_PROP_FRAME_WIDTH))
    height = int(input_stream.get(cv2.CAP_PROP_FRAME_HEIGHT))

    output_stream = CameraServer.putVideo('Debug', width * 2, height)

    ###################################
    # Main Function
    ###################################
    while True:

        # Get camera data
        frame_time, frame = input_stream.grabFrame()

        if frame_time == 0:
            time.sleep(0.01)
            continue

        current_team = TeamColor[current_team_entry.getString("RED")]

        # Preprocessing
        if current_team == TeamColor.RED:
            convert = cv2.cvtColor(frame, cv2.COLOR_RGB2HSV)
            mask = cv2.inRange(convert, LOW_RED, HIGH_RED)
        else:
            convert = cv2.cvtColor(frame, cv2.COLOR_BGR2HSV)
            mask = cv2.inRange(convert, LOW_BLUE, HIGH_BLUE)

        detectable = cv2.blur(mask, (20, 20))
        _, detectable = cv2.threshold(detectable, 100, 255, cv2.THRESH_BINARY_INV)
        detectable_bordered = cv2.copyMakeBorder(detectable, 1, 1, 1, 1, cv2.BORDER_CONSTANT, value=255)

        # Get contours
        contours = [
            c for c in ContourInfo.find_contours(
                detectable_bordered, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE
            )
            if width * height * 0.9 > c.area > width * height * 0.01 and c.circularity > 0.8 and c.mean[0] < 127
        ]

        # Draw into debug output stream
        output = cv2.bitwise_and(frame, frame, mask=mask)
        ContourInfo.draw_contours(
            output, contours, CONTOUR_DRAW_COL, CONTOUR_DRAW_WIDTH, CONTOUR_DRAW_CROSS_SIZE
        )
        cat = cv2.hconcat([cv2.cvtColor(detectable, cv2.COLOR_GRAY2RGB), output])

        output_stream.putFrame(cat)

        # Update NetworkTables
        angles_entry.setDoubleArray([(2 / width) * c.center[0] for c in contours])
        xs_entry.setDoubleArray([c.center[0] for c in contours])
        ys_entry.setDoubleArray([c.center[1] for c in contours])
        proto_ver_entry.setString(VERSION)
