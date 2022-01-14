package frc.robot.framework;

import java.util.Arrays;

import frc.robot.utils.ArrayUtils;

public class CommandGroup extends Command
{
    private final Command[] commands;

    public CommandGroup(Command first, Command... commands)
    {
        this.commands = new Command[commands.length + 1];
        ArrayUtils.appendFront(first, commands, this.commands);
    }

    @Override
    public void execute() 
    {
        for(var c: commands)
        {
            c.execute();
        }
    }

    @Override
    public boolean finished() 
    {
        return Arrays.stream(commands).allMatch(c -> c.finished());
    }

    @Override
    public Command[] getSubCommands() 
    {
        return commands;
    }
}