package frc.robot.model;

import com.kauailabs.navx.frc.AHRS;

import frc.robot.framework.CommandManager;
import frc.robot.utilities.smoothing.Smoother;

public class Drivetrain implements DrivetrainInterface
{
    private final DrivetrainModel model;

    public static final Drivetrain DISABLED = new Drivetrain();

    public Drivetrain(Smoother defaultSmoother, AHRS navx, CommandManager manager)
    {
        model = new DrivetrainModel(defaultSmoother, navx, manager);
    }

    private Drivetrain()
    {
        model = null;
    }

    @Override
    public void set(double speed, double turn) {
        if(model != null)
        {
            model.set(speed, turn);
        }
    }

    @Override
    public void setMaxOutput(double maxOutput) {
        if(model != null)
        {
            model.setMaxOutput(maxOutput);
        }
    }

    @Override
    public void stop() {
        if(model != null)
        {
            model.stop();
        }
    }

    @Override
    public void setTargetDistance(double td) {
        if(model != null)
        {
            model.setTargetDistance(td);
        }
    }

    @Override
    public void setTargetAngle(double ta) {
        if(model != null)
        {
            model.setTargetAngle(ta);
        }
    }

    @Override
    public void update(double deltaTime) {
        if(model != null)
        {
            model.update(deltaTime);
        }
    }

    @Override
    public void releaseDistanceTarget() {
        if(model != null)
        {
            model.releaseDistanceTarget();
        }
    }

    @Override
    public void releaseAngleTarget() {
        if(model != null)
        {
            model.releaseAngleTarget();
        }
    }

    @Override
    public boolean reachedTargetDistance() {
        if(model != null)
        {
            return model.reachedTargetDistance();
        }
        return false;
    }

    @Override
    public boolean reachedTargetAngle() {
        if(model != null)
        {
            return model.reachedTargetAngle();
        }
        return false;
    }

    @Override
    public void setSmoother(Smoother speedSmoother) {
        if(model != null)
        {
            model.setSmoother(speedSmoother);
        }
    }

    
}
