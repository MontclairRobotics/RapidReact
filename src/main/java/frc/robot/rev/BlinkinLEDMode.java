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
