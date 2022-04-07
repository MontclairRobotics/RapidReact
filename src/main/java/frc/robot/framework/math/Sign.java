package frc.robot.framework.math;

public enum Sign 
{
    PLUS(1),
    MINUS(-1),
    ZERO(0),
    UNDEFINED(Double.NaN)
    ;

    public final double sign;
    private Sign(double sign)
    {
        this.sign = sign;
    }
}
