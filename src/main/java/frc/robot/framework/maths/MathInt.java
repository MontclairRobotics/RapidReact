package frc.robot.framework.maths;

public final class MathInt
{
    private MathInt() {}
    
    public static int add(int x, int y)
    {
        return x + y;
    }
    public static int subtract(int x, int y)
    {
        return x - y;
    }
    public static int multiply(int x, int y)
    {
        return x * y;
    }
    public static int divide(int x, int y)
    {
        return x / y;
    }
    public static float divideFloat(int x, int y)
    {
        return (float)x / y;
    }

    public static int modulo(int x, int y)
    {
        return x % y;
    }

    public static int pow(int x, int y)
    {
        return (int)Math.pow(x, y);
    }

    public static int min(int x, int y)
    {
        return x < y ? x : y;
    }
    public static int max(int x, int y)
    {
        return x > y ? x : y;
    }

    public static int abs(int x)
    {
        return Math.abs(x);
    }
    public static int square(int x)
    {
        return x * x;
    }
    public static int sqrt(int x)
    {
        return (int)Math.sqrt(x);
    }
    public static int negative(int x)
    {
        return -x;
    }
    public static int inverse(int x)
    {
        return 1 / x;
    }

    public static boolean greaterThan(int x, int y)
    {
        return x > y;
    }
    public static boolean greaterThanOrEqualTo(int x, int y)
    {
        return x >= y;
    }
    public static boolean lessThan(int x, int y)
    {
        return x < y;
    }
    public static boolean lessThanOrEqualTo(int x, int y)
    {
        return x <= y;
    }
    public static boolean equalTo(int x, int y)
    {
        return x == y;
    }
    public static boolean unequalTo(int x, int y)
    {
        return x != y;
    }

    public static boolean inRange(int x, int min, int max)
    {
        return (min < x) && (x < max);
    }
    public static boolean inRangeInclusive(int x, int min, int max)
    {
        return (min <= x) && (x <= max);
    }

    public static boolean inRadius(int x, int center, int radius)
    {
        return (center - radius < x) && (x < center + radius);
    }

    public static boolean isEven(int x)
    {
        return x % 2 == 0;
    }
    public static boolean isOdd(int x)
    {
        return x % 2 == 1;
    }

    public static boolean isPositive(int x)
    {
        return x > 0;
    }
    public static boolean isNegative(int x)  
    {
        return x < 0;
    }
    public static boolean isNonPositive(int x)
    {
        return x <= 0;
    }
    public static boolean isNonNegative(int x)  
    {
        return x >= 0;
    }

    public static boolean isZero(int x)
    {
        return x == 0;
    }
    public static boolean isNonZero(int x)
    {
        return x != 0;
    }

    public static Sign signum(int x)
    {
        if(x == 0) return Sign.ZERO;
        else if(x < 0) return Sign.MINUS;
        else return Sign.PLUS;
    }

    public static int clamp(int value, int minValue, int maxValue)
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

    public static boolean signsDoNotMatch(int x, int y)
    {
        return (x >= 0) != (y >= 0);
    }

    public static boolean signsMatch(int x, int y)
    {
        return (x >= 0) == (y >= 0);
    }

    public static int signFromBoolean(boolean x)
    {
        return x ? 1 : -1;
    }
}
