package frc.robot.managers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.BiPredicate;
import java.util.function.Function;

import com.ctre.phoenix.sensors.PigeonIMU;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Data;
import frc.robot.framework.ManagerBase;
import frc.robot.framework.wpilib.senables.Sendables;
import frc.robot.structure.DetectedBall;

public class VisionManager extends ManagerBase 
{
    ////////////////////////
    // NETWORK TABLE INFO
    ////////////////////////
    public static final String CURRENT_PROTO_VER = "1.1.0";

    public static final String NT_NAME = "Vision";
    public static final String PROTO_VER = "__ver";
    
    public static final String IS_WRITING = "IsWriting";
    public static final String CIRCULARITIES = "Circularities";
    public static final String PERIMETERS = "Perimeters";
    public static final String ANGLES = "Angles";
    public static final String AREAS = "Areas";
    public static final String XS = "Xs";
    public static final String YS = "Ys";

    public static final String CURRENT_TEAM = "CurrentTeam";

    ////////////////////////
    // CONSTANTS
    ////////////////////////
    public static final double MIN_AREA = 0.1*0.1;
    public static final double MIN_CIRCULARITY = 0.4;

    ////////////////////////
    // IMPLEMENTATION
    ////////////////////////
    public VisionManager()
    {
        var nt = NetworkTableInstance.getDefault().getTable(NT_NAME);

        protoVerEntry = nt.getEntry(PROTO_VER);

        isWritingEntry = nt.getEntry(IS_WRITING);

        circularitiesEntry = nt.getEntry(CIRCULARITIES);
        perimetersEntry = nt.getEntry(PERIMETERS);
        anglesEntry = nt.getEntry(ANGLES);
        areasEntry = nt.getEntry(AREAS);
        xsEntry = nt.getEntry(XS);
        ysEntry = nt.getEntry(YS);

        currentTeamEntry = nt.getEntry(CURRENT_TEAM);

        isUpdating = true;
        reset();
    }

    private final NetworkTableEntry
        protoVerEntry, 
        isWritingEntry,
        anglesEntry, areasEntry, xsEntry, ysEntry, circularitiesEntry, perimetersEntry,
        currentTeamEntry
    ;

    @Override
    public void reset()
    {
        balls = new ArrayList<>();
    }

    private boolean isUpdating;

    private ArrayList<DetectedBall> balls = new ArrayList<>();
    public ArrayList<DetectedBall> getBalls() {return balls;}
    
    public void always() 
    {
        currentTeamEntry.setString(Data.getAlliance());

        if(!isUpdating)
        {
            return;
        }
        
        var pVer = protoVerEntry.getString("[not present]");
        if(!pVer.equals(CURRENT_PROTO_VER))
        {
            if(System.currentTimeMillis() % 10 == 0)
            {
                if(pVer.equals("[not present]"))
                {
                    System.out.println(
                        "[WARNING]: vision data is not present! Skipping this update."
                    );
                }
                else
                {
                    System.err.println(
                        "[WARNING]: vision protocol version '" + pVer + 
                        "' does not match expected version " + CURRENT_PROTO_VER +
                        ". Stopping the vision pipeline now."
                    );

                    isUpdating = false;
                }
            }

            reset();
            return;
        }

        if(isWritingEntry.getBoolean(false))
        {
            System.out.println("[LOW]: vision data is lagging behind! Skipping this update.");
            return;
        }

        var circularities = circularitiesEntry.getDoubleArray(new double[0]);
        var perimeters = perimetersEntry.getDoubleArray(new double[0]);
        var angles = anglesEntry.getDoubleArray(new double[0]);
        var areas = areasEntry.getDoubleArray(new double[0]);
        var xs = xsEntry.getDoubleArray(new double[0]);
        var ys = ysEntry.getDoubleArray(new double[0]);

        var len = angles.length;

        if(
            circularities.length != len ||
            perimeters.length != len ||
            angles.length != len || 
            areas.length != len ||
            xs.length != len ||
            ys.length != len) 
        {
            System.out.println(
                "Lengths of circularities, perimeters, angles, areas, xs, and ys."
            );

            reset();
            return;
        }

        balls = new ArrayList<>();
        for(int i = 0; i < len; i++)
        {
            balls.add(
                new DetectedBall(
                    circularities[i],
                    perimeters[i],
                    angles[i],
                    areas[i],
                    xs[i], 
                    ys[i]
                )
            );
        }

        /*/ TEMPORARY
        System.out.println("---------------------------------");
        System.out.println("Ball count: " + balls.size());
        for (var ball : balls) 
        {
            System.out.println(ball);
        }
        System.out.println();
        System.out.println("---------------------------------");
        //*/
    }

    /**
     * @param predicate The predicate to use to choose which ball to select. 
     * Applied to the current ball, and the current selected ball in order.
     */
    public DetectedBall getBall(BiPredicate<DetectedBall, DetectedBall> predicate)
    {
        DetectedBall selection = null;

        for (var ball : balls)
        {
            if (selection == null || predicate.test(ball, selection))
            {
                selection = ball;
            }
        }

        return selection;
    }
}
