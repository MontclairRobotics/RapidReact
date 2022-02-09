package frc.robot.commands;

import frc.robot.framework.Command;
import frc.robot.model.Drivetrain;

public class PIDDistanceCommand extends Command{
    private final Drivetrain drivetrain;
    private double distance;

    public PIDDistanceCommand(Drivetrain drivetrain, double distance)
    {
        this.drivetrain = drivetrain;
        this.distance = distance;
    }

    @Override
    public void onInit() {
        drivetrain.setTargetDistance(distance);
        drivetrain.setTargetAngle(0);
    }

    @Override
    public boolean finished() {
        return drivetrain.reachedTargetDistance();
    }

    @Override
    public void onEnd(boolean wasCancelled) {
        drivetrain.releaseDistanceTarget();
        drivetrain.releaseAngleTarget();
    }

    @Override
    public void execute() {

    }
    

}
