package frc.robot.framework;

import java.util.HashSet;
import java.util.Iterator;
import java.util.HashMap;
import java.time.Instant;
import java.util.ArrayList;

public abstract class CommandManager<T extends CommandManager<T>>
{
    // Data structures
    private final HashSet<Command<T>> activeCommands;
    private final HashMap<RobotState, ArrayList<Command<T>>> stateCommands;
    private final HashSet<Command<T>> defaultCommands;

    private RobotState currentState;

    // Constructor
    public CommandManager()
    {
        activeCommands = new HashSet<>();
        stateCommands = new HashMap<>();
        defaultCommands = new HashSet<>();

        currentState = RobotState.NONE;

        init();
    }

    /** Initializes this manager. */
    public abstract void init();

    /** Returns the current manager as an instance of T. */
    @SuppressWarnings("unchecked")
    private T asT()
    {
        return (T)this;
    }

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
    public final void addCommand(Command<T> command, RobotState newState)
    {
        if(stateCommands.containsKey(newState))
        {
            stateCommands.get(newState).add(command);
        }
        else
        {
            var newCommands = new ArrayList<Command<T>>();
            newCommands.add(command);

            stateCommands.put(newState, newCommands);
        }
    }

    /**
     * A fluent interface for adding a command to the command 
     * The command added will run when any state is changed.
     * @param command  The command to add
     */
    public final void addDefaultCommand(Command<T> command)
    {
        defaultCommands.add(command);
    }

    /**
     * Start a new command.
     * If the command is already running, restart it.
     * If the command belongs to another command manager, do nothing.
     * @param command The command to start
     */
    public final void start(Command<T> command)
    {
        if(command.getManager() != this) return;

        if(!command.isRunning())
        {
            activeCommands.add(command);
            command.init(asT());
        }
        else
        {
            command.end(true);
            command.init(asT());
        }
    }

    /**
     * Prematurely end the given command
     * @param command The command to end
     */
    public void stop(Command<T> command)
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
        var iter = activeCommands.iterator();
        while(iter.hasNext())
        {
            handleCommand(iter.next());
        }
    }

    /**
     * Handle the given command
     */
    public void handleCommand(Command<T> command)
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
    public void handleNextCommand(Iterator<Command<T>> commandIter)
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

        var originalState = currentState;
        currentState = newState;

        activeCommands.removeIf(c -> !c.remainDuringStateChange(originalState, newState));

        if(stateCommands.containsKey(newState))
        {
            for(var c: stateCommands.get(newState))
            {
                if(!activeCommands.contains(c))
                    activeCommands.add(c);
            }
        }

        for(var c: defaultCommands)
        {
            if(!activeCommands.contains(c))
                activeCommands.add(c);
        }
    }   

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
        if (debug);
        {
            System.out.println("DEBUG @ " + Instant.now().toString() + ": " + msg);
        }
    }
}