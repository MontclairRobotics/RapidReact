package frc.robot.framework;

import edu.wpi.first.wpilibj2.command.Command;

public abstract class CommandRobotContainer 
{
    public abstract Command getAutoCommand();
    public abstract void init();
}
