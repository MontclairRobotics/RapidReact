package frc.robot.managers;

import frc.robot.RapidReact;
import frc.robot.framework.CommandRobot;
import frc.robot.framework.ManagerBase;
import frc.robot.framework.RobotState;
import frc.robot.subsystems.Drivetrain;

public class DrivetrainManager extends ManagerBase
{
    private final Drivetrain drivetrain;

    public DrivetrainManager(Drivetrain drivetrain)
    {
        this.drivetrain = drivetrain;
    }

    @Override
    public void periodic()
    {
        if(CommandRobot.getState() == RobotState.DISABLED)
        {
            return;
        }
        
        drivetrain.onUpdate();
    }
}
