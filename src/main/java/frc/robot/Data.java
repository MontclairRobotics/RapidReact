package frc.robot;

import java.lang.reflect.Modifier;
import java.util.Arrays;

import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilderImpl;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.RapidReactContainer.AutoCommand;
import frc.robot.framework.wpilib.senables.Sendables;

public final class Data 
{
    private Data(){}

    private static SendableChooser<AutoCommand> autoChooser;

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

        autoChooser = Sendables.enumChooser(AutoCommand.MAIN, AutoCommand::values);
        SmartDashboard.putData("Auto.Command", autoChooser);

        SmartDashboard.putNumber("PID.DistanceToTarget", 0);
        SmartDashboard.putNumber("PID.AngleToTarget", 0);

        SmartDashboard.putNumber("AngularVelocity", 0);

        SmartDashboard.putNumber("CurrentMaxSpeed", 0);
        SmartDashboard.putString("CurrentEasing", "None");
    }

    public static double getDistanceKP() {return 0.1;/*SmartDashboard.getNumber("PID.Distance.KP", 0);*/}
    public static double getDistanceKI() {return 0.0;/*SmartDashboard.getNumber("PID.Distance.KI", 0);*/}
    public static double getDistanceKD() {return 0.0;/*SmartDashboard.getNumber("PID.Distance.KD", 0);*/}
    public static double getDistanceTolerance() {return 0.1;/*SmartDashboard.getNumber("PID.Distance.Tolerance", 0);*/}

    public static double getAngleKP() {return 0.005;/*SmartDashboard.getNumber("PID.Angle.KP", 0);*/}
    public static double getAngleKI() {return 0.0;/*SmartDashboard.getNumber("PID.Angle.KI", 0);*/}
    public static double getAngleKD() {return 0.0;/*SmartDashboard.getNumber("PID.Angle.KD", 0);*/}
    public static double getAngleTolerance() {return 1.0;/*SmartDashboard.getNumber("PID.Angle.Tolerance", 0);*/}

    public static AutoCommand getAutoCommand() 
    {
        var c = autoChooser.getSelected();
        if(c == null) return AutoCommand.MAIN;
        return c;
    }

    public static void setDistanceToTarget(double value) {SmartDashboard.putNumber("PID.DistanceToTarget", value);}
    public static void setAngleToTarget(double value) {SmartDashboard.putNumber("PID.AngleToTarget", value);}
    public static void setAngularVelocity(double value) {SmartDashboard.putNumber("AngularVelocity", value);}

    public static void setCurrentMaxSpeed(double value) {SmartDashboard.putNumber("CurrentMaxSpeed", value);}
    public static void setCurrentEasing(String value) {SmartDashboard.putString("CurrentEasing", value);}
}
