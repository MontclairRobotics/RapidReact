package frc.robot.rev;

public enum BlinkinLEDMode 
{
    // DYLAN: fixed palette pattern (first page)
    RAINBOW_RAINBOW(-0.99),
    RAINBOW_PARTY(-0.97),

    // LILA: fixed palette pattern (page 2)
    FIRE_LARGE(-0.57),
    TWINKLES_RAINBOW(-0.53),
    TWINKLES_PARTY(-0.53),
    TWINKLES_OCEAN(-0.51),
    TWNKLES_LAVA(-0.49),
    TWINKLES_FOREST(-0.47),
    COLOR_WAVES_RAINBOW(-0.45),
    COLOR_WAVES_PARTY(-0.43),
    COLOR_WAVES_OCEAN(-0.41),
    COLOR_WAVES_LAVA(-0.39),
    COLOR_WAVES_FOREST(-0.37),
    LARSON_SCANNER_RED(-0.35),
    LARSON_SCANNER_GRAY(-0.33),
    LIGHT_CHASE_RED(-0.31),
    LIGHT_CHASE_BLUE(-0.23),
    LIGHT_CHASE_GRAY(-0.27),
    HEARTBEAT_RED(-0.25),
    HEARTBEAT_BLUE(-0.23),
    HEARTBEAT_WHITE(-0.21),
    HEARTBEAT_GRAY(-0.19),
    BREATH_RED(-0.17),
    BREATH_BLUE(-0.15),
    BREATH_GRAY(-0.13),
    STROBE_RED(-0.11),
    STROBE_BLUE(-0.09)
    STROBE_GOLD(-0.07),
    STROBE_WHITE(-0.05),


    // ABE: color 1 patterns

    // MERT: color 2 patterns

    // CAITIE: color 1 & 2 patterns

    // MAX: solid color patterns

    ;

    //#region Internals
    private double sparkValue;

    private BlinkinLEDMode(double sparkValue)
    {
        this.sparkValue = sparkValue;
    }
    //#endregion
}
