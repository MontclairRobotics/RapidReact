package frc.robot.framework.frc;

import java.io.IOException;
import java.nio.file.Path;

import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryUtil;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;

public class PathWeaver 
{
    private PathWeaver() {}

    private static String folderPath;
    public static void setFolderPath(String folderPath) {PathWeaver.folderPath = folderPath;}

    public static Trajectory getThrow(String name) throws IOException
    {
        try 
        {
            var trajectoryPath = Filesystem.getDeployDirectory().toPath().resolve(name);
            return TrajectoryUtil.fromPathweaverJson(trajectoryPath);
        } 
        catch (IOException ex) 
        {
            DriverStation.reportError("Unable to open trajectory '" + name + "'. Stopping robot program.", ex.getStackTrace());
            throw ex;
        }
    }
    public static Trajectory get(String name)
    {
        try 
        {
            var trajectoryPath = Filesystem.getDeployDirectory().toPath().resolve(name);
            return TrajectoryUtil.fromPathweaverJson(trajectoryPath);
        } 
        catch (IOException ex) 
        {
            DriverStation.reportError("Unable to open trajectory '" + name + "'. Stopping robot program.", ex.getStackTrace());
            System.exit(1);
            return null;
        }
    }
}
