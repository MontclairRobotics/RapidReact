package frc.robot.framework;

public class RobotState
{
    private String name;
    public RobotState(String name)
    {
        this.name = name;
    }

    @Override
    public boolean equals(Object other)
    {
        if(other instanceof RobotState)
        {
            return (this.name == ((RobotState)other).name);
        }

        return false;
    }

    // Default members
    public static final RobotState 
        TELEOP = new RobotState("teleop"),
        AUTONOMOUS = new RobotState("autonomous"),
        TESTING = new RobotState("testing"),
        NONE = new RobotState("none");
}
