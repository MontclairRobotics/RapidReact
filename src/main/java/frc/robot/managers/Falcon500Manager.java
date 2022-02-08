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

<<<<<<< Updated upstream
                    // TODO: Why the fuck is this not working D Y L A N ! ! ! ! ! ! ! ! ! ! ! ! ! (love you btw <3)
                   // cmd.debug("Axis is " + speed);
=======
                    
>>>>>>> Stashed changes
                    
                    talon.set(ControlMode.PercentOutput, speed);
                }
            ),
            RobotState.TELEOP
        );

    }
    
}
