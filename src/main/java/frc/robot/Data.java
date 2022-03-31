package frc.robot;

import java.lang.reflect.Modifier;
import java.util.Arrays;

import edu.wpi.first.hal.AllianceStationID;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.shuffleboard.SimpleWidget;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilderImpl;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.framework.wpilib.senables.Sendables;

public final class Data 
{
    private Data(){}
                                        //fucky wucky
    public static void setup()
    {
        mainTab = Shuffleboard.getTab("Main");
        debugTab = Shuffleboard.getTab("Debug");

        // SETTINGS //
        useFmsAlliance = debugTab.add("Use FMS Alliance?", false)
            .withWidget(BuiltInWidgets.kToggleSwitch)
            .getEntry();
        
        // CHOOSERS //
        allianceChooser = Sendables.chooser("Red", "Blue");
        mainTab.add("Alliance", allianceChooser)
            .withWidget(BuiltInWidgets.kSplitButtonChooser);
        
        // DEBUGS //
        distanceToTarget = debugTab.add("Distance to Target", 0)
            .withWidget(BuiltInWidgets.kGraph)
            .getEntry();
        angleToTarget = debugTab.add("Angle to Target", 0)
            .withWidget(BuiltInWidgets.kGraph)
            .getEntry();

        turnMode = debugTab.add("Turn Mode", "[normal]").getEntry();
        driveMode = debugTab.add("Drive Mode", "[normal]").getEntry();

        turnSpeed = debugTab.add("Turn Speed", 0).getEntry();
        driveSpeed = debugTab.add("Drive Speed", 0).getEntry();

        angleToBall = debugTab.add("Angle to Ball", 0).getEntry();
        ballArea = debugTab.add("Area of Ball", 0).getEntry();

        // SUPPLIED VALUES //
        mainTab.addNumber("Angular Velocity", RapidReact.navx::getAngularVelocity);
        mainTab.addNumber("Current Max Speed", RapidReact.drivetrain::getMaxOutput);
        mainTab.addString("Current Easing", () -> RapidReact.drivetrain.getProfiler().getName());
        
        debugTab.addNumber("Distace Travelled", RapidReact.drivetrain::getAverageDistance);

        // INTERNALS //
        fmsAlliance = NetworkTableInstance.getDefault()
            .getTable("FMSInfo")
            .getEntry("IsRedAlliance");
    }

    public static ShuffleboardTab mainTab() {return mainTab;}

    private static ShuffleboardTab mainTab;
    private static ShuffleboardTab debugTab;

    private static NetworkTableEntry 
        distanceToTarget,
        angleToTarget,
        useFmsAlliance,
        turnMode,
        driveMode,
        turnSpeed,
        driveSpeed,
        angleToBall,
        ballArea
    ;

    private static NetworkTableEntry fmsAlliance;
    private static SendableChooser<String> allianceChooser;

    public static String getAllianceRaw() {return allianceChooser.getSelected();}
    public static boolean getUseFMSAlliance() {return useFmsAlliance.getBoolean(false);}

    public static String getAlliance()
    {
        if(getUseFMSAlliance())
        {
            return fmsAlliance.getBoolean(false) ? "Blue" : "Red";
        }

        return getAllianceRaw();
    }

    public static double getDistanceKP() {return 0.035;}
    public static double getDistanceKI() {return 0.0;}
    public static double getDistanceKD() {return 0.0078;}
    public static double getDistanceTolerance() {return 2;}

    public static double getAngleKP() {return 0.009;}
    public static double getAngleKI() {return 0.002;}
    public static double getAngleKD() {return 0.00145;}
    public static double getAngleTolerance() {return 1.0;}

    public static double getBallKP() {return 0.25;}
    public static double getBallKI() {return 0.0;}
    public static double getBallKD() {return 0.01;}
    public static double getBallTolerance() {return 0.02;}

    public static void setDistanceToTarget(double value) 
    {
        distanceToTarget.setDouble(value);
    }
    public static void setAngleToTarget(double value) 
    {
        angleToTarget.setDouble(value);
    }

    public static void setTurnMode(String value)
    {
        turnMode.setString(value);
    }
    public static void setDriveMode(String value)
    {
        driveMode.setString(value);
    }

    public static void setTurnSpeed(double value)
    {
        turnSpeed.setDouble(value);
    }
    public static void setDriveSpeed(double value)
    {
        driveSpeed.setDouble(value);
    }

    public static void setAngleToBall(double value)
    {
        angleToBall.setDouble(value);
    }
    public static void setBallArea(double value)
    {
        ballArea.setDouble(value);
    }
}
