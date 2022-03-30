package frc.robot.framework;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public abstract class ManagedSubsystemBase extends SubsystemBase implements Manager
{
    public ManagedSubsystemBase()
    {
        CommandRobot.registerManager(this);
    }

    public void reset() {}
    public void always() {}
    public void initialize() {}
}
