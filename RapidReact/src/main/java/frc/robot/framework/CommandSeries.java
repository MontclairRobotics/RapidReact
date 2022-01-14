package frc.robot.framework;

import java.util.Arrays;

import frc.robot.utils.ArrayUtils;

public class CommandSeries extends Command
{
    private final Command[] commands;
    private int index;

    public CommandSeries(Command first, Command... commands)
    {
        this.commands = new Command[commands.length + 1];
        ArrayUtils.appendFront(first, commands, this.commands);
        
        index = 0;
    }

    @Override
    public void onInit() 
    {
        commands[0].onInit();
    }

    @Override
    public void execute() 
    {
        if(index >= commands.length) 
            return;
        
        commands[index].execute();

        if(commands[index].finished())
        {
            commands[index].onEnd(false);
            index++;

            if(index < commands.length)
            {
                commands[index].onInit();
            }
        }
    }

    @Override
    public boolean finished() 
    {
        return index >= commands.length;
    }
    
}