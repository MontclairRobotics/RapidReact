package frc.robot;

import java.lang.reflect.Modifier;
import java.util.Arrays;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
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
        /*
        SmartDashboard.setDefaultNumber("PID.Distance.KP", 0.1);
        SmartDashboard.setPersistent("PID.Distance.KP");

        SmartDashboard.setDefaultNumber("PID.Distance.KI", 0);
        SmartDashboard.setPersistent("PID.Distance.KI");

        SmartDashboard.setDefaultNumber("PID.Distance.KD", 0);
        SmartDashboard.setPersistent("PID.Distance.KD");

        SmartDashboard.setDefaultNumber("PID.Distance.Tolerance", 0.1);
        SmartDashboard.setPersistent("PID.Distance.Tolerance");

        SmartDashboard.setDefaultNumber("PID.Angle.KP", 0.01);
        SmartDashboard.setPersistent("PID.Angle.KP");

        SmartDashboard.setDefaultNumber("PID.Angle.KI", 0);
        SmartDashboard.setPersistent("PID.Angle.KI");

        SmartDashboard.setDefaultNumber("PID.Angle.KD", 0);
        SmartDashboard.setPersistent("PID.Angle.KD");

        SmartDashboard.setDefaultNumber("PID.Angle.Tolerance", 1);
        SmartDashboard.setPersistent("PID.Angle.Tolerance");
        //*/

        SmartDashboard.putNumber("PID.DistanceToTarget", 0);
        SmartDashboard.putNumber("PID.AngleToTarget", 0);

        SmartDashboard.putNumber("AngularVelocity", 0);

        SmartDashboard.putNumber("CurrentMaxSpeed", 0);
        SmartDashboard.putString("CurrentEasing", "None");
        
        allianceChooser = Sendables.chooser("Red", "Blue");
        SmartDashboard.putData("Alliance", allianceChooser);

        SmartDashboard.putBoolean("UseFMSAlliance", false);

        fmsAlliance = NetworkTableInstance.getDefault().getTable("FMSInfo").getEntry("IsRedAlliance");
    }

    private static NetworkTableEntry fmsAlliance;
    private static SendableChooser<String> allianceChooser;

    public static String getAllianceRaw() {return allianceChooser.getSelected();}
    public static boolean getUseFMSAlliance() {return SmartDashboard.getBoolean("UseFMSAlliance", false);}

    public static String getAlliance()
    {
        if(getUseFMSAlliance())
        {
            return fmsAlliance.getBoolean(false) ? "Blue" : "Red";
        }

        return getAllianceRaw();
    }

    public static double getDistanceKP() {return 0.025;/*SmartDashboard.getNumber("PID.Distance.KP", 0);*/}
    public static double getDistanceKI() {return 0.0;/*SmartDashboard.getNumber("PID.Distance.KI", 0);*/}
    public static double getDistanceKD() {return 0.0;/*SmartDashboard.getNumber("PID.Distance.KD", 0);*/}
    public static double getDistanceTolerance() {return 2;/*SmartDashboard.getNumber("PID.Distance.Tolerance", 0);*/}

    public static double getAngleKP() {return 0.009;/*SmartDashboard.getNumber("PID.Angle.KP", 0);*/}
    public static double getAngleKI() {return 0.001;/*SmartDashboard.getNumber("PID.Angle.KI", 0);*/}
    public static double getAngleKD() {return 0.0004;/*SmartDashboard.getNumber("PID.Angle.KD", 0);*/}
    public static double getAngleTolerance() {return 1.0;/*SmartDashboard.getNumber("PID.Angle.Tolerance", 0);*/}

    public static double getBallKP() {return 0.25;/*SmartDashboard.getNumber("PID.Angle.KP", 0);*/}
    public static double getBallKI() {return 0.0;/*SmartDashboard.getNumber("PID.Angle.KI", 0);*/}
    public static double getBallKD() {return 0.01;/*SmartDashboard.getNumber("PID.Angle.KD", 0);*/}
    public static double getBallTolerance() {return 0.02;/*SmartDashboard.getNumber("PID.Angle.Tolerance", 0);*/}

    public static void setDistanceToTarget(double value) {SmartDashboard.putNumber("PID.DistanceToTarget", value);}
    public static void setAngleToTarget(double value) {SmartDashboard.putNumber("PID.AngleToTarget", value);}
    public static void setAngularVelocity(double value) {SmartDashboard.putNumber("AngularVelocity", value);}

    public static void setCurrentMaxSpeed(double value) {SmartDashboard.putNumber("CurrentMaxSpeed", value);}
    public static void setCurrentEasing(String value) {SmartDashboard.putString("CurrentEasing", value);}
}
