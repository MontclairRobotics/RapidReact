// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.framework.controllers.InputController;
import frc.robot.utilities.smoothing.LinearSmoother;
import frc.robot.utilities.smoothing.NullSmoother;
import frc.robot.utilities.smoothing.Smoother;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants 
{
    // Left motor port numbers
    public static final int LEFT_MOTOR_1_PORT = 1;
    public static final int LEFT_MOTOR_2_PORT = 2;
    public static final int LEFT_MOTOR_3_PORT = 3;

    // Right motor port numbers
    public static final int RIGHT_MOTOR_1_PORT = 4;
    public static final int RIGHT_MOTOR_2_PORT = 5;
    public static final int RIGHT_MOTOR_3_PORT = 6;

    // Intake motor port number
    public static final int INTAKE_MOTOR_PORT = 7;

    // Port for xbox controller
    public static final int DRIVER_CONTROLLER_PORT = 0;
    public static final int OPERATOR_CONTROLLER_PORT = 1;

    public static final InputController.Type DRIVER_CONTROLLER_TYPE = InputController.Type.PS4;
    public static final InputController.Type OPERATOR_CONTROLLER_TYPE = InputController.Type.PS4;

    // Speeds for the robot
    public static final double AUTO_SPEED = 0.7;
    public static final double[] ROBOT_SPEEDS = {
        0.7,
        0.4,
        1
    };

    // Intake Motor Speed
    public static final double BALL_INTAKE_SPEED = 0.5; //idk

    // Motors inversion
    public static final boolean LEFT_INVERTED = false;
    public static final boolean RIGHT_INVERTED = true;
    
    public static final Smoother DRIVE_SMOOTHER 
        = new LinearSmoother(0, -1, 1, 0.1);
    public static final NullSmoother DRIVE_NULL_SMOOTHER
        = new NullSmoother(0, -1, 1);

    public static final class PID 
    {
        public static final double KP = 1;
        public static final double KI = 0;
        public static final double KD = 0;

        public static final double TOLERANCE = 0.05;
    }
    public static final class AnglePID
    {
        public static final double DEADBAND = 0.1;

        public static final double KP = 1;
        public static final double KI = 0;
        public static final double KD = 0;

        public static final double TOLERANCE = 0.05;
    }
}