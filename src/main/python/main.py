#! /usr/bin/python3
from cv_utils import *
from data_structures import *
from constant import *
from networktables import *
import cv2

###################################
# NetworkTables
###################################
NetworkTables.initialize(server='roborio-555-frc.local')

dataTable = NetworkTables.getTable(NT_NAME)
smartDashboard = NetworkTables.getTable("SmartDashboard")

protoVerEntry = dataTable.getEntry(PROTO_VER_NAME)

xsEntry = dataTable.getEntry(XS_NAME)
ysEntry = dataTable.getEntry(YS_NAME)
anglesEntry = dataTable.getEntry(ANGLES_NAME)

currentTeamEntry = smartDashboard.getEntry(CURRENT_TEAM_NAME)

###################################
# Globals
###################################
videoCapture = cv2.VideoCapture(0)
width = int(videoCapture.get(cv2.CAP_PROP_FRAME_WIDTH))
height = int(videoCapture.get(cv2.CAP_PROP_FRAME_HEIGHT))


###################################
# Main Function
###################################
def update():

    _, frame = videoCapture.read()
    # For Video Version (Temp)
    #frame = cv2.resize(frame, (324, 576), interpolation=cv2.INTER_LINEAR)
    ##########################

    mask = None

    current_team = TeamColor[currentTeamEntry.getString("RED")]

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

    # Draw Dev (Temp)
    """
    output = cv2.bitwise_and(frame, frame, mask=mask)
    ContourInfo.draw_contours(
        output, contours, (255, 255, 255), 3
    )
    cv2.line(frame, (int(width / 2), 0), (int(width / 2), height), (0, 90, 255), 3)
    cv2.rectangle(frame, (0, 0), (130, 80), (255, 255, 255), -1)
    cat = cv2.hconcat([cv2.cvtColor(detectable, cv2.COLOR_GRAY2RGB), output])
    cv2.imshow(f"Color Mask : Team - {current_team.value}", cat)
    """

    # Update NetworkTables
    anglesEntry.setDoubleArray([(2 / width) * c.center[0] for c in contours])
    xsEntry.setDoubleArray([c.center[0] for c in contours])
    ysEntry.setDoubleArray([c.center[1] for c in contours])
    protoVerEntry.setString(CURRENT_PROTO_VER)


###################################
# Main Behaviour
###################################
if __name__ == '__main__':
    while True:
        update()
