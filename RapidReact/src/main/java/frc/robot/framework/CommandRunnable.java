package frc.robot.framework;

@FunctionalInterface
public interface CommandRunnable<T extends Command<?>>
{
    public void run(T command);
}
