package frc.robot.framework;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj.TimedRobot;

/**
 * The base class for all command-based robots.
 * The generic parameter must be the same as the class which derives from this class/
 * @param T  The type of the derived class.
 */
public final class CommandRobot<M extends CommandManager> extends TimedRobot
{
    private M manager;

    private CommandRobot(M manager)
    {
        this.manager = manager;
    }

    public static <M extends CommandManager> CommandRobot<M> create(Supplier<M> managerSupplier)
    {
        return new CommandRobot<M>(managerSupplier.get());
    }
    public static <M extends CommandManager> Supplier<CommandRobot<M>> creator(Supplier<M> managerSupplier)
    {
        return () -> create(managerSupplier);
    }

    @Override
    public void robotPeriodic()
    {
        manager.execute();
    }

    @Override
    public void autonomousInit() 
    {
        manager.changeState(RobotState.AUTONOMOUS);
    }

    @Override
    public void teleopInit() 
    {
        manager.changeState(RobotState.TELEOP);
    }

    @Override
    public void testInit() 
    {
        manager.changeState(RobotState.TESTING);
    }

    @Override
    public void disabledInit() 
    {
        manager.changeState(RobotState.NONE);
    }
}
