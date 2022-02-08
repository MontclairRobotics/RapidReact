// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

// Modified by Montclair Robotics Team 555

package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.framework.CommandManager;
import frc.robot.framework.Commands;
import frc.robot.framework.Order;
import frc.robot.framework.RobotState;
import frc.robot.framework.controllers.InputController;
import frc.robot.model.BallMover;
import frc.robot.model.BallSucker;
import frc.robot.model.Drivetrain;

import static frc.robot.Constants.*;
import static frc.robot.framework.controllers.InputController.Button.*;

import com.kauailabs.navx.frc.AHRS;

import static frc.robot.framework.controllers.InputController.Axis.*;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the
 * name of this class or
 * the package after creating this project, you must also update the
 * build.gradle file in the
 * project.
 */
public final class RapidReachManager extends CommandManager {
    ////////////////////////////////
    // CONTROLLERS
    ////////////////////////////////
    public static final InputController driverController = InputController.from(DRIVER_CONTROLLER_TYPE,
            DRIVER_CONTROLLER_PORT);
    public static final InputController operatorController = InputController.from(OPERATOR_CONTROLLER_TYPE,
            OPERATOR_CONTROLLER_PORT);

    ////////////////////////////////
    // MODELS
    ////////////////////////////////
    public static final AHRS navigator = new AHRS();
    public static final Drivetrain drivetrain = new Drivetrain(DRIVE_SMOOTHER, navigator);
    public static final BallSucker ballSucker = new BallSucker();
    public static final BallMover ballMover = new BallMover();

    public static int speedIndex = 0;

    ////////////////////////////////
    // INITIALIZATION
    ////////////////////////////////
    @Override
    public void init()
    {
        System.out.println("this guy");

        // Debug
        enableDebug();

        // Smart dashboard
        SmartDashboard.putString("Easing", "Drive");
        SmartDashboard.putNumber("Speed", ROBOT_SPEEDS[speedIndex]);

        ///////////////////////////////////////////////////
        // TELEOP
        ///////////////////////////////////////////////////

        // Calibrate the navx
        addStartupCommand(
            () -> navigator.calibrate()
        );

        // Intake command
        addCommand(
            // Commands
            Commands.pollToggle(
                () -> operatorController.getButton(B_CIRCLE),
                () -> ballSucker.setMotor(BALL_INTAKE_SPEED), 
                () -> ballSucker.setMotor(0.0)
            )
            .withOrder(Order.INPUT),
            // State
            RobotState.TELEOP
        );

        // Transport command
        addCommand(
            // Commands
            Commands.pollToggle(
                () -> operatorController.getButton(Y_TRIANGLE),
                () -> ballMover.setMotor(BALL_TRANSPORT_SPEED), 
                () -> ballMover.setMotor(0.0)
            )
            .withOrder(Order.INPUT),
            // State
            RobotState.TELEOP
        );

        // Max speed command
        addCommand(
            // Command
            Commands.pollActive(
                () -> driverController.getButtonPressed(A_CROSS), 
                () -> 
                {
                    // Loop through speeds
                    speedIndex++;
                    speedIndex %= ROBOT_SPEEDS.length;

                    // Set speed
                    drivetrain.setMaxOutput(ROBOT_SPEEDS[speedIndex]);
                    
                    // Display on smart dashboard
                    SmartDashboard.putNumber("Speed", ROBOT_SPEEDS[speedIndex]);
                }
            )
            .withOrder(Order.INPUT),
            // State
            RobotState.TESTING
        );

        // Ease control command
        addCommand(
            // Command
            Commands.pollToggle(
                () -> driverController.getAxis(LEFT_TRIGGER) > 0.5, 
                () -> 
                {
                    // Display on smart dashboard
                    SmartDashboard.putString("Easing", "None");

                    // Set smoother
                    drivetrain.setSmoother(DRIVE_NULL_SMOOTHER);
                },
                () -> 
                {
                    // Display on smart dashboard
                    SmartDashboard.putString("Easing", "Drive");
                    
                    // Set smoother
                    drivetrain.setSmoother(DRIVE_SMOOTHER);
                }
            )
            .withOrder(Order.INPUT),
            // State
            RobotState.TELEOP
        );

        // Drive command
        addCommand(
            // Command
            Commands.forever(
                () -> {
                    drivetrain.set(
                        -driverController.getAxis(LEFT_Y), 
                        driverController.getAxis(LEFT_X)
                    );
                }
            )
            .withOrder(Order.LATE_INPUT),
            // State
            RobotState.TELEOP
        );

        //PID straught angle command
        addCommand(
            // Command
            Commands.pollToggle (
                // If joystick angle is within the deadband, PID angle
                () -> Math.abs(driverController.getAxis(LEFT_Y)) <= AnglePID.DEADBAND,
                () -> 
                {
                    // PID the angle to 0
                    drivetrain.setTargetAngle(0.0);
                },
                () -> 
                {
                    // If not in deadband, stop PIDing angle
                    drivetrain.releaseAngleTarget();
                }
            )
            .withOrder(Order.INPUT),
            // State
            RobotState.TELEOP
        );

        ///////////////////////////////////////////////////
        // AUTONOMOUS
        ///////////////////////////////////////////////////
        
        // Basic auto command 
        
        addCommand(
            // Command
            Commands.series(
                Commands.forTime(
                    2.0, 
                    () -> 
                    {
                        drivetrain.setMaxOutput(AUTO_SPEED);
                        drivetrain.set(1, 0);
                    }, 
                    () -> drivetrain.stop()
                )
            )
            .withOrder(Order.END),
            // State
            RobotState.AUTONOMOUS
        );
        
        
        // Auto command with PID
        /*addCommand(
            // Command
            Commands.series(
                Commands.once(() -> drivetrain.setTargetDistance(3)),
                Commands.waitUntil(() -> drivetrain.reachedTargetDistance()),
                Commands.once(() -> drivetrain.releaseDistanceTarget())
            )
            .withOrder(Order.INPUT),
            // State
            RobotState.AUTONOMOUS
        );*/
       // addCommand(
            // Command
          //  Commands.forever(
          //          cmd -> {
          //              drivetrain.update(cmd.deltaTime());
          //          }
          //      )
         //   .withOrder(Order.BEGIN),
         //   // State
        //    RobotState.AUTONOMOUS
       // );

       addCommand(
            // Shooter Button
            Commands.pollToggle(
                () -> operatorController.getButton(Contants.SHOOTER_BUTTON),
                () -> ballShooter.shoot(), 
                () -> ballSucker.stop()
            )
            .withOrder(Order.INPUT),
            // State
            RobotState.OUTPUT
        );
       


        ///////////////////////////////////////////////////
        // DEFAULT
        ///////////////////////////////////////////////////

        // Update subsystems
        addAlwaysCommand(
            cmd -> drivetrain.update(cmd.deltaTime()),
            Order.OUTPUT
        );
    }
}
