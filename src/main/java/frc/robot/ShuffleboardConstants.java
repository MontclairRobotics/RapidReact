package frc.robot;

import java.lang.reflect.Modifier;
import java.util.Arrays;

public final class ShuffleboardConstants 
{
    private ShuffleboardConstants(){}

    public static final String
        // Distance pid
        DISTANCE_KP = "PID.Distance.KP",
        DISTANCE_KI = "PID.Distance.KI",
        DISTANCE_KD = "PID.Distance.KD",

        DISTANCE_TOLERANCE = "PID.Distance.Tolerance",

        // Angle pid
        ANGLE_KP = "PID.Angle.KP",
        ANGLE_KI = "PID.Angle.KI",
        ANGLE_KD = "PID.Angle.KD",

        ANGLE_TOLERANCE = "PID.Angle.Tolerance",

        // Main
        AUTO_COMMAND = "Auto.Command",

        DISTANCE_TO_TARGET = "PID.DistanceToTarget",
        ANGLE_TO_TARGET = "PID.AngleToTarget",
        ANGULAR_VELOCITY = "PID.AngularVelocity"
    ;

    public static final String[] MEMBERS = 
        Arrays.stream(
            ShuffleboardConstants.class
            .getDeclaredFields()
        )
        .filter(f -> (f.getModifiers() & Modifier.STATIC) != 0)
        .filter(f -> f.getType().equals(String.class))
        .map(f -> {
            try {
                return (String)f.get(null);
            } catch (Exception e) {
                return null;
            }
        })
        .toArray(String[]::new)
    ;
}
