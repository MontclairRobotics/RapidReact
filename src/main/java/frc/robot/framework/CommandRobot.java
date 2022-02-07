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
    /** The command manager for this robot. */
    private M manager;

    // Constructor
    private CommandRobot(M manager)
    {
        this.manager = manager;
    }

    /** Create this command robot given a provider for the command manager. */
    public static <M extends CommandManager> CommandRobot<M> create(Supplier<M> managerSupplier)
    {
        return new CommandRobot<M>(managerSupplier.get());
    }
    /** Get a creator for this command robot given a provider for the command manager. */
    public static <M extends CommandManager> Supplier<CommandRobot<M>> creator(Supplier<M> managerSupplier)
    {
        return () -> create(managerSupplier);
    }

    @Override
    public final void robotInit() 
    {
        manager.initDeltaTime();
    }

    @Override
    public final void robotPeriodic()
    {
        manager.execute();
    }

    @Override
    public final void autonomousInit() 
    {
        manager.changeState(RobotState.AUTONOMOUS);
    }

    @Override
    public final void autonomousPeriodic() {
        //DO NOTHING
    }

    @Override
    public final void teleopInit() 
    {
        manager.changeState(RobotState.TELEOP);
    }

    @Override
    public final void teleopPeriodic() {
        //DO NOTHING
    }

    @Override
    public final void testInit() {
        manager.changeState(RobotState.TESTING);
    }

    @Override
    public final void testPeriodic() {
        //DO NOTHING
    }

    @Override
    public final void disabledInit() {
        manager.changeState(RobotState.NONE);
    }

    @Override
    public final void disabledPeriodic() {
        //DO NOTHING
    }
}
