package frc.robot.framework;

public record RobotState(String id)
{
    // Default members
    public static final RobotState 
        TELEOP = new RobotState("teleop"),
        AUTONOMOUS = new RobotState("autonomous"),
        DISABLED = new RobotState("disabled"),
        TESTING = new RobotState("testing"),
        NONE = new RobotState("none");
}
