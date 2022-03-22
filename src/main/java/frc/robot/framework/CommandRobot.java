package frc.robot.framework;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.framework.wpilib.AutoCommands;

public class CommandRobot extends TimedRobot
{
    public CommandRobot(RobotContainer container)
    {
        CommandRobot.container = container;
    }

    private static long lastTime;
    private static RobotContainer container;
    private static Command autoCommand;
    private static SendableChooser<Command> autoChooser;
    private static RobotState state = RobotState.DISABLED;
    private static List<Manager> managers = new ArrayList<Manager>();

    public static void registerManager(Manager manager) 
    {
        managers.add(manager);
    }

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
        container.initialize();
        autoChooser = AutoCommands.chooser();
    }

    @Override
    public void robotPeriodic() 
    {
        for(var m : managers)
        {
            m.periodic();
        }
        
        CommandScheduler.getInstance().run();
        lastTime = System.currentTimeMillis();
    }

    @Override
    public void autonomousInit() 
    {
        //System.out.println("Auto Init");

        state = RobotState.AUTO;
        container.reset();

        autoCommand = autoChooser.getSelected();
        //System.out.println(autoCommand == null);

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

        container.reset();
        lastTime = System.currentTimeMillis() - (long)(TimedRobot.kDefaultPeriod * 1_000);
    }

    @Override
    public void testInit() 
    {
        container.reset();
    }

    @Override
    public void disabledInit() 
    {
        state = RobotState.DISABLED;
        if(autoCommand != null)
        {
            CommandScheduler.getInstance().cancel(autoCommand);
        }
    }

    public static Supplier<CommandRobot> from(Supplier<RobotContainer> containerSupplier)
    {
        return () -> new CommandRobot(containerSupplier.get());
    }
}
