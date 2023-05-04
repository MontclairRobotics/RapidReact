package frc.robot.structure;

public class DetectedBall 
{
    private double angle;
    private double area;
    private double x;
    private double y;
    private double circularity;
    private double perimeter;

    public DetectedBall(
        double circularity, 
        double perimeter,
        double angle, 
        double area, 
        double x, 
        double y)
    {
        this.circularity = circularity;
        this.perimeter = perimeter;
        this.angle = angle;
        this.area = area;
        this.x = x;
        this.y = y;
    }

    public double getCircularity() {return circularity;}
    public double getPerimeter() {return perimeter;}
    public double getAngle() {return angle;}
    public double getArea() {return area;}
    public double getX() {return x;}
    public double getY() {return y;}

    @Override
    public String toString() 
    {
        return "DetectedBall(" + 
            x + ", " + y + ", angle=" + angle 
            + ", area=" + area + ", circularity=" + circularity
            + ", perimeter=" + perimeter + ")";
    }
}
