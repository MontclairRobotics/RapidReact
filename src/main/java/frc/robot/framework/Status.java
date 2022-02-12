package frc.robot.framework;

public enum Status 
{
    ENABLED,
    DISABLED
    ;

    public boolean isDisabled()
    {
        return this.equals(DISABLED);
    }
}
