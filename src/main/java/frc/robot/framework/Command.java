package frc.robot.framework;

public abstract class Command
{
    private CommandManager manager = null;
    private boolean running = false;

    private Order order = Order.EXECUTION;

    Order getOrder() {
        return order;
    }

    void init(CommandManager manager) 
    {
        this.manager = manager;
        running = true;

        onInit();
    }

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
}
