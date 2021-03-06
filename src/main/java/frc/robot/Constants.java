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
    public static final int LEFT_MOTOR_1_PORT = 9;
    public static final int LEFT_MOTOR_2_PORT = 10;
    public static final int LEFT_MOTOR_3_PORT = 11;

    // Right motor port numbers
    public static final int RIGHT_MOTOR_1_PORT = 2;
    public static final int RIGHT_MOTOR_2_PORT = 3;
    public static final int RIGHT_MOTOR_3_PORT = 4;

    // Intake motor port number
    public static final int INTAKE_MOTOR_PORT = 41;

    // Transport motor port number
    public static final int TRANSPORT_MOTOR_PORT = 40;
    
    // Shooter motor Port
    public static final int LEFT_SHOOTER_MOTOR_PORT = 7;
    public static final int RIGHT_SHOOTER_MOTOR_PORT = 5;

    // Climber Motor Ports
    public static final int LEFT_CLIMBER_MOTOR_PORT = 0;
    public static final int RIGHT_CLIMBER_MOTOR_PORT = 0;

    // BlinkinLEDDriver port
    public static final int BLINKIN_LED_DRIVER_PORT = 0;

    // Left Ultrasonic sensor ports
    public static final int LEFT_ULTRASONIC_SENSOR_PING_PORT = 1;
    public static final int LEFT_ULTRASONIC_SENSOR_ECHO_PORT = 2;
    
    // Right Ultrasonic sensor ports
    public static final int RIGHT_ULTRASONIC_SENSOR_PING_PORT = 1;
    public static final int RIGHT_ULTRASONIC_SENSOR_ECHO_PORT = 2;    

    // Ball Sucker motor speed
    public static final double BALL_SUCKER_MOTOR_SPEED = 1;

    // Transport Motor Speed
    public static final double BALL_TRANSPORT_SPEED = 1; //idk

    //Shooter Speed
    public static final double SHOOTER_SPEED = 0.7;

    //Climber Speed
    //256 ticks per rotation
    //20.25 to 1 ratio
    public static final double CLIMBER_MOTOR_SPEED = .5; //idk
    public static final double REVERSE_CLIMBER_MOTOR_SPEED = -.25; //idk

    // Drivetrain Inversion
    public static final boolean LEFT_DRIVE_INVERSION = true;
    public static final boolean RIGHT_DRIVE_INVERSION = false;

    // Shooter Inversion
    public static final boolean SHOOTER_LEFT_INVERSION = true;
    public static final boolean SHOOTER_RIGHT_INVERSION = false;

    // Motors inversion
    public static final boolean LEFT_CLIMBER_INVERTED = false;
    public static final boolean RIGHT_CLIMBER_INVERTED = true;

    // Port for xbox controller
    public static final int DRIVER_CONTROLLER_PORT = 0;
    public static final int OPERATOR_CONTROLLER_PORT = 1;

    public static final InputController.Type DRIVER_CONTROLLER_TYPE = InputController.Type.PS4;
    public static final InputController.Type OPERATOR_CONTROLLER_TYPE = InputController.Type.PS4;

    public static final double REVERSE_SHOOTER_SPEED = -0.4;

    // Speeds for the robot
    public static final double AUTO_SPEED = 0.7;
    public static final double[] ROBOT_SPEEDS = {
        0.7,
        1
    };

    public static final Smoother DRIVE_SMOOTHER 
        = new LinearSmoother(0, -1, 1, 0.02);
    public static final NullSmoother DRIVE_NULL_SMOOTHER
        = new NullSmoother(0, -1, 1);

    public static final double TURN_FACTOR = 0.6;
    public static final double TURN_DRIVE_FACTOR = 0.2;

    public static double adjustTurn(double speed, double targetTurn)
    {
        return (1 - Math.abs(TURN_DRIVE_FACTOR * speed)) * targetTurn * TURN_FACTOR;
    }

    // drive train coversion rate in ticks per feet
    // TODO: make easier to change
    public static final double TICKS_PER_ROT = 42.0;
    public static final double GEAR_RATIO = 10.86 / 1.0;
    public static final double WHEEL_DIAMETER = 6.0; //inches
    public static final double INCHES_TO_FEET = 12.0; //feet

    public static final double CONVERSION_RATE 
        = TICKS_PER_ROT * GEAR_RATIO / WHEEL_DIAMETER * INCHES_TO_FEET;



    //Button to activate shooter
    //public static final InputController SHOOTER_BUTTON = InputController.Button.A_CROSS;

    public static final double ANGLE_PID_DEADBAND = 0.1;
    public static final double ANGLE_VELOCITY_DEADBAND = 0.01;

    // Auto command types
    public static final String
        AUTO_DRIVE_TEST = "AUTO_DRIVE_TEST",
        AUTO_MAIN = "AUTO_MAIN"
    ;
}