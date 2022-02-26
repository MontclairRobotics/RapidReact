package frc.robot.framework.utilities;

public final class Ops 
{
    private Ops() {}

    public static final double epsilon = 1e-7;
    
    public static double add(double x, double y)
    {
        return x + y;
    }
    public static double subtract(double x, double y)
    {
        return x - y;
    }
    public static double multiply(double x, double y)
    {
        return x * y;
    }
    public static double divide(double x, double y)
    {
        return x / y;
    }

    public static double modulo(double x, double y)
    {
        return x % y;
    }

    public static double pow(double x, double y)
    {
        return Math.pow(x, y);
    }

    public static double min(double x, double y)
    {
        return x < y ? x : y;
    }
    public static double max(double x, double y)
    {
        return x > y ? x : y;
    }

    public static double abs(double x)
    {
        return Math.abs(x);
    }
    public static double square(double x)
    {
        return x * x;
    }
    public static double sqrt(double x)
    {
        return Math.sqrt(x);
    }
    public static double negative(double x)
    {
        return -x;
    }
    public static double inverse(double x)
    {
        return 1 / x;
    }
}
