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

    public static <T extends CommandManager<T>> Command<T> untilSome(BooleanSupplier pred, Runnable runnable)
    {
        return untilSome(pred, $ -> runnable.run());
    }
    public static <T extends CommandManager<T>> Command<T> untilSome(BooleanSupplier pred, CommandRunnable<Command<T>> runnable)
    {
        return new Command<T>()
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

    public static <T extends CommandManager<T>> Command<T> whileSome(BooleanSupplier pred, Runnable runnable)
    {
        return whileSome(pred, $ -> runnable.run());
    }
    public static <T extends CommandManager<T>> Command<T> whileSome(BooleanSupplier pred, CommandRunnable<Command<T>> runnable)
    {
        return untilSome(() -> !pred.getAsBoolean(), runnable);
    }


    public static <T extends CommandManager<T>> ForeverCommand<T> forever(Runnable runnable)
    {
        return forever($ -> runnable.run());
    }
    public static <T extends CommandManager<T>> ForeverCommand<T> forever(CommandRunnable<ForeverCommand<T>> runnable)
    {
        return new ForeverCommand<T>()
        {
            @Override
            public void execute()
            {
                runnable.run(this);
            }
        };
    }


    public static <T extends CommandManager<T>> WhenCommand<T> whenSome(BooleanSupplier pred, Runnable onTrue)
    {
        return whenSome(pred, $ -> onTrue.run());
    }
    public static <T extends CommandManager<T>> WhenCommand<T> whenSome(BooleanSupplier pred, CommandRunnable<WhenCommand<T>> onTrue)
    {
        return whenSomeAndNotAndBecomesAndBecomesNot(pred, onTrue, $ -> {}, $ -> {}, $ -> {});
    }

    public static <T extends CommandManager<T>> WhenCommand<T> whenSomeBecomes(BooleanSupplier pred, Runnable onBecomeTrue)
    {
        return whenSomeBecomes(pred, $ -> onBecomeTrue.run());
    }
    public static <T extends CommandManager<T>> WhenCommand<T> whenSomeBecomes(BooleanSupplier pred, CommandRunnable<WhenCommand<T>> onBecomeTrue)
    {
        return whenSomeAndNotAndBecomesAndBecomesNot(pred, $ -> {}, $ -> {}, onBecomeTrue, $ -> {});
    }
    
    public static <T extends CommandManager<T>> WhenCommand<T> whenNotSome(BooleanSupplier pred, Runnable onFalse)
    {
        return whenNotSome(pred, $ -> onFalse.run());
    }
    public static <T extends CommandManager<T>> WhenCommand<T> whenNotSome(BooleanSupplier pred, CommandRunnable<WhenCommand<T>> onFalse)
    {
        return whenSomeAndNotAndBecomesAndBecomesNot(pred, $ -> {}, onFalse, $ -> {}, $ -> {});
    }

    public static <T extends CommandManager<T>> WhenCommand<T> whenSomeBecomesNot(BooleanSupplier pred, Runnable onBecomeFalse)
    {
        return whenSomeBecomesNot(pred, $ -> onBecomeFalse.run());
    }
    public static <T extends CommandManager<T>> WhenCommand<T> whenSomeBecomesNot(BooleanSupplier pred, CommandRunnable<WhenCommand<T>> onBecomeFalse)
    {
        return whenSomeAndNotAndBecomesAndBecomesNot(pred, $ -> {}, $ -> {}, $ -> {}, onBecomeFalse);
    }

    public static <T extends CommandManager<T>> WhenCommand<T> whenSomeAndNot(BooleanSupplier pred, Runnable onTrue, Runnable onFalse)
    {
        return whenSomeAndNot(pred, $ -> onTrue.run(), $ -> onFalse.run());
    }
    public static <T extends CommandManager<T>> WhenCommand<T> whenSomeAndNot(BooleanSupplier pred, CommandRunnable<WhenCommand<T>> onTrue, CommandRunnable<WhenCommand<T>> onFalse)
    {
        return whenSomeAndNotAndBecomesAndBecomesNot(pred, onTrue, onFalse, $ -> {}, $ -> {});
    }
    
    public static <T extends CommandManager<T>> WhenCommand<T> whenSomeBecomesAndBecomesNot(BooleanSupplier pred, Runnable onBecomeTrue, Runnable onBecomeFalse)
    {
        return whenSomeBecomesAndBecomesNot(pred, $ -> onBecomeTrue.run(), $ -> onBecomeFalse.run());
    }
    public static <T extends CommandManager<T>> WhenCommand<T> whenSomeBecomesAndBecomesNot(BooleanSupplier pred, CommandRunnable<WhenCommand<T>> onBecomeTrue, CommandRunnable<WhenCommand<T>> onBecomeFalse)
    {
        return whenSomeAndNotAndBecomesAndBecomesNot(pred, $ -> {}, $ -> {}, onBecomeTrue, onBecomeFalse);
    }

    public static <T extends CommandManager<T>> WhenCommand<T> whenSomeAndNotAndBecomesAndBecomesNot(BooleanSupplier pred, Runnable onTrue, Runnable onFalse, Runnable onBecomeTrue, Runnable onBecomeFalse)
    {
        return whenSomeAndNotAndBecomesAndBecomesNot(pred, $ -> onTrue.run(), $ -> onFalse.run(), $ -> onBecomeTrue.run(), $ -> onBecomeFalse.run());
    }
    public static <T extends CommandManager<T>> WhenCommand<T> whenSomeAndNotAndBecomesAndBecomesNot(BooleanSupplier pred, CommandRunnable<WhenCommand<T>> onTrue, CommandRunnable<WhenCommand<T>> onFalse, CommandRunnable<WhenCommand<T>> onBecomeTrue, CommandRunnable<WhenCommand<T>> onBecomeFalse)
    {
        return new WhenCommand<T>()
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

    public static <T extends CommandManager<T>> OnceCommand<T> once(Runnable runnable)
    {
        return once($ -> runnable.run());
    }
    public static <T extends CommandManager<T>> OnceCommand<T> once(CommandRunnable<OnceCommand<T>> runnable)
    {
        return new OnceCommand<T>()
        {
            @Override
            public void execute() 
            {
                runnable.run(this);
            }
        };
    }

    public static <T extends CommandManager<T>> TimedCommand<T> timed(double targetTimeLength, Runnable runnable)
    {
        return timed(targetTimeLength, $ -> runnable.run());
    }
    public static <T extends CommandManager<T>> TimedCommand<T> timed(double targetTimeLength, CommandRunnable<TimedCommand<T>> runnable)
    {
        return new TimedCommand<T>(targetTimeLength)
        {
            @Override
            public void execute() 
            {
                runnable.run(this);
            }
        };
    }

    public static <T extends CommandManager<T>> Command<T> start(Command<T> command)
    {
        return start(() -> command);
    }
    public static <T extends CommandManager<T>> Command<T> start(Supplier<Command<T>> commandGetter)
    {
        return new OnceCommand<T>()
        {
            @Override
            public void execute() 
            {
                getManager().start(commandGetter.get());
            }
        };
    }

    public static <T extends CommandManager<T>> Command<T> stop(Command<T> command)
    {
        return stop(() -> command);
    }
    public static <T extends CommandManager<T>> Command<T> stop(Supplier<Command<T>> commandGetter)
    {
        return new OnceCommand<T>()
        {
            @Override
            public void execute() 
            {
                getManager().stop(commandGetter.get());
            }
        };
    }

    @SafeVarargs
    public static <T extends CommandManager<T>> Command<T> series(Command<T>... commands)
    {
        return new Command<T>()
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
    public static <T extends CommandManager<T>> Command<T> group(Command<T>... commands)
    {
        return new Command<T>()
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
    public static <T extends CommandManager<T>> Command<T> race(Command<T>... commands)
    {
        return new Command<T>()
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

    public static <T extends CommandManager<T>> Command<T> debug(String msg)
    {
        return once(man -> man.getManager().debug(msg));
    }
    public static <T extends CommandManager<T>> Command<T> debug(Supplier<String> msgGetter)
    {
        return once(man -> man.getManager().debug(msgGetter.get()));
    }
}
