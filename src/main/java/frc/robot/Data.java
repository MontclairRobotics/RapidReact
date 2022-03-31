package frc.robot;

import java.lang.reflect.Modifier;
import java.util.Arrays;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.VideoSource;
import edu.wpi.first.hal.AllianceStationID;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
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

        allianceChooser = Sendables.chooser("Red", "Blue");
        fmsAlliance = NetworkTableInstance.getDefault()
            .getTable("FMSInfo")
            .getEntry("IsRedAlliance");

        setupMainTab();
        setupDebugTab();
    }

    private static void setupMainTab()
    {
        mainTab.add("Alliance", allianceChooser)
            .withWidget(BuiltInWidgets.kSplitButtonChooser)
            .withPosition(0, 3)
            .withSize(1, 2);

        var values = mainTab.getLayout("Values", BuiltInLayouts.kList)
            .withPosition(1, 0)
            .withSize(1, 3);

        values.addNumber("Angular Velocity", RapidReact.navx::getAngularVelocity);
        values.addNumber("Current Max Speed", RapidReact.drivetrain::getMaxOutput);
        values.addString("Current Easing", () -> RapidReact.drivetrain.getProfiler().getName());

        /*
        mainTab.add("Cam 1", CameraServer.getVideo("Shooter Vision").getSource())
            .withPosition(1, 0)
            .withSize(3, 2);
        mainTab.add("Vision", CameraServer.getVideo("Vision Output").getSource())
            .withPosition(1+3, 0)
            .withSize(3, 2);
        */
    }
    private static void setupDebugTab()
    {
        distanceToTarget = debugTab.add("Distance to Target", 0)
            .withWidget(BuiltInWidgets.kGraph)
            .withPosition(0, 0)
            .withSize(3, 3)
            .getEntry();
        angleToTarget = debugTab.add("Angle to Target", 0)
            .withWidget(BuiltInWidgets.kGraph)
            .withPosition(0+3, 0)
            .withSize(3, 3)
            .getEntry();

        var drive = debugTab.getLayout("Drive", BuiltInLayouts.kList)
            .withPosition(0+3+3, 0)
            .withSize(1, 2);

        driveMode = drive.add("Drive Mode", "[normal]").getEntry();
        driveSpeed = drive.add("Drive Speed", 0).getEntry();
        drive.addNumber("Distace Travelled", RapidReact.drivetrain::getAverageDistance);

        var turn = debugTab.getLayout("Turn", BuiltInLayouts.kList)
            .withPosition(0+3+3+1, 0)
            .withSize(1, 2);

        turnSpeed = turn.add("Turn Speed", 0).getEntry();
        turnMode = turn.add("Turn Mode", "[normal]").getEntry();

        var ball = debugTab.getLayout("Ball", BuiltInLayouts.kList)
            .withPosition(0+3+3+1+1, 0)
            .withSize(1, 2);
        
        angleToBall = ball.add("Angle to Selected", 0).getEntry();
        ballArea = ball.add("Area of Selected", 0).getEntry();

        useFmsAlliance = debugTab.add("Use FMS Alliance?", false)
            .withWidget(BuiltInWidgets.kToggleSwitch)
            .withPosition(0, 3)
            .withSize(2, 1)
            .getEntry();
    }

    public static void setupAuto(String name, Sendable sendable) 
    {
        mainTab.add(name, sendable)
            .withWidget(BuiltInWidgets.kComboBoxChooser)
            .withPosition(0+2, 3)
            .withSize(2, 1);
        debugTab.add(name, sendable)
            .withWidget(BuiltInWidgets.kComboBoxChooser)
            .withPosition(0+2, 3)
            .withSize(2, 1);
    }

    public static ShuffleboardTab mainTab() {return mainTab;}
    public static ShuffleboardTab debugTab() {return debugTab;}

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
            return fmsAlliance.getBoolean(false) ? "Red" : "Blue";
        }

        return getAllianceRaw();
    }

    public static double getDistanceKP() {return 0.035;}
    public static double getDistanceKI() {return 0.0;}
    public static double getDistanceKD() {return 0.0078;}
    public static double getDistanceTolerance() {return 2;}

    public static double getAngleKP() {return 0.0091;}
    public static double getAngleKI() {return 0.002;}
    public static double getAngleKD() {return 0.00135;}
    public static double getAngleTolerance() {return 2.0;}
    public static double getAngleIntMax() {return 0.1;}

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
