package frc.robot;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.LayoutType;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.shuffleboard.SimpleWidget;
import edu.wpi.first.wpilibj.shuffleboard.SuppliedValueWidget;

public final class ShuffleboardConstants 
{
    private ShuffleboardConstants(){}

    public static final ShuffleboardTab 
        pidTab = Shuffleboard.getTab("PID"),
        mainTab = Shuffleboard.getTab("MAIN")
    ;

    private static double distanceToTargetValue = 0.0;
    private static double angleToTargetValue = 0.0;

    public static void setDistanceToTargetValue(double distanceToTargetValue)
    {
        ShuffleboardConstants.distanceToTargetValue = distanceToTargetValue;
    }
    public static void setAngleToTargetValue(double angleToTargetValue)
    {
        ShuffleboardConstants.angleToTargetValue = angleToTargetValue;
    }

    public static final NetworkTableEntry
        // Distance pid
        distanceKP = pidTab.addPersistent("Distance.KP", 0.1).getEntry(),
        distanceKI = pidTab.addPersistent("Distance.KI", 0).getEntry(),
        distanceKD = pidTab.addPersistent("Distance.KD", 0).getEntry(),

        distanceTolerance = pidTab.addPersistent("Distance.Tolerance", 0.1).getEntry(),

        // Angle pid
        angleKP = pidTab.addPersistent("Angle.KP", 0.1).getEntry(),
        angleKI = pidTab.addPersistent("Angle.KI", 0).getEntry(),
        angleKD = pidTab.addPersistent("Angle.KD", 0).getEntry(),

        angleTolerance = pidTab.addPersistent("Angle.Tolerance", 0.1).getEntry(),

        // Main
        autoCommand = mainTab.addPersistent("Auto.Command", Constants.AUTO_MAIN).getEntry()
    ;

    public static final SuppliedValueWidget<Double> 
        distanceToTarget = pidTab.addNumber("Distance.ToTarget", () -> distanceToTargetValue),
        angleToTarget = pidTab.addNumber("Angle.ToTarget", () -> angleToTargetValue)
    ;
}
