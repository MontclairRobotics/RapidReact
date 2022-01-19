package frc.robot.framework;

import java.math.RoundingMode;

public class RobotState
{
    private String id;

    public RobotState(String id)
    {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) 
    {
        if(obj instanceof RobotState)
        {
            return ((RobotState)obj).id.equals(id);
        }

        return false;
    }

    // Default members
    public static final RobotState 
        TELEOP = new RobotState("teleop"),
        AUTONOMOUS = new RobotState("autonomous"),
        DISABLED = new RobotState("disabled"),
        TESTING = new RobotState("testing"),
        NONE = new RobotState("none");
}
