package frc.robot.framework.math;

public abstract class Updater<T>
{
    public Updater(T value)
    {
        current = value;
    }

    private T current;

    public final T current()
    {
        return current;
    }

    public final void setDirect(T value)
    {
        current = value;
    }
    
    public final void update(double deltaTime, T target)
    {
        current = update(deltaTime, current, target);
    }

    protected abstract T update(double deltaTime, T current, T target);
}
