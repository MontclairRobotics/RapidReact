package frc.robot.framework.wpilib;

import java.security.InvalidAlgorithmParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;

public final class AutoCommands 
{
    public AutoCommands() {}
    private final Map<String, Supplier<Command>> commands = new HashMap<>();

    public AutoCommands add(String name, Supplier<Command> command)
    {
        commands.put(name, command);
        return this;
    }
    public Command get(String name)
    {
        return commands.get(name).get();
    }

    public SendableChooser<Command> chooser(String defaultName)
    {
        var s = new SendableChooser<Command>();

        s.setDefaultOption(defaultName, get(defaultName));

        for(var name : commands.keySet())
        {
            if(name.equals(defaultName))
            {
                continue;
            }

            s.addOption(name, get(name));
        }

        return s;
    }
}
