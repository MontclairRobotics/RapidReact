package frc.robot.framework;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import edu.wpi.first.util.function.BooleanConsumer;
import frc.robot.framework.bases.ForeverCommand;
import frc.robot.framework.bases.OnceCommand;
import frc.robot.framework.bases.TimedCommand;
import frc.robot.framework.bases.PollCommand;

/** 
 * A utility class which provides methods to create commands from lambdas.
 */
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


    public static PollCommand pollActive(BooleanSupplier pred, Runnable onTrue)
    {
        return pollActive(pred, $ -> onTrue.run());
    }
    public static PollCommand pollActive(BooleanSupplier pred, CommandRunnable<PollCommand> onTrue)
    {
        return pollActiveAndToggle(pred, onTrue, $ -> {}, $ -> {}, $ -> {});
    }

    public static PollCommand pollToggle(BooleanSupplier pred, Runnable onBecomeTrue)
    {
        return pollToggle(pred, $ -> onBecomeTrue.run());
    }
    public static PollCommand pollToggle(BooleanSupplier pred, CommandRunnable<PollCommand> onBecomeTrue)
    {
        return pollActiveAndToggle(pred, $ -> {}, $ -> {}, onBecomeTrue, $ -> {});
    }

    public static PollCommand pollActive(BooleanSupplier pred, Runnable onTrue, Runnable onFalse)
    {
        return pollActive(pred, $ -> onTrue.run(), $ -> onFalse.run());
    }
    public static PollCommand pollActive(BooleanSupplier pred, CommandRunnable<PollCommand> onTrue, CommandRunnable<PollCommand> onFalse)
    {
        return pollActiveAndToggle(pred, onTrue, onFalse, $ -> {}, $ -> {});
    }
    
    public static PollCommand pollToggle(BooleanSupplier pred, Runnable onBecomeTrue, Runnable onBecomeFalse)
    {
        return pollToggle(pred, $ -> onBecomeTrue.run(), $ -> onBecomeFalse.run());
    }
    public static PollCommand pollToggle(BooleanSupplier pred, CommandRunnable<PollCommand> onBecomeTrue, CommandRunnable<PollCommand> onBecomeFalse)
    {
        return pollActiveAndToggle(pred, $ -> {}, $ -> {}, onBecomeTrue, onBecomeFalse);
    }

    public static PollCommand pollActiveAndToggle(BooleanSupplier pred, Runnable onTrue, Runnable onFalse, Runnable onBecomeTrue, Runnable onBecomeFalse)
    {
        return pollActiveAndToggle(pred, $ -> onTrue.run(), $ -> onFalse.run(), $ -> onBecomeTrue.run(), $ -> onBecomeFalse.run());
    }
    public static PollCommand pollActiveAndToggle(BooleanSupplier pred, CommandRunnable<PollCommand> onTrue, CommandRunnable<PollCommand> onFalse, CommandRunnable<PollCommand> onBecomeTrue, CommandRunnable<PollCommand> onBecomeFalse)
    {
        return new PollCommand()
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


    public static Command doWait(Runnable runnable, double targetTimeLength)
    {
        return series(
            once(runnable),
            waitForTime(targetTimeLength)
        );
    }
    public static Command doWaitDo(Runnable runnable, double targetTimeLength, Runnable end)
    {
        return series(
            once(runnable),
            waitForTime(targetTimeLength),
            once(end)
        );
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

                while(commands[index].finished())
                {
                    commands[index].onEnd(false);
                    index++;

                    if(index >= commands.length)
                    {
                        break;
                    }

                    commands[index].onInit();
                    commands[index].execute();
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

    public static Command select(Supplier<Command> splitter)
    {
        return new Command() 
        {
            private Command inner;

            @Override
            public void onInit() 
            {
                inner = splitter.get();
                inner.onInit();
            }

            @Override
            public void onEnd(boolean wasCancelled) 
            {
                inner.onEnd(wasCancelled);
            }

            @Override
            public boolean finished() {
                return inner.finished();
            }

            @Override
            public boolean remainDuringStateChange(RobotState originalState, RobotState newState) {
                return inner.remainDuringStateChange(originalState, newState);            
            }

            @Override
            public void execute() {
                inner.execute();
            }
        };
    }

    public static TimedCommand waitForTime(double time)
    {
        return forTime(time, () -> {});
    }
    public static Command waitUntil(BooleanSupplier pred)
    {
        return untilSome(pred, () -> {});
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