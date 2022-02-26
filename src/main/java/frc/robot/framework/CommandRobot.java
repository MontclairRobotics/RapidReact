package frc.robot.framework;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

public class CommandRobot extends TimedRobot
{
    public CommandRobot(CommandRobotContainer container)
    {
        CommandRobot.container = container;
        CommandRobot.robot = this;
    }

    private static long lastTime;
    private static CommandRobotContainer container;
    private static CommandRobot robot;

    public static double deltaTime()
    {
        return (System.currentTimeMillis() - lastTime) / 1000.0;
    }

    @Override
    public void robotInit() 
    {
        lastTime = System.currentTimeMillis() - (long)(TimedRobot.kDefaultPeriod * 1000);
    }

    @Override
    public void robotPeriodic() 
    {
        CommandScheduler.getInstance().run();
        lastTime = System.currentTimeMillis();
    }

    @Override
    public void autonomousInit() 
    {
        CommandScheduler.getInstance().cancelAll();
        var autoComm = container.getAutoCommand();

        if(autoComm != null)
        {
            CommandScheduler.getInstance().schedule(autoComm);
        }
    }

    @Override
    public void teleopInit() 
    {
        CommandScheduler.getInstance().cancelAll();
    }

    @Override
    public void teleopExit() 
    {
        CommandScheduler.getInstance().cancelAll();
    }

    public static Supplier<CommandRobot> from(Supplier<CommandRobotContainer> containerSupplier)
    {
        return () -> new CommandRobot(containerSupplier.get());
    }
}
