package frc.robot.framework;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.framework.wpilib.AutoCommands;

public abstract class RobotContainer 
{
    public abstract void initialize();
    public abstract void reset();

    public abstract AutoCommands getAutoCommands();

    public BiConsumer<String, Sendable> autoCommandInitializer()
    {
        return (n, c) -> {
            SmartDashboard.putData(n, c);
            SmartDashboard.setPersistent(n);
        };
    }
    public String autoNetworkTableName()
    {
        return "Auto";
    }
    public String defaultAutoCommand()
    {
        return "main";
    }
}
