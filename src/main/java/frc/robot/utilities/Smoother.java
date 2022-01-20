public abstract class Smoother {
    protected double current, target;
    private long lastUpdateTime;
    public double getCurrent() {
        return current;
    }

    public update(double target) {
        updateInternal(target);
        lastUpdateTime = System.currentTimeMillis();
    }

    public double deltaTime()
    {
        return (System.currentTimeMillis() - lastUpdateTime) / 1000.0;
    }

    protected abstract void updateInternal(double target);
}

