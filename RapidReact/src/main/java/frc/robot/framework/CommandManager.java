package frc.robot.framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public final class CommandManager 
{
    // Data structures
    private final HashSet<Command> activeCommands;
    private final HashMap<RobotState, ArrayList<Command>> defaultCommands;

    private RobotState currentState;

    // Constructor
    public CommandManager()
    {
        activeCommands = new HashSet<>();
        defaultCommands = new HashMap<>();

        currentState = RobotState.NONE;
    }

    // Fluent interface for adding default commands
    public CommandManager addDefaultCommand(Command command, RobotState newState)
    {
        if(defaultCommands.containsKey(newState))
        {
            defaultCommands.get(newState).add(command);
        }
        else
        {
            var newCommands = new ArrayList<Command>();
            newCommands.add(command);

            defaultCommands.put(newState, newCommands);
        }
        return this;
    }

    /**
     * Start a new command.
     * If the command is already running, restart it.
     * If the command belongs to another command manager, do nothing.
     * @param command The command to start
     */
    public void start(Command command)
    {
        if(command.getManager() != this) return;

        if(!command.isRunning())
        {
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
        var iter = activeCommands.iterator();
        while(iter.hasNext())
        {
            handleCommand(iter.next());
        }
    }

    /**
     * Handle the given command
     */
    void handleCommand(Command command)
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
    void handleNextCommand(Iterator<Command> commandIter)
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

        activeCommands.removeIf(c -> c.remainDuringStateChange(originalState, newState));
        if(defaultCommands.containsKey(newState))
        {
            activeCommands.addAll(defaultCommands.get(newState));
        }
    }
}
