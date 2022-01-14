package frc.robot.framework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public final class CommandManager 
{
    // Data structures
    private final ArrayList<Command> activeCommands;
    private final HashMap<RobotState, ArrayList<Command>> defaultCommands;

    private RobotState currentState;

    // Constructor
    public CommandManager()
    {
        activeCommands = new ArrayList<>();
        defaultCommands = new HashMap<>();
        for(RobotState newState : RobotState.values())
        {
            defaultCommands.put(newState, new ArrayList<>());
        }

        currentState = RobotState.NONE;
    }

    // Fluent interface for adding default commands
    public CommandManager addDefaultCommand(Command command, RobotState newState)
    {
        defaultCommands.get(newState).add(command);
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
        if(command.commandManager != this) return;

        command.commandManager = this;
        command.onInit();

        if(!isRunning(command))
            activeCommands.add(command);
    }

    /**
     * Prematurely end the given command
     * @param command The command to end
     */
    public void stop(Command command)
    {
        if(activeCommands.contains(command))
        {
            command.onEnd(true);
            activeCommands.remove(command);
        }
    }

    /**
     * Execute one frame
     */
    public void execute()
    {
        int i = 0;
        while(i < activeCommands.size())
        {
            var cmd = activeCommands.get(i);

            cmd.execute();
            if(cmd.finished())
            {
                cmd.onEnd(false);
                activeCommands.remove(i);
            }
            else
            {
                i++;
            }
        }
    }

    /**
     * Check whether a given command is currently running
     * @param command The command to check
     * @return If the command is running
     */
    public boolean isRunning(Command command)
    {
        return activeCommands.contains(command) || Arrays.stream(command.getSubCommands()).anyMatch(sc -> activeCommands.contains(sc));
    }

    /**
     * Switch to a new state
     * @param newState The new state
     */
    public void switchTo(RobotState newState)
    {
        if(newState.equals(currentState)) return;

        var originalState = currentState;
        currentState = newState;

        activeCommands.removeIf(c -> c.remainDuringStateChange(originalState, newState));
        activeCommands.addAll(defaultCommands.get(newState));
    }
}
