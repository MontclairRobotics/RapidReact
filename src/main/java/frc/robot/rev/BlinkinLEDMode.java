package frc.robot.rev;

public enum BlinkinLEDMode 
{
    // DYLAN: fixed palette pattern (first page)
    RAINBOW_RAINBOW(-0.99),
    RAINBOW_PARTY(-0.97),

    // LILA: fixed palette pattern (page 2)

    // ABE: color 1 patterns

    // MERT: color 2 patterns

    // CAITIE: color 1 & 2 patterns

    // MAX: solid color patterns
    
    HOT_PINK(0.57),
    DARK_RED(0.59),
    RED(0.61),
    RED_ORANGE(0.63),
    ORANGE(0.65),
    GOLD(0.67),
    YELLOW(0.69),
    LAWN_GREEN(0.71),
    LIME(0.73),
    DARK_GREEN(0.75),
    GREEN(0.77),
    BLUE_GREEN(0.79),
    AQUA(0.81),
    SKY_BLUE(0.83),
    DARK_BLUE(0.85),
    BLUE(0.87),
    BLUE_VIOLET(0.89),
    VIOLET(0.91),
    WHITE(0.93),
    GRAY(0.95),
    DARK_GRAY(0.97),
    BLACK(0.99),

    ;

    //#region Internals
    private double sparkValue;

    private BlinkinLEDMode(double sparkValue)
    {
        this.sparkValue = sparkValue;
    }
    //#endregion
}
