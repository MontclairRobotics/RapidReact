package frc.robot.framework;

public final class RobotState
{
    private final String name;
    public RobotState(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return "State{" + name + "}";
    }

    @Override
    public boolean equals(Object other)
    {
        if(other instanceof RobotState)
        {
            return name.equals(((RobotState)other).name);
        }

        return false;
    }

    @Override
    public String toString()
    {
        return "State {" + (name == null ? "none" : name) + "}";
    }

    // Default members
    public static final RobotState 
        TELEOP = new RobotState("TELEOP"),
        AUTONOMOUS = new RobotState("AUTONOMOUS"),
        TESTING = new RobotState("TESTING"),
        NONE = new RobotState(null);
}
