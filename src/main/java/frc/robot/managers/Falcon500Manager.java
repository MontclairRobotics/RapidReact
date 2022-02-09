c

public class Falcon500Manager extends CommandManager {

    public static final TalonFX talon = new TalonFX(0);
    public static final XboxController driverController = new XboxController(0);

    @Override
    public void init() {

        enableDebug();

        addCommand(
            Commands.forever(
                cmd -> {
                    var speed =  driverController.getRawAxis(XboxController.Axis.kLeftY.value);

                    
                    
                    talon.set(ControlMode.PercentOutput, speed);
                }
            ),
            RobotState.TELEOP
        );

    }
    
}
