package frc.robot.managers;

import com.ctre.phoenix.sensors.PigeonIMU;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import frc.robot.Team;
import frc.robot.framework.ManagerBase;

public class VisionManager extends ManagerBase 
{
    public static final String CURRENT_PROTO_VER = "0.5.0";
    public static final String NT_NAME = "Vision";

    public static final String PROTO_VER = "__VER";
    public static final String ANGLE = "ANGLE";
    public static final String IS_PRESENT = "IS_PRESENT";
    public static final String TEAM = "TEAM";

    public VisionManager()
    {
        var nt = NetworkTableInstance.getDefault().getTable(NT_NAME);

        protoVerEntry = nt.getEntry(PROTO_VER);
        angleEntry = nt.getEntry(ANGLE);
        isPresentEntry = nt.getEntry(IS_PRESENT);
        teamEntry = nt.getEntry(TEAM);

        isUpdating = true;
        reset();
    }

    private final NetworkTableEntry
        protoVerEntry, angleEntry, isPresentEntry, teamEntry
    ;

    private void reset()
    {
        angle = 0;
        isPresent = false;
        team = Team.RED;
    }

    private boolean isUpdating;

    private double angle;
    private boolean isPresent;
    private Team team;

    public double getAngle() {return angle;}
    public boolean getIsPresent() {return isPresent;}
    public Team getTeam() {return team;}
    
    public void periodic() 
    {
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
                return;
            }
            else
            {
                System.out.println(
                    "[WARNING]: vision protocol version '" + pVer + 
                    "' does not match expected version " + CURRENT_PROTO_VER +
                    "'. Vision values will be reset and not updated until this is fixed."
                );
            }
            isUpdating = false;
            reset();
            return;
        }

        angle = angleEntry.getDouble(0);
        isPresent = isPresentEntry.getBoolean(false);

        var teamStr = teamEntry.getString("red");
        switch(teamStr)
        {
            case "red":
                team = Team.RED;
                break;
            case "blue":
                team = Team.BLUE;
                break;
        }
    }
}
