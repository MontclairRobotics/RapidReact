package frc.robot.framework.maths;

public enum Sign 
{
    PLUS(1),
    MINUS(-1),
    ZERO(0)
    ;

    private double sign;
    private Sign(double sign)
    {
        this.sign = sign;
    }

    public double get()
    {
        return sign;
    }
}
