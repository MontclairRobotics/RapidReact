from enum import Enum
import numpy as np


class TeamColor(Enum):
    RED = "Red"
    BLUE = "Blue"


# Window Size (DO NOT CHANGE)
SCREEN_WIDTH = 640
SCREEN_HEIGHT = 640

# Network Tables
CURRENT_PROTO_VER = "0.5.0"

NT_NAME = "Vision"

PROTO_VER_NAME = "__VER"
CURRENT_TEAM_NAME = "CURRENT_TEAM"

ANGLES_NAME = "ANGLES"
XS_NAME = "XS"
YS_NAME = "YS"


FOV = 69

# Ball Colors (HSV)
LOW_BLUE = (190 / 2, 100, 50)
HIGH_BLUE = (220 / 2, 255, 240)

LOW_RED = (115, 50, 100)
HIGH_RED = (160, 255, 255)

# Balls in Line of Intake
INTAKE_OFFSET = 45

MAX_CIRCLES = 20


def avg(value: list):
    return sum(value)/len(value)
