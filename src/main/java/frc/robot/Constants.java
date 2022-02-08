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
    public static final int LEFT_MOTOR_1_PORT = 2;
    public static final int LEFT_MOTOR_2_PORT = 3;
    public static final int LEFT_MOTOR_3_PORT = 5;

    // Right motor port numbers
    public static final int RIGHT_MOTOR_1_PORT = 4;
    public static final int RIGHT_MOTOR_2_PORT = 6;
    public static final int RIGHT_MOTOR_3_PORT = 1;

    // Intake motor port number
    public static final int INTAKE_MOTOR_PORT = 7;

    // Transport motor port number
    public static final int TRANSPORT_MOTOR_PORT = 8;

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

    // Transport Motor Speed
    public static final double BALL_TRANSPORT_SPEED = 0.5; //idk

    // Motors inversion
    public static final boolean LEFT_INVERTED = false;
    public static final boolean RIGHT_INVERTED = true;
    
    public static final Smoother DRIVE_SMOOTHER 
        = new LinearSmoother(0, -1, 1, 0.333);
    public static final NullSmoother DRIVE_NULL_SMOOTHER
        = new NullSmoother(0, -1, 1);

    public static final double TURN_FACTOR = 0.7;
    public static final double TURN_DRIVE_FACTOR = 0.5;

    public static double adjustTurn(double speed, double targetTurn)
    {
        return (1 - TURN_DRIVE_FACTOR * speed) * targetTurn * TURN_FACTOR;
    }

    // Constants for drive pid
    public static final class PID 
    {
        public static final double KP = 0.1;
        public static final double KI = 0;
        public static final double KD = 0;

        public static final double TOLERANCE = 0.05;
    }
    // Constants for angle pid
    public static final class AnglePID
    {
        public static final double DEADBAND = 0.1;

        public static final double KP = 1;
        public static final double KI = 0;
        public static final double KD = 0;

        public static final double TOLERANCE = 0.05;
    }

    // drive train coversion rate in ticks per feet
    // TODO: make easier to change
    public static final double TICKS_PER_ROT = 42.0;
    public static final double GEAR_RATIO = 10.86 / 1.0;
    public static final double WHEEL_DIAM = 6.0; //inches
    public static final double IN_TO_FT = 12.0; //feet

    public static final double CONVERSION_RATE 
        = TICKS_PER_ROT * GEAR_RATIO / WHEEL_DIAM * IN_TO_FT;

    // Shooter Inversion
    public static final boolean SHOOTER_LEFT_INVERSION = true;
    public static final boolean SHOOTER_RIGHT_INVERSION = false;

    //Shooter Speed
    public static final int SHOOTER_SPEED = 100;

    //Shooter Port
    public static final int LEFT_SHOOTER_MOTOR_PORT = 9;
    public static final int RIGHT_SHOOTER_MOTOR_PORT = 10;

    //Button to activate shooter
    //public static final InputController SHOOTER_BUTTON = InputController.Button.A_CROSS;

    }

