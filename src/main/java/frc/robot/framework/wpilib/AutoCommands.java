package frc.robot.framework.wpilib;

import java.security.InvalidAlgorithmParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;

public final class AutoCommands 
{
    private AutoCommands() {}

    private static final Map<String, Supplier<Command>> commands = new HashMap<>();

    // Settings
    private static BiConsumer<String, Sendable> autoCommandInitializer = (n, c) -> {
        SmartDashboard.putData(n, c);
        SmartDashboard.setPersistent(n);
    };
    private static String autoNetworkTableName = "Auto";
    private static String defaultAutoCommand = "Main";

    public static void setAutoCommandInitializer(BiConsumer<String, Sendable> value)
    {autoCommandInitializer = value;}
    public static void setAutoNetworkTableName(String value)
    {autoNetworkTableName = value;}
    public static void setDefaultAutoCommand(String value)
    {defaultAutoCommand = value;}
    
    public static void add(String name, Supplier<Command> command)
    {
        commands.put(name, command);
    }
    public static Command get(String name)
    {
        return commands.get(name).get();
    }

    public static SendableChooser<Command> chooser()
    {
        var s = new SendableChooser<Command>();

        s.setDefaultOption(defaultAutoCommand, get(defaultAutoCommand));

        for(var name : commands.keySet())
        {
            if(name.equals(defaultAutoCommand))
            {
                continue;
            }

            s.addOption(name, get(name));
        }

        autoCommandInitializer.accept(autoNetworkTableName, s);

        return s;
    }
}
