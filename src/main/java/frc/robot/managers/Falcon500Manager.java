package frc.robot.managers;

import edu.wpi.first.wpilibj.XboxController;
import frc.robot.framework.CommandManager;
import frc.robot.framework.Commands;
import frc.robot.framework.RobotState;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;

public class Falcon500Manager extends CommandManager {

    public static final TalonFX talon = new TalonFX(0);
    public static final XboxController driverController = new XboxController(0);

    @Override
    public void init() {

        //enableDebug();

        addCommand(
            Commands.forever(
                cmd -> {
                    var speed =  driverController.getRawAxis(XboxController.Axis.kLeftY.value);

                    // TODO: Why the fuck is this not working D Y L A N ! ! ! ! ! ! ! ! ! ! ! ! ! (love you btw <3)
                    // cmd.debug("Axis is " + speed);
                    
                    talon.set(ControlMode.PercentOutput, speed);
                }
            ),
            RobotState.TELEOP
        );

    }
    
}
