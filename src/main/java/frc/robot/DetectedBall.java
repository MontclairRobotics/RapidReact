package frc.robot;

public class DetectedBall 
{
    private double angle;
    private double x;
    private double y;

    public DetectedBall(double angle, double x, double y)
    {
        this.angle = angle;
        this.x = x;
        this.y = y;
    }

    public double getAngle() {return angle;}
    public double getX() {return x;}
    public double getY() {return y;}
}
