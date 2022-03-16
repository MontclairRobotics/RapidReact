import math
import cv2
import numpy as np


class ContourInfo:

    @staticmethod
    def find_contours(image, *args, selector=lambda c: True, skip_other_check=False, **kwargs):
        return [
            ContourInfo(c, image)
            for c in cv2.findContours(image, *args, **kwargs)[0]
        ]

    @staticmethod
    def draw_contours(image, contours, color, thickness, *args, **kwargs):
        cv2.drawContours(image, [c.contour for c in contours], -1, color, thickness, *args, **kwargs)
        for c in contours:
            cv2.circle(image, c.center, 1, color, thickness=thickness)

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

        # if not skip_other_check:
        #     inverse_mask = cv2.bitwise_not(self.mask)
        #     opposite = ContourInfo.find_contours(inverse_mask, mask=inverse_mask, skip_other_check=True)[0]
        #
        #     if not selector(self):
        #         self._copy_from(opposite)