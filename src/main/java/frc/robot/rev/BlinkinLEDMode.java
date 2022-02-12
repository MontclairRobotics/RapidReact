package frc.robot.rev;

public enum BlinkinLEDMode 
{
    // DYLAN: fixed palette pattern (first page)
    RAINBOW_RAINBOW(-0.99),
    RAINBOW_PARTY(-0.97),

    // LILA: fixed palette pattern (page 2)

    // ABE: color 1 patterns

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
