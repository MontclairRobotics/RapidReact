package frc.robot.framework;

import java.util.Arrays;
import java.util.function.BooleanSupplier;

import frc.robot.framework.bases.TimedCommand;

public final class Commands 
{
    private Commands() {}

    public static final Command untilSome(BooleanSupplier pred, Runnable runnable)
    {
        return new Command()
        {
            @Override
            public void execute() 
            {
                runnable.run();
            }

            @Override
            public boolean finished() 
            {
                return pred.getAsBoolean();
            }
        };
    }

    public static final Command whileSome(BooleanSupplier pred, Runnable runnable)
    {
        return untilSome(() -> !pred.getAsBoolean(), runnable);
    }

    public static final TimedCommand timed(double time, Runnable runnable)
    {
        return new TimedCommand(time)
        {
            @Override
            public void execute() 
            {
                runnable.run();
            }
        };
    }

    public static final Command series(Command... commands)
    {
        return new Command()
        {
            private int index = 0;

            @Override
            public void onInit() 
            {
                index = 0;
                commands[0].init(getManager());
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

            @Override
            public void onEnd(boolean wasCancelled) 
            {
                for(var cmd: commands)
                {
                    if(cmd.isRunning()) cmd.end(wasCancelled);
                }
            }
        };
    }

    public static final Command group(Command... commands)
    {
        return new Command()
        {
            @Override
            public void onInit() 
            {
                for(var cmd: commands)
                {
                    cmd.init(getManager());
                }
            }

            @Override
            public void execute() 
            {
                for(var cmd: commands)
                {
                    getManager().handleCommand(cmd);
                }
            }

            @Override
            public void onEnd(boolean wasCancelled) 
            {   
                for(var cmd: commands)
                {
                    if(cmd.isRunning())
                    {
                        cmd.end(wasCancelled);
                    }
                }
            }

            @Override
            public boolean finished() 
            {
                return Arrays.stream(commands).allMatch(c -> c.finished());
            }
        };
    }

    public static final Command race(Command... commands)
    {
        return new Command()
        {
            @Override
            public void onInit() 
            {
                for(var cmd: commands)
                {
                    cmd.init(getManager());
                }
            }

            @Override
            public void execute() 
            {
                for(var cmd: commands)
                {
                    getManager().handleCommand(cmd);
                }
            }

            @Override
            public void onEnd(boolean wasCancelled) 
            {   
                for(var cmd: commands)
                {
                    if(cmd.isRunning())
                    {
                        cmd.end(wasCancelled);
                    }
                }
            }

            @Override
            public boolean finished() 
            {
                return Arrays.stream(commands).anyMatch(c -> c.finished());
            }
        };
    }
}
