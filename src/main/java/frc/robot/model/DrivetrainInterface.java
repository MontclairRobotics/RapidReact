package frc.robot.model;

import frc.robot.utilities.smoothing.Smoother;

public interface DrivetrainInterface 
{
    void setSmoother(Smoother speedSmoother);
    void set(double speed, double turn);
    void setMaxOutput(double maxOutput);
    void stop();
    void setTargetDistance(double td);
    void setTargetAngle(double ta);
    void update(double deltaTime);
    void releaseDistanceTarget();
    void releaseAngleTarget();
    boolean reachedTargetDistance();
    boolean reachedTargetAngle();
}
