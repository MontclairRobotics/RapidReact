package frc.robot.framework;

public abstract class Command 
{
    private CommandManager commandManager;
    private boolean running;

    void init(CommandManager commandManager) 
    {
        this.commandManager = commandManager;
        running = true;

        onInit();
    }

    void end(boolean wasCancelled)
    {
        this.commandManager = null;
        running = false;

        onEnd(wasCancelled);
    }

    /** Get the command manager this command is running on */
    public CommandManager getManager()
    {
        return commandManager;
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
}
