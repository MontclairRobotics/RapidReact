package frc.robot;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.BallMover;
import frc.robot.subsystems.BallShooter;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.BallSucker;

import static edu.wpi.first.wpilibj2.command.CommandGroupBase.*;

import java.util.function.DoubleSupplier;

import static frc.robot.RapidReact.*;
import static frc.robot.framework.frc.commands.Commands.*;

public final class RapidReactCommands
{
    private RapidReactCommands() {}

    public static Command shootSequenceBuilder(double shootTime, double speed)
    {
        return race(
            sequence(
                instant(() -> {
                    ballMover.startMovingBackwards();
                    ballShooter.reverseShooting();
                    ballSucker.startSucking();
                }),
                waitFor(0.05),
                instant(() -> {
                    ballMover.stop();
                    ballSucker.stop();
                    ballShooter.startShooting(speed);
                }),
                waitFor(0.2),
                instant(() -> ballMover.startMoving()),
                waitFor(shootTime),
                instant(() -> {
                    ballMover.stop();
                    ballShooter.stop();
                })
            ),
            block(ballMover, ballShooter, ballSucker)
        );
    }

    public static Command shootSequence()
    {
        return shootSequenceBuilder(2, Constants.SHOOTER_SPEED);
    }

    public static Command shootSequenceShort()
    {
        return shootSequenceBuilder(1, Constants.SHOOTER_SPEED);
    }

    public static Command turn(double degrees)
    {
        return turn(() -> degrees);
    }
    public static Command turn(DoubleSupplier degrees)
    {
        return sequence(
            race(
                waitFor(3.5 + Math.abs(degrees.getAsDouble() / 180.0)), // fail safe in event of lock up
                sequence(
                    instant(() -> drivetrain.setTargetAngle(degrees.getAsDouble())),
                    waitUntil(drivetrain::reachedTargetAngle)
                )
            ),
            instant(drivetrain::releaseAngleTarget)
        );
    }

    public static Command driveForTime(double time, double percentOutput)
    {
        return sequence(
            sequence(
                instant(() -> {
                    drivetrain.setMaxOutput(Constants.AUTO_SPEED);
                    drivetrain.set(percentOutput, 0);
                    drivetrain.startStraightPidding();
                    //System.out.println("Start Driving");
                }),
                runForTime(time, block(drivetrain)),
                instant(() -> {
                    drivetrain.set(0, 0);
                    drivetrain.stop();
                    //**/drivetrain.stopStraightPidding();
                    //System.out.println("Stop Driving");
                })
            )
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
                drivetrain.releaseDistanceTarget();
                drivetrain.stop();
            })
        );
    }
}
