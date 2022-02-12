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

    ;

    //#region Internals
    private double sparkValue;

    private BlinkinLEDMode(double sparkValue)
    {
        this.sparkValue = sparkValue;
    }
    //#endregion
}
