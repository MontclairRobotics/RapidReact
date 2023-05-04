package frc.robot;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import static frc.robot.framework.Commands.*;
import edu.wpi.first.wpilibj2.command.*;
import static edu.wpi.first.wpilibj2.command.CommandBase.*;

import frc.robot.framework.ScoreHeights;

public class Commands555 {

    public Command Shoot(ScoreHeights height) {
        return Commands.sequence(
                Commands.runOnce(() -> {
                    RapidReact.ballShooter.setHeight(height);
                }),
                Commands.runOnce(() -> {
                    RapidReact.ballMover.startMovingBackwards();
                    RapidReact.ballShooter.reverseShooting();
                }),
                runForTime(0.25, block(RapidReact.ballMover, RapidReact.ballShooter)),
                Commands.runOnce(() -> {
                    RapidReact.ballMover.stop();
                    RapidReact.ballShooter.startShooting();
                }),
                runForTime(0.5, block(RapidReact.ballShooter)),
                instant(() -> RapidReact.ballMover.startMoving()),
                runForTime(2.5, block(RapidReact.ballMover, RapidReact.ballShooter)),
                instant(() -> {
                    RapidReact.ballMover.stop();
                    RapidReact.ballShooter.stop();
                }
            )
        );
    }
}
