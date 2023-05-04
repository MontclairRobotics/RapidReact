package frc.robot.framework;

import frc.robot.Constants;

public enum ScoreHeights {
    MID(Constants.SHOOTER_MID_SPEED.get()),
    HIGH(Constants.SHOOTER_HIGH_SPEED.get())
    ;

    private ScoreHeights(double speed) {
        this.speed = speed;
    }
    private final double speed;

    public double getSpeed() {
        return speed;
    }
}
