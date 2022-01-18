package frc.robot.framework;

public class RobotState
{
    private final String name;

    public RobotState(String name)
    {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) 
    {
        return obj instanceof RobotState ? ((RobotState)obj).name.equals(name) : false;
    }

    // Default members
    public static final RobotState 
        TELEOP = new RobotState("teleop"),
        AUTONOMOUS = new RobotState("autonomous"),
        DISABLED = new RobotState("disabled"),
        TESTING = new RobotState("testing"),
        NONE = new RobotState("none");
}
