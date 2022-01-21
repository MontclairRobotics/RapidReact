package frc.robot.framework;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import edu.wpi.first.util.function.BooleanConsumer;
import frc.robot.framework.bases.ForeverCommand;
import frc.robot.framework.bases.OnceCommand;
import frc.robot.framework.bases.TimedCommand;
import frc.robot.framework.bases.WhenCommand;

public final class Commands 
{
    private Commands() {}
    
    // Static methods
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


    public static WhenCommand when(BooleanSupplier pred, Runnable onTrue)
    {
        return when(pred, $ -> onTrue.run());
    }
    public static WhenCommand when(BooleanSupplier pred, CommandRunnable<WhenCommand> onTrue)
    {
        return whenAndWhenNotAndBecomesTrueAndBecomesFalse(pred, onTrue, $ -> {}, $ -> {}, $ -> {});
    }

    public static WhenCommand whenBecomesTrue(BooleanSupplier pred, Runnable onBecomeTrue)
    {
        return whenBecomesTrue(pred, $ -> onBecomeTrue.run());
    }
    public static WhenCommand whenBecomesTrue(BooleanSupplier pred, CommandRunnable<WhenCommand> onBecomeTrue)
    {
        return whenAndWhenNotAndBecomesTrueAndBecomesFalse(pred, $ -> {}, $ -> {}, onBecomeTrue, $ -> {});
    }
    
    public static WhenCommand whenNot(BooleanSupplier pred, Runnable onFalse)
    {
        return whenNot(pred, $ -> onFalse.run());
    }
    public static WhenCommand whenNot(BooleanSupplier pred, CommandRunnable<WhenCommand> onFalse)
    {
        return whenAndWhenNotAndBecomesTrueAndBecomesFalse(pred, $ -> {}, onFalse, $ -> {}, $ -> {});
    }

    public static WhenCommand whenBecomesFalse(BooleanSupplier pred, Runnable onBecomeFalse)
    {
        return whenBecomesFalse(pred, $ -> onBecomeFalse.run());
    }
    public static WhenCommand whenBecomesFalse(BooleanSupplier pred, CommandRunnable<WhenCommand> onBecomeFalse)
    {
        return whenAndWhenNotAndBecomesTrueAndBecomesFalse(pred, $ -> {}, $ -> {}, $ -> {}, onBecomeFalse);
    }

    public static WhenCommand whenAndWhenNot(BooleanSupplier pred, Runnable onTrue, Runnable onFalse)
    {
        return whenAndWhenNot(pred, $ -> onTrue.run(), $ -> onFalse.run());
    }
    public static WhenCommand whenAndWhenNot(BooleanSupplier pred, CommandRunnable<WhenCommand> onTrue, CommandRunnable<WhenCommand> onFalse)
    {
        return whenAndWhenNotAndBecomesTrueAndBecomesFalse(pred, onTrue, onFalse, $ -> {}, $ -> {});
    }
    
    public static WhenCommand whenBecomesTrueAndBecomesFalse(BooleanSupplier pred, Runnable onBecomeTrue, Runnable onBecomeFalse)
    {
        return whenBecomesTrueAndBecomesFalse(pred, $ -> onBecomeTrue.run(), $ -> onBecomeFalse.run());
    }
    public static WhenCommand whenBecomesTrueAndBecomesFalse(BooleanSupplier pred, CommandRunnable<WhenCommand> onBecomeTrue, CommandRunnable<WhenCommand> onBecomeFalse)
    {
        return whenAndWhenNotAndBecomesTrueAndBecomesFalse(pred, $ -> {}, $ -> {}, onBecomeTrue, onBecomeFalse);
    }

    public static WhenCommand whenAndWhenNotAndBecomesTrueAndBecomesFalse(BooleanSupplier pred, Runnable onTrue, Runnable onFalse, Runnable onBecomeTrue, Runnable onBecomeFalse)
    {
        return whenAndWhenNotAndBecomesTrueAndBecomesFalse(pred, $ -> onTrue.run(), $ -> onFalse.run(), $ -> onBecomeTrue.run(), $ -> onBecomeFalse.run());
    }
    public static WhenCommand whenAndWhenNotAndBecomesTrueAndBecomesFalse(BooleanSupplier pred, CommandRunnable<WhenCommand> onTrue, CommandRunnable<WhenCommand> onFalse, CommandRunnable<WhenCommand> onBecomeTrue, CommandRunnable<WhenCommand> onBecomeFalse)
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

    public static TimedCommand forTime(double targetTimeLength, Runnable runnable)
    {
        return forTime(targetTimeLength, $ -> runnable.run());
    }
    public static TimedCommand forTime(double targetTimeLength, CommandRunnable<TimedCommand> runnable)
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

    public static TimedCommand forTime(double targetTimeLength, Runnable runnable, Runnable onEnd)
    {
        return forTime(targetTimeLength, $ -> runnable.run(), ($0, $1) -> onEnd.run());
    }
    public static TimedCommand forTime(double targetTimeLength, Runnable runnable, BooleanConsumer onEnd)
    {
        return forTime(targetTimeLength, $ -> runnable.run(), ($, wasCancelled) -> onEnd.accept(wasCancelled));
    }
    public static TimedCommand forTime(double targetTimeLength, CommandRunnable<TimedCommand> runnable, BiConsumer<TimedCommand, Boolean> onEnd)
    {
        return new TimedCommand(targetTimeLength)
        {
            @Override
            public void execute() 
            {
                runnable.run(this);
            }

            @Override
            public void onEnd(boolean wasCancelled) 
            {
                onEnd.accept(this, wasCancelled);
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
    public static Command raceGroup(Command... commands)
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

    public static TimedCommand wait(double time)
    {
        return forTime(time, () -> {});
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