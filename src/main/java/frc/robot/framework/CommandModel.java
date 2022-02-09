package frc.robot.framework;

public abstract class CommandModel 
{
    private CommandManager manager;
    public CommandManager getManager()
    {
        return manager;
    }

    public CommandModel(CommandManager manager)
    {
        this.manager = manager;
    }

    public final void debug(String msg)
    {
        manager.debug(msg);
    }
}
