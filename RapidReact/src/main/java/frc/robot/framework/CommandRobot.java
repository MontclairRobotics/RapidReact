package frc.robot.framework;

import edu.wpi.first.wpilibj.TimedRobot;

public class CommandRobot extends TimedRobot
{
    private CommandManager manager;

    public CommandRobot()
    {
        manager = new CommandManager();
    }

    public CommandManager getManager() 
    {
        return manager;
    }

    @Override
    public void robotPeriodic() 
    {
        manager.execute();
    }

    @Override
    public void autonomousInit() 
    {
        manager.switchTo(RobotState.AUTONOMOUS);
    }

    @Override
    public void teleopInit() 
    {
        manager.switchTo(RobotState.TELEOP);
    }

    @Override
    public void testInit() 
    {
        manager.switchTo(RobotState.TESTING);
    }

    @Override
    public void disabledInit() 
    {
        manager.switchTo(RobotState.DISABLED);
    }
}
