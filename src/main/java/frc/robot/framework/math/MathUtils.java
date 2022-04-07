package frc.robot.framework.math;

public final class MathUtils
{
    private MathUtils() {}

    public static final double epsilon = 1E-7;

    public static boolean approximately(double x, double y)
    {
        return Math.abs(x - y) <= epsilon;
    }
    public static boolean notApproximately(double x, double y)
    {
        return Math.abs(x - y) > epsilon;
    }

    public static boolean inRange(double x, double min, double max)
    {
        return (min < x) && (x < max);
    }
    public static boolean inRangeInclusive(double x, double min, double max)
    {
        return (min <= x) && (x <= max);
    }

    public static boolean inRadius(double x, double center, double radius)
    {
        return (center - radius < x) && (x < center + radius);
    }

    public static Sign signof(double x)
    {
        if(x == 0) return Sign.ZERO;
        else if(Double.isNaN(x)) return Sign.UNDEFINED;
        else if(x < 0) return Sign.MINUS;
        else return Sign.PLUS;
    }

    public static boolean signsDoNotMatch(double x, double y)
    {
        return (x >= 0) != (y >= 0);
    }

    public static boolean signsMatch(double x, double y)
    {
        return (x >= 0) == (y >= 0);
    }

    public static double signFromBoolean(boolean x)
    {
        return x ? 1 : -1;
    }

    /**
     * Returns abs(x)^y * sign(x).
     * Generally used to raise an input to a power and preserve the sign.
     */
    public static double powsign(double x, double y)
    {
        return signof(x).value * Math.pow(Math.abs(x), y);
    }
}
