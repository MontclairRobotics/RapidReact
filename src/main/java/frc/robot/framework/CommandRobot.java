package frc.robot.framework;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.button.Trigger;

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
    private static Command autoCommand;
    private static RobotState state = RobotState.DISABLED;

    public static RobotState getState() 
    {
        return state;
    }
    
    public static Trigger whenAuto()
    {
        return new Trigger(() -> state.equals(RobotState.AUTO));
    }
    public static Trigger whenTeleop()
    {
        return new Trigger(() -> state.equals(RobotState.TELEOP));
    }

    public static double deltaTime()
    {
        return (System.currentTimeMillis() - lastTime) / 1000.0;
    }

    @Override
    public void robotInit() 
    {
        container.initOnce();
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
        container.init();
        state = RobotState.AUTO;

        autoCommand = container.getAutoCommand();

        if(autoCommand != null)
        {
            CommandScheduler.getInstance().schedule(autoCommand);
        }

        lastTime = System.currentTimeMillis() - (long)(TimedRobot.kDefaultPeriod * 1_000);
    }

    @Override
    public void autonomousExit() 
    {
        if(autoCommand != null)
        {
            CommandScheduler.getInstance().cancel(autoCommand);
        }
    }

    @Override
    public void teleopInit() 
    {
        state = RobotState.TELEOP;

        if(autoCommand != null)
        {
            CommandScheduler.getInstance().cancel(autoCommand);
        }

        container.init();
        lastTime = System.currentTimeMillis() - (long)(TimedRobot.kDefaultPeriod * 1_000);
    }

    @Override
    public void testInit() 
    {
        container.init();
    }

    @Override
    public void disabledInit() 
    {
        state = RobotState.DISABLED;
    }

    public static Supplier<CommandRobot> from(Supplier<CommandRobotContainer> containerSupplier)
    {
        return () -> new CommandRobot(containerSupplier.get());
    }
}
