package frc.robot.framework;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

import javax.naming.spi.StateFactory;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.TimedRobot;
import frc.robot.framework.bases.ForeverCommand;
import frc.robot.framework.bases.OnceCommand;

import java.util.HashMap;
import java.time.Instant;
import java.util.ArrayList;

/**
 * The class responsible for managing and running commands.
 * Should be inherited by command managers.
 */
public abstract class CommandManager
{
    //////////////////////////
    // Data structures
    //////////////////////////

    /** The set of commands that are currently running, sorted by order. */
    private final SortedSet<Command> activeCommands;

    /** The set of commands that are added whenever their state begins. */
    private final Map<RobotState, ArrayList<Command>> stateCommands;
    /** The set of commands that are added when any state begins. */
    private final Set<Command> defaultCommands;
    /** The set of commands that are added on startup (change from RobotState.NONE). */
    private final Set<Command> startupCommands;

    /** The current state of the command manager. */
    private RobotState currentState;
    /** The last time which this manager updated. */
    private long lastUpdateTime;

    // Constructor
    public CommandManager()
    {
        activeCommands = new ConcurrentSkipListSet<Command>((a, b) -> b.getOrder().compareTo(a.getOrder()));
        stateCommands = new HashMap<>();
        defaultCommands = new HashSet<>();
        startupCommands = new HashSet<>();

        currentState = RobotState.NONE;

        lastUpdateTime = -1;

        init();
    }

    /** Initializes this instance */
    public abstract void init();

    /**
     * Get the current state of this robot manager.
     */
    public final RobotState getCurrentState()
    {
        return currentState;
    }

    /**
     * A fluent interface for adding a command to the command 
     * The command added will run when the given state is made the current state.
     * @param command  The command to add
     * @param newState The state when the command will run
     */
    public final void addCommand(Command command, RobotState newState)
    {
        if(stateCommands.containsKey(newState))
        {
            stateCommands.get(newState).add(command);
        }
        else
        {
            var newCommands = new ArrayList<Command>();
            newCommands.add(command);

            stateCommands.put(newState, newCommands);
        }
    }

    /**
     * A fluent interface for adding a command to the command 
     * The command added will run when any state is changed.
     * @param command  The command to add
     */
    public final void addDefaultCommand(Command command)
    {
        defaultCommands.add(command);
    }

    /** A fluent interface for adding some action to run always, in every state. */
    public final void addAlwaysCommand(Runnable runnable)
    {
        addStartupCommand(Commands.forever(runnable));
    }
    /** A fluent interface for adding some action to run always, in every state. */
    public final void addAlwaysCommand(Runnable runnable, Order order)
    {
        addStartupCommand(Commands.forever(runnable).withOrder(order));
    }
    /** A fluent interface for adding some action to run always, in every state. */
    public final void addAlwaysCommand(CommandRunnable<ForeverCommand> runnable)
    {
        addStartupCommand(Commands.forever(runnable));
    }
    /** A fluent interface for adding some action to run always, in every state. */
    public final void addAlwaysCommand(CommandRunnable<ForeverCommand> runnable, Order order)
    {
        addStartupCommand(Commands.forever(runnable).withOrder(order));
    }

    /** A fluent interface for adding some action to run when the robot starts. */
    public final void addStartupCommand(Command command)
    {
        startupCommands.add(command);
    }
    /** A fluent interface for adding some action to run when the robot starts. */
    public final void addStartupCommand(Runnable runnable)
    {
        addStartupCommand(Commands.once(runnable));
    }
    /** A fluent interface for adding some action to run when the robot starts. */
    public final void addStartupCommand(Runnable runnable, Order order)
    {
        addStartupCommand(Commands.once(runnable).withOrder(order));
    }
    /** A fluent interface for adding some action to run when the robot starts. */
    public final void addStartupCommand(CommandRunnable<OnceCommand> runnable)
    {
        addStartupCommand(Commands.once(runnable));
    }
    /** A fluent interface for adding some action to run when the robot starts. */
    public final void addStartupCommand(CommandRunnable<OnceCommand> runnable, Order order)
    {
        addStartupCommand(Commands.once(runnable).withOrder(order));
    }

    /**
     * Start a new command.
     * If the command is already running, restart it.
     * If the command belongs to another command manager, do nothing.
     * @param command The command to start
     */
    public final void start(Command command)
    {
        if(command.getManager() != null && command.getManager() != this) return;

        if(!command.isRunning())
        {
            debug("wow 2");
            activeCommands.add(command);
            command.init(this);
        }
        else
        {
            command.end(true);
            command.init(this);
        }
    }

    /**
     * Prematurely end the given command
     * @param command The command to end
     */
    public void stop(Command command)
    {
        if(command.isRunning() && command.getManager() == this)
        {
            command.end(true);
            activeCommands.remove(command);
        }
    }

    /**
     * Execute one frame
     */
    public void execute()
    {
        /*
        System.out.println("--------------------------------------");
        for(var c: activeCommands)
        {
            System.out.println(c == null);
        }
        System.out.println("--------------------------------------");
        */

        var iter = activeCommands.iterator();
        while(iter.hasNext())
        {
            handleNextCommand(iter);
        }

        lastUpdateTime = System.currentTimeMillis();
    }

    /**
     * Handle the given command
     */
    public void handleCommand(Command command)
    {
        command.execute();

        if(command.finished())
        {
            command.end(false);
            activeCommands.remove(command);
        }
    }

    /**
     * Handle the next command from a given iterator
     */
    public void handleNextCommand(Iterator<Command> commandIter)
    {
        var command = commandIter.next();

        command.execute();

        if(command.finished())
        {
            command.end(false);
            commandIter.remove();
        }
    }

    /**
     * Switch to a new state
     * @param newState The new state
     */
    public void changeState(RobotState newState)
    {
        if(newState.equals(currentState)) return;

        var newIsNone = newState.equals(RobotState.NONE);

        var originalState = currentState;
        currentState = newState;

        for(var c: activeCommands)
        {
            if(newIsNone || !c.remainDuringStateChange(originalState, newState))
            {
                stop(c);
            }
        }

        if(newIsNone)
        {
            return;
        }

        if(originalState.equals(RobotState.NONE))
        {
            for(var c : startupCommands)
            {
                if(!c.isRunning())
                    start(c);
            }
        }

        if(stateCommands.containsKey(newState))
        {
            for(var c : stateCommands.get(newState))
            {
                if(!c.isRunning())
                    start(c);
            }
        }

        for(var c : defaultCommands)
        {
            if(!c.isRunning())
                start(c);
        }

        System.out.println(newState);
    }   

    /** Whether or not this manager is in debug mode. */
    private boolean debug;
    /** Returns whether or not the manager is currently in debug mode. */
    public boolean isDebug()
    {
        return debug;
    }
    /** Enables debug mdoe for this manager. */
    protected void enableDebug()
    {
        debug = true;
    }

    /**
     * Print a message if the manager is currently in debug mode.
     * @param msg The message.
     */
    public void debug(String msg)
    {
        if (debug)
        {
            System.out.println("DEBUG @ " + Instant.now().toString() + ": " + msg);
        }
    }

    /** Returns an approximation of the time (in seconds) elapsed since the last update. */
    public double deltaTime()
    {
        if(lastUpdateTime == -1)
        {
            return TimedRobot.kDefaultPeriod;
        }
        else
        {
            return (System.currentTimeMillis() - lastUpdateTime) / 1000.0;
        }
    }

    /** Initialize the delta time. */
    void initDeltaTime()
    {
        lastUpdateTime = System.currentTimeMillis();
    }
}
