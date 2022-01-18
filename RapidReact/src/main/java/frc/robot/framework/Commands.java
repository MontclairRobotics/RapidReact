package frc.robot.framework;

import java.util.Arrays;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import frc.robot.framework.bases.ForeverCommand;
import frc.robot.framework.bases.OnceCommand;
import frc.robot.framework.bases.TimedCommand;
import frc.robot.framework.bases.WhenCommand;

public final class Commands 
{
    private Commands() {}

    public static Command untilSome(BooleanSupplier pred, Runnable runnable)
    {
        return untilSome(pred, $ -> runnable.run());
    }
    public static Command untilSome(BooleanSupplier pred, CommandRunnable<Command> runnable)
    {
        return new Command()
        {
            @Override
            public void execute() 
            {
                runnable.run(this);
            }

            @Override
            public boolean finished() 
            {
                return pred.getAsBoolean();
            }
        };
    }

    public static Command whileSome(BooleanSupplier pred, Runnable runnable)
    {
        return whileSome(pred, $ -> runnable.run());
    }
    public static Command whileSome(BooleanSupplier pred, CommandRunnable<Command> runnable)
    {
        return untilSome(() -> !pred.getAsBoolean(), runnable);
    }


    public static ForeverCommand forever(Runnable runnable)
    {
        return forever($ -> runnable.run());
    }
    public static ForeverCommand forever(CommandRunnable<ForeverCommand> runnable)
    {
        return new ForeverCommand()
        {
            @Override
            public void execute()
            {
                runnable.run(this);
            }
        };
    }


    public static WhenCommand whenSome(BooleanSupplier pred, Runnable onTrue)
    {
        return whenSome(pred, $ -> onTrue.run());
    }
    public static WhenCommand whenSome(BooleanSupplier pred, CommandRunnable<WhenCommand> onTrue)
    {
        return whenSomeAndNotAndBecomesAndBecomesNot(pred, onTrue, $ -> {}, $ -> {}, $ -> {});
    }

    public static WhenCommand whenSomeBecomes(BooleanSupplier pred, Runnable onBecomeTrue)
    {
        return whenSomeBecomes(pred, $ -> onBecomeTrue.run());
    }
    public static WhenCommand whenSomeBecomes(BooleanSupplier pred, CommandRunnable<WhenCommand> onBecomeTrue)
    {
        return whenSomeAndNotAndBecomesAndBecomesNot(pred, $ -> {}, $ -> {}, onBecomeTrue, $ -> {});
    }
    
    public static WhenCommand whenNotSome(BooleanSupplier pred, Runnable onFalse)
    {
        return whenNotSome(pred, $ -> onFalse.run());
    }
    public static WhenCommand whenNotSome(BooleanSupplier pred, CommandRunnable<WhenCommand> onFalse)
    {
        return whenSomeAndNotAndBecomesAndBecomesNot(pred, $ -> {}, onFalse, $ -> {}, $ -> {});
    }

    public static WhenCommand whenSomeBecomesNot(BooleanSupplier pred, Runnable onBecomeFalse)
    {
        return whenSomeBecomesNot(pred, $ -> onBecomeFalse.run());
    }
    public static WhenCommand whenSomeBecomesNot(BooleanSupplier pred, CommandRunnable<WhenCommand> onBecomeFalse)
    {
        return whenSomeAndNotAndBecomesAndBecomesNot(pred, $ -> {}, $ -> {}, $ -> {}, onBecomeFalse);
    }

    public static WhenCommand whenSomeAndNot(BooleanSupplier pred, Runnable onTrue, Runnable onFalse)
    {
        return whenSomeAndNot(pred, $ -> onTrue.run(), $ -> onFalse.run());
    }
    public static WhenCommand whenSomeAndNot(BooleanSupplier pred, CommandRunnable<WhenCommand> onTrue, CommandRunnable<WhenCommand> onFalse)
    {
        return whenSomeAndNotAndBecomesAndBecomesNot(pred, onTrue, onFalse, $ -> {}, $ -> {});
    }
    
    public static WhenCommand whenSomeBecomesAndBecomesNot(BooleanSupplier pred, Runnable onBecomeTrue, Runnable onBecomeFalse)
    {
        return whenSomeBecomesAndBecomesNot(pred, $ -> onBecomeTrue.run(), $ -> onBecomeFalse.run());
    }
    public static WhenCommand whenSomeBecomesAndBecomesNot(BooleanSupplier pred, CommandRunnable<WhenCommand> onBecomeTrue, CommandRunnable<WhenCommand> onBecomeFalse)
    {
        return whenSomeAndNotAndBecomesAndBecomesNot(pred, $ -> {}, $ -> {}, onBecomeTrue, onBecomeFalse);
    }

    public static WhenCommand whenSomeAndNotAndBecomesAndBecomesNot(BooleanSupplier pred, Runnable onTrue, Runnable onFalse, Runnable onBecomeTrue, Runnable onBecomeFalse)
    {
        return whenSomeAndNotAndBecomesAndBecomesNot(pred, $ -> onTrue.run(), $ -> onFalse.run(), $ -> onBecomeTrue.run(), $ -> onBecomeFalse.run());
    }
    public static WhenCommand whenSomeAndNotAndBecomesAndBecomesNot(BooleanSupplier pred, CommandRunnable<WhenCommand> onTrue, CommandRunnable<WhenCommand> onFalse, CommandRunnable<WhenCommand> onBecomeTrue, CommandRunnable<WhenCommand> onBecomeFalse)
    {
        return new WhenCommand()
        {
            @Override
            public boolean predicate() 
            {
                return pred.getAsBoolean();
            }

            @Override
            public void onTrue() 
            {
                onTrue.run(this);
            }

            @Override
            public void onFalse() 
            {
                onFalse.run(this);
            }

            @Override
            public void onBecomeTrue() 
            {
                onBecomeTrue.run(this);
            }

            @Override
            public void onBecomeFalse() 
            {
                onBecomeFalse.run(this);
            }
        };
    }

    public static OnceCommand once(Runnable runnable)
    {
        return once($ -> runnable.run());
    }
    public static OnceCommand once(CommandRunnable<OnceCommand> runnable)
    {
        return new OnceCommand()
        {
            @Override
            public void execute() 
            {
                runnable.run(this);
            }
        };
    }

    public static TimedCommand timed(double targetTimeLength, Runnable runnable)
    {
        return timed(targetTimeLength, $ -> runnable.run());
    }
    public static TimedCommand timed(double targetTimeLength, CommandRunnable<TimedCommand> runnable)
    {
        return new TimedCommand(targetTimeLength)
        {
            @Override
            public void execute() 
            {
                runnable.run(this);
            }
        };
    }


    public static Command start(Command command)
    {
        return start(() -> command);
    }
    public static Command start(Supplier<Command> commandGetter)
    {
        return new OnceCommand()
        {
            @Override
            public void execute() 
            {
                getManager().start(commandGetter.get());
            }
        };
    }

    public static Command stop(Command command)
    {
        return stop(() -> command);
    }
    public static Command stop(Supplier<Command> commandGetter)
    {
        return new OnceCommand()
        {
            @Override
            public void execute() 
            {
                getManager().stop(commandGetter.get());
            }
        };
    }

    @SafeVarargs
    public static Command series(Command... commands)
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
            public boolean remainDuringStateChange(RobotState originalState, RobotState newState) 
            {
                return Arrays.stream(commands).allMatch(c -> c.remainDuringStateChange(originalState, newState));
            }

            @Override
            public void onEnd(boolean wasCancelled) 
            {
                for(var cmd : commands)
                {
                    if(cmd.isRunning()) cmd.end(wasCancelled);
                }
            }
        };
    }

    @SafeVarargs
    public static Command group(Command... commands)
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
            public boolean remainDuringStateChange(RobotState originalState, RobotState newState) 
            {
                return Arrays.stream(commands).allMatch(c -> c.remainDuringStateChange(originalState, newState));
            }

            @Override
            public boolean finished() 
            {
                return Arrays.stream(commands).allMatch(c -> c.finished());
            }
        };
    }

    @SafeVarargs
    public static Command race(Command... commands)
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
            public boolean remainDuringStateChange(RobotState originalState, RobotState newState) 
            {
                return Arrays.stream(commands).allMatch(c -> c.remainDuringStateChange(originalState, newState));
            }

            @Override
            public boolean finished() 
            {
                return Arrays.stream(commands).anyMatch(c -> c.finished());
            }
        };
    }

    public static Command debug(String msg)
    {
        return once(man -> man.getManager().debug(msg));
    }
    public static Command debug(Supplier<String> msgGetter)
    {
        return once(man -> man.getManager().debug(msgGetter.get()));
    }
}
