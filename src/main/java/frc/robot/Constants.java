// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import frc.robot.framework.frc.controllers.GameController;
import frc.robot.framework.profiling.LinearProfiler;
import frc.robot.framework.profiling.NothingProfiler;
import frc.robot.framework.profiling.Profiler;

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
    public static final int LEFT_MOTOR_2_PORT = 4;
    public static final int LEFT_MOTOR_3_PORT = 3; // this motor has ben commented out

    // Right motor port numbers
    public static final int RIGHT_MOTOR_1_PORT = 9; //2;
    public static final int RIGHT_MOTOR_2_PORT = 10; //3;
    public static final int RIGHT_MOTOR_3_PORT = 11; //4; this motor has been commented out

    // Intake motor port number
    public static final int INTAKE_MOTOR_PORT = 1;

    // Transport motor port number
    public static final int TRANSPORT_MOTOR_PORT = 40;
    
    // Shooter motor Port
    public static final int LEFT_SHOOTER_MOTOR_PORT = 7;
    public static final int RIGHT_SHOOTER_MOTOR_PORT = 5;

    // Climber Motor Ports
    public static final int LEFT_CLIMBER_MOTOR_PORT = 41;
    public static final int RIGHT_CLIMBER_MOTOR_PORT = 42;

    // Rotational Climber Motor Ports
    public static final int LEFT_ROTATIONAL_CLIMBER_MOTOR_PORT = 43;
    public static final int RIGHT_ROTATIONAL_CLIMBER_MOTOR_PORT = 44;

    // Climber limit switch ports
    public static final int LEFT_LOWER_CLIMBER_LIMIT_PORT = 0;
    public static final int RIGHT_LOWER_CLIMBER_LIMIT_PORT = 1;
    
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
    public static final double BALL_TRANSPORT_SPEED = 1; 

    //Shooter Speed
    public static final double SHOOTER_SPEED = 0.75;

    //Climber Speed
    public static final double CLIMBER_MOTOR_SPEED =  1; 
    public static final double REVERSE_CLIMBER_MOTOR_SPEED = -1;

    // Drivetrain Inversion
    public static final boolean LEFT_DRIVE_INVERSION = false;
    public static final boolean RIGHT_DRIVE_INVERSION = true;
    public static final double MIN_PID_TURN = 0.0002;

    // Shooter Inversion
    public static final boolean SHOOTER_LEFT_INVERSION = true;
    public static final boolean SHOOTER_RIGHT_INVERSION = false;

    // Climber inversion
    public static final boolean LEFT_CLIMBER_INVERTED = false;
    public static final boolean RIGHT_CLIMBER_INVERTED = false;

    // Rotational Climber Inversion
    public static final boolean LEFT_ROTATIONAL_CLIMBER_INVERTED = false;
    public static final boolean RIGHT_ROTATIONAL_CLIMBER_INVERTED = false;

    // Port for xbox controller
    public static final int DRIVER_CONTROLLER_PORT = 0;
    public static final int OPERATOR_CONTROLLER_PORT = 1;

    public static final GameController.Type DRIVER_CONTROLLER_TYPE = GameController.Type.PS4;
    public static final GameController.Type OPERATOR_CONTROLLER_TYPE = GameController.Type.PS4;

    public static final double REVERSE_SHOOTER_SPEED = -0.7;
    

    // Speeds for the robot
    public static final double AUTO_SPEED = 0.5;
    public static final double[] DRIVE_SPEEDS = {
        1,
        0.5
    };

    public static final Profiler DRIVE_PROFILER 
        = new LinearProfiler(0, -1, 1, 1.0 / 1.0, 1.0 / 1.25, "DRIVE-1");
    public static final Profiler NOTHING_PROFILER
        = new NothingProfiler(0, -1, 1, "NOTHING-1");

    public static final Profiler[] PROFILERS = {
        DRIVE_PROFILER,
        NOTHING_PROFILER
    };

    public static final double TURN_FACTOR = 0.5;
    public static final double TURN_DRIVE_FACTOR = 0.2;

    public static double adjustTurn(double speed, double targetTurn)
    {
        return (1 - Math.abs(TURN_DRIVE_FACTOR * speed)) * targetTurn * TURN_FACTOR;
    }

    // Rotational Climber Speed
    public static final double ROTATIONAL_CLIMBER_MOTOR_SPEED = 1;
    public static final double REVERSE_ROTATIONAL_CLIMBER_MOTOR_SPEED = -1;

    // drive train coversion rate in ticks per feet
    // TODO: make easier to change
    public static final double TICKS_PER_ROTATION = 42.0;
    public static final double GEAR_RATIO_IN_TO_OUT = 10.86 / 1.0;
    public static final double WHEEL_DIAMETER = 6.0; //inches
    public static final double WHEEL_CIRCUMFERENCE = Math.PI * WHEEL_DIAMETER;

    public static final double CONVERSION_RATE 
        = (WHEEL_DIAMETER /*in*/ * Math.PI /*r o*/) / /*r m*/ GEAR_RATIO_IN_TO_OUT;

    public static final double ANGLE_PID_DEADBAND = 0.1;
    public static final double ANGLE_VELOCITY_DEADBAND = 20.0 / 1.0;

	public static final double ANGLE_PID_SCALE = 0.7;
    
    /////////////////////////////////
    // AUTO
    /////////////////////////////////
    public static final double AUTO_DRIVE_DISTANCE = 96.0;
    public static final double AUTO_WAIT_TIME = 5;
}