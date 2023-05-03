package frc.robot.framework.commandrobot;

public interface Manager 
{
    void always();
    default void reset() {}
    default void initialize() {}
}
