package frc.robot.framework;

public abstract class Command 
{
    CommandManager commandManager;
    /** Get the command manager this command is running on */
    public CommandManager getCommandManager()
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

    /** Returns the subcommands managed by this command */
    public Command[] getSubCommands()
    {
        return new Command[] {};
    }
}
