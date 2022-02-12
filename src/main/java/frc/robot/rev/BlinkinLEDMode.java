package frc.robot.rev;

public enum BlinkinLEDMode 
{
    // DYLAN: fixed palette pattern (first page)
    RAINBOW_RAINBOW(-0.99),
    RAINBOW_PARTY(-0.97),
    RAINBOW_OCEAN(-0.95),
    RAINBOW_LAVA(-0.93),
    RAINBOW_FOREST(-0.91),
    RAINBOW_GLITTER(-0.89),

    CONFETTI(-0.87),
    
    SHOT_RED(-0.85),
    SHOT_BLUE(-0.83),
    SHOT_WHITE(-0.81),

    SINELON_RAINBOW(-0.79),
    SINELON_PARTY(-0.77),
    SINELON_OCEAN(-0.75),
    SINELON_LAVA(-0.73),
    SINELON_FOREST(-0.71),

    BPM_RAINBOW(-0.69),
    BPM_PARTY(-0.67),
    BPM_OCEAN(-0.65),
    BPM_LAVA(-0.63),
    BPM_FOREST(-0.61),
    
    FIRE_MEDIUM(-0.59),

    // LILA: fixed palette pattern (page 2)

    // ABE: color 1 patterns
    C1_END_TO_END_BLEND_TO_BLACK(-0.03),
    C1_LARSON_SCANNER(-0.01),
    C1_LIGHT_CHASE(0.01),

    C1_HEARTBEAT_SLOW(0.03),
    C1_HEARTBEAT_MEDIUM(0.05),
    C1_HEARTBEAT_FAST(0.07),

    C1_BREATH_SLOW(0.09),
    C1_BREATH_FAST(0.11),
    
    C1_SHOT(0.13),
    C1_STROBE(0.15),
    // MERT: color 2 patterns
    C2_END_TO_END_BLEND_TO_BLACK(.17),
    C2_LARSON_SCANNER(.19),
    C2_LIGHT_CHASE(.21),
    C2_HEARTBEAT_SLOW(.23),
    C2_HEARTBEAT_MEDIUM(.25),
    C2_HEARTBEAT_FAST(.27),
    C2_BREATH_SLOW(.29),
    C2_BREATH_FAST(.31),
    C2_SHOT(.33),
    C2_STROBE(.35),

    // CAITIE: color 1 & 2 patterns
    SPARKLE_C1_ON_C2(0.37),
    SPARKLE_C2_ON_C1(0.39),
    COLOR_GRADIENT_C1_AND_C2(0.41),
    BPM_C1_AND_C2(0.43),
    END_TO_END_BLEND_C1_TO_C2(0.45),
    END_TO_END_BLEND(0.47),
    __SETUP__C1_AND_C2_NO_BLENDING(0.49),
    TWINKLES_C1_AND_C2(0.51),
    COLOR_WAVES_C1_AND_C2(0.53),
    SINELON_C1_AND_C2(0.55),

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
