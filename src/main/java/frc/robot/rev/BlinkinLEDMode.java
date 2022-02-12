package frc.robot.rev;

public enum BlinkinLEDMode 
{
    // DYLAN: fixed palette pattern (first page)
    RAINBOW_RAINBOW(-0.99),
    RAINBOW_PARTY(-0.97),

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
