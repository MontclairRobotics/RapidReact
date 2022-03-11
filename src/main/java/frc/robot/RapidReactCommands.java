package frc.robot;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.BallMover;
import frc.robot.subsystems.BallShooter;
import frc.robot.subsystems.Drivetrain;

import static edu.wpi.first.wpilibj2.command.CommandGroupBase.*;
import static frc.robot.framework.Commands.*;

import static frc.robot.RapidReact.*;

public final class RapidReactCommands
{
    private RapidReactCommands() {}

    public static Command shootSequence()
    {
        return sequence(
            instant(() -> {
                ballMover.startMovingBackwards();
                ballShooter.reverseShooting();
            }),
            runForTime(0.1, block(ballMover, ballShooter)),
            instant(() -> {
                ballMover.stop();
                ballShooter.startShooting();
            }),
            runForTime(0.3, block(ballShooter)),
            instant(() -> ballMover.startMoving()),
            runForTime(2.5, block(ballMover, ballShooter)),
            instant(() -> {
                ballMover.stop();
                ballShooter.stop();
            })
        );
    }

    public static Command turn(double degrees)
    {
        return sequence(
            instant(() -> drivetrain.setTargetAngle(degrees)),
            runUntil(drivetrain::reachedTargetAngle, block(drivetrain)),
            instant(() -> drivetrain.releaseAngleTarget())
        );
    }

    public static Command driveForTime(double time, double percentOutput)
    {
        return sequence(
            instant(() -> {
                drivetrain.setMaxOutput(Constants.AUTO_SPEED);
                drivetrain.set(percentOutput, 0);
                drivetrain.startStraightPidding();
            }),
            runForTime(time, block(drivetrain)),
            instant(() -> {
                drivetrain.stop();
                drivetrain.stopStraightPidding();
            })
        );
    }

    public static Command driveDistance(double distance)
    {
        return sequence(
            instant(() -> {
                drivetrain.setMaxOutput(Constants.AUTO_SPEED);
                drivetrain.setTargetDistance(distance);
                drivetrain.startStraightPidding();
            }),
            runUntil(() -> drivetrain.reachedTargetDistance(), block(drivetrain)),
            instant(() -> {
                drivetrain.stop();
                drivetrain.releaseDistanceTarget();
                drivetrain.stopStraightPidding();
            })
        );
    }
}
