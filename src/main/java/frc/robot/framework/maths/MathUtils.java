package frc.robot.framework.maths;

public final class MathUtils
{
    private MathUtils() {}

    public static final double epsilon = 1E-7;
    
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

    public static boolean greaterThan(double x, double y)
    {
        return x > y;
    }
    public static boolean greaterThanOrEqualTo(double x, double y)
    {
        return x >= y;
    }
    public static boolean lessThan(double x, double y)
    {
        return x < y;
    }
    public static boolean lessThanOrEqualTo(double x, double y)
    {
        return x <= y;
    }
    public static boolean equalTo(double x, double y)
    {
        return x == y;
    }
    public static boolean unequalTo(double x, double y)
    {
        return x != y;
    }
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

    public static boolean isInteger(double x)
    {
        return (int)x == x;
    }
    public static boolean isEven(double x)
    {
        return x % 2 == 0;
    }
    public static boolean isOdd(double x)
    {
        return x % 2 == 1;
    }

    public static boolean isPositive(double x)
    {
        return x > 0;
    }
    public static boolean isNegative(double x)  
    {
        return x < 0;
    }
    public static boolean isNonPositive(double x)
    {
        return x <= 0;
    }
    public static boolean isNonNegative(double x)  
    {
        return x >= 0;
    }

    public static boolean isNAN(double x)
    {
        return x != x;
    }

    public static boolean isZero(double x)
    {
        return x == 0;
    }
    public static boolean isNonZero(double x)
    {
        return x != 0;
    }

    public static Sign signum(double x)
    {
        if(x == 0 || Double.isNaN(x)) return Sign.ZERO;
        else if(x < 0) return Sign.MINUS;
        else return Sign.PLUS;
    }

    public static double clamp(double value, double minValue, double maxValue)
    {
        if(value < minValue)
        {
            return minValue;
        }

        if(value > maxValue)
        {
            return maxValue;
        }

        return value;
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
}
