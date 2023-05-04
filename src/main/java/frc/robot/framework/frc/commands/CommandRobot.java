package frc.robot.framework.frc.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.framework.RobotState;
import frc.robot.framework.frc.AutoCommands;

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
    private static List<Manager> managers = new ArrayList<Manager>();

    public static void registerManager(Manager manager) 
    {
        managers.add(manager);
    }

    public static void unregisterManager(Manager manager)
    {
        managers.remove(manager);
    }

    public static double deltaTime()
    {
        return (System.currentTimeMillis() - lastTime) / 1000.0;
    }

    @Override
    public void robotInit() 
    {
        container.initialize();
        for(var m : managers)
        {
            m.initialize();
        }

        autoChooser = AutoCommands.chooser();
    }

    @Override
    public void robotPeriodic() 
    {   
        CommandScheduler.getInstance().run();
        for(var m : managers)
        {
            ManagedSubsystemBase msb;

            if(m instanceof ManagedSubsystemBase)
            {
                msb = (ManagedSubsystemBase)m;
            }
            else
            {
                continue;
            }

            if(ManagedSubsystemBase.subsystemHasCommand(msb))
            {
                msb.whenActive();
            }
            else
            {
                msb.whenInactive();
            }
        }
        for(var m : managers)
        {
            m.always();
        }

        lastTime = System.currentTimeMillis();
    }

    @Override
    public void autonomousInit() 
    {
        reset();

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
        reset();

        lastTime = System.currentTimeMillis() - (long)(TimedRobot.kDefaultPeriod * 1_000);
    }

    @Override
    public void testInit() 
    {
        reset();
    }

    @Override
    public void disabledInit() 
    {
        reset();
    }

    private void reset()
    {
        container.reset();
        for(var m : managers)
        {
            m.reset();
        }
    }

    public static Supplier<CommandRobot> of(Supplier<RobotContainer> containerSupplier)
    {
        return () -> new CommandRobot(containerSupplier.get());
    }
}
