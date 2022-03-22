package frc.robot.managers;

import com.ctre.phoenix.sensors.PigeonIMU;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import frc.robot.DetectedBall;
import frc.robot.Team;
import frc.robot.framework.ManagerBase;

public class VisionManager extends ManagerBase 
{
    public static final String CURRENT_PROTO_VER = "0.5.0";

    public static final String NT_NAME = "Vision";
    public static final String PROTO_VER = "__VER";
    
    public static final String ANGLES = "ANGLES";
    public static final String XS = "XS";
    public static final String YS = "YS";

    public VisionManager()
    {
        var nt = NetworkTableInstance.getDefault().getTable(NT_NAME);

        protoVerEntry = nt.getEntry(PROTO_VER);

        anglesEntry = nt.getEntry(ANGLES);
        xsEntry = nt.getEntry(XS);
        ysEntry = nt.getEntry(YS);

        isUpdating = true;
        reset();
    }

    private final NetworkTableEntry
        protoVerEntry, anglesEntry, xsEntry, ysEntry
    ;

    private void reset()
    {
        balls = new DetectedBall[0];
    }

    private void simulate()
    {
        protoVerEntry.setString("0.5.0");
        anglesEntry.setDoubleArray(new double[] {0, 1});
        xsEntry.setDoubleArray(new double[] {200, 300});
        ysEntry.setDoubleArray(new double[] {100, 200});
    }

    private boolean isUpdating;

    private DetectedBall[] balls;
    public DetectedBall[] getBalls() {return balls;}
    
    public void periodic() 
    {
        //*TEMPORARY
        simulate();
        //*/

        if(!isUpdating)
        {
            return;
        }
        
        var pVer = protoVerEntry.getString("[not present]");
        if(!pVer.equals(CURRENT_PROTO_VER))
        {
            if(pVer.equals("[not present]"))
            {
                System.out.println(
                    "[WARNING]: vision data is not present! Skipping this update."
                );
            }
            else
            {
                System.out.println(
                    "[WARNING]: vision protocol version '" + pVer + 
                    "' does not match expected version " + CURRENT_PROTO_VER +
                    ". Stopping the vision pipeline now."
                );

                isUpdating = false;
            }

            reset();
            return;
        }

        var angles = anglesEntry.getDoubleArray(new double[0]);
        var xs = xsEntry.getDoubleArray(new double[0]);
        var ys = ysEntry.getDoubleArray(new double[0]);

        if(angles.length != xs.length || ys.length != xs.length) 
        {
            System.out.println(
                "Lengths of angles, xs, and ys must be equal."
            );

            reset();
            return;
        }

        var len = angles.length;

        balls = new DetectedBall[len];
        for(int i = 0; i < len; i++)
        {
            balls[i] = new DetectedBall(angles[i], xs[i], ys[i]);
        }

        /*/ TEMPORARY
        System.out.println("---------------------------------");
        System.out.println("Ball count: " + balls.length);
        for (var ball : balls) 
        {
            System.out.println(ball);
        }
        System.out.println();
        System.out.println("---------------------------------");
        //*/
    }
}
