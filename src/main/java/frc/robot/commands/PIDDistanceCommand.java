package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.subsystems.Drivetrain;

import static edu.wpi.first.wpilibj2.command.CommandGroupBase.*;
import static frc.robot.framework.Commands.*;

public final class PIDDistanceCommand
{
    private PIDDistanceCommand() {}

    public static Command get(Drivetrain drivetrain, double distance)
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
