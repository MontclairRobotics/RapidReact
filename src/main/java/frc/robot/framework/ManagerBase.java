package frc.robot.framework;

public abstract class ManagerBase implements Manager
{
    public ManagerBase()
    {
        CommandRobot.registerManager(this);
    }
}
