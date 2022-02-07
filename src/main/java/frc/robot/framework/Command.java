package frc.robot.framework;

/** 
 * The basic unit of the command framework.
 * Can be run, and can control whether or not it has ended and should stop running.
 * Can be run again.
 * 
 * When started, the 'onInit()' method will be called.
 * When stopped, the 'onEnd()' method will be called
 * When running, the 'execute()' method will be called
 * To determine if the command has finished, the 'finished()' method will be called
 */
public abstract class Command
{
    /** The command manager in charge of this command. */
    private CommandManager manager = null;
    /** Whether or not the command is currently running. */
    private boolean running = false;

    /** The order of this command defined by the Order class. */
    private Order order = Order.EXECUTION;

    // Getter
    Order getOrder() {
        return order;
    }

    /** The internal method which starts a command on a given command manager */
    void init(CommandManager manager) 
    {
        this.manager = manager;
        running = true;

        onInit();
    }

    /** The internal method which ends a command */
    void end(boolean wasCancelled)
    {
        manager = null;
        running = false;

        onEnd(wasCancelled);
    }

    /** Runs every frame */
    public abstract void execute();
    /** Returns if the command is finished executing */
    public abstract boolean finished();

    /** Runs when the command is started */
    public void onInit() {}
    /** Runs when the command is finished or cancelled */
    public void onEnd(boolean wasCancelled) {}

    /** Returns whether this command should be killed when a state change occurs */
    public boolean remainDuringStateChange(RobotState originalState, RobotState newState) 
    {
        return false;
    }

    /** Returns whether this command is currently running */
    public final boolean isRunning()
    {
        return running;
    }
    /** Get the robot this command is running on */
    public final CommandManager getManager()
    {
        return manager;
    }
    /** Get an approximation for the time (in seconds) elapsed since the last execution. */
    public final double deltaTime()
    {
        return manager.deltaTime();
    }

    /** Set this command's priority */
    public final void setOrder(Order order)
    {
        this.order = order;
    }

    /** Set this command's priority and then return this command (fluent interface) */
    public final Command withOrder(Order order)
    {
        this.order = order;
        return this;
    }

    /** Debug a command */
    public final void debug(String msg)
    {
        getManager().debug(msg);
    }
}
