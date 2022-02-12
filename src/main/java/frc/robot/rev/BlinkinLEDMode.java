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
