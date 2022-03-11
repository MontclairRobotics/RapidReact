package frc.robot.framework.maths;

public final class MathFloat
{
    private MathFloat() {}

    public static final float epsilon = 1E-7F;
    
    public static float add(float x, float y)
    {
        return x + y;
    }
    public static float subtract(float x, float y)
    {
        return x - y;
    }
    public static float multiply(float x, float y)
    {
        return x * y;
    }
    public static float divide(float x, float y)
    {
        return x / y;
    }

    public static float modulo(float x, float y)
    {
        return x % y;
    }

    public static float pow(float x, float y)
    {
        return (float)Math.pow(x, y);
    }

    public static float min(float x, float y)
    {
        return x < y ? x : y;
    }
    public static float max(float x, float y)
    {
        return x > y ? x : y;
    }

    public static float abs(float x)
    {
        return Math.abs(x);
    }
    public static float square(float x)
    {
        return x * x;
    }
    public static float sqrt(float x)
    {
        return (float)Math.sqrt(x);
    }
    public static float negative(float x)
    {
        return -x;
    }
    public static float inverse(float x)
    {
        return 1 / x;
    }

    public static boolean greaterThan(float x, float y)
    {
        return x > y;
    }
    public static boolean greaterThanOrEqualTo(float x, float y)
    {
        return x >= y;
    }
    public static boolean lessThan(float x, float y)
    {
        return x < y;
    }
    public static boolean lessThanOrEqualTo(float x, float y)
    {
        return x <= y;
    }
    public static boolean equalTo(float x, float y)
    {
        return x == y;
    }
    public static boolean unequalTo(float x, float y)
    {
        return x != y;
    }
    public static boolean approximately(float x, float y)
    {
        return Math.abs(x - y) <= epsilon;
    }
    public static boolean notApproximately(float x, float y)
    {
        return Math.abs(x - y) > epsilon;
    }

    public static boolean inRange(float x, float min, float max)
    {
        return (min < x) && (x < max);
    }
    public static boolean inRangeInclusive(float x, float min, float max)
    {
        return (min <= x) && (x <= max);
    }

    public static boolean inRadius(float x, float center, float radius)
    {
        return (center - radius < x) && (x < center + radius);
    }

    public static boolean isInteger(float x)
    {
        return (int)x == x;
    }
    public static boolean isEven(float x)
    {
        return x % 2 == 0;
    }
    public static boolean isOdd(float x)
    {
        return x % 2 == 1;
    }

    public static boolean isPositive(float x)
    {
        return x > 0;
    }
    public static boolean isNegative(float x)  
    {
        return x < 0;
    }
    public static boolean isNonPositive(float x)
    {
        return x <= 0;
    }
    public static boolean isNonNegative(float x)  
    {
        return x >= 0;
    }

    public static boolean isNaN(float x)
    {
        return Float.isNaN(x);
    }

    public static boolean isZero(float x)
    {
        return x == 0;
    }
    public static boolean isNonZero(float x)
    {
        return x != 0;
    }

    public static boolean isFinite(float x)
    {
        return Float.isFinite(x);
    }
    public static boolean isInfinite(float x)
    {
        return Float.isInfinite(x);
    }

    public static Sign signum(float x)
    {
        if(x == 0) return Sign.ZERO;
        else if(isNaN(x)) return Sign.UNDEFINED;
        else if(x < 0) return Sign.MINUS;
        else return Sign.PLUS;
    }

    public static float clamp(float value, float minValue, float maxValue)
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

    public static boolean signsDoNotMatch(float x, float y)
    {
        return (x >= 0) != (y >= 0);
    }

    public static boolean signsMatch(float x, float y)
    {
        return (x >= 0) == (y >= 0);
    }

    public static float signFromBoolean(boolean x)
    {
        return x ? 1 : -1;
    }
}
