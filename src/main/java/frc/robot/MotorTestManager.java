package frc.robot;

import edu.wpi.first.wpilibj.motorcontrol.Spark;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.XboxController.Axis;
import frc.robot.commands.MaxSpeedCommand;
import frc.robot.framework.Command;
import frc.robot.framework.CommandManager;
import frc.robot.framework.Commands;
import frc.robot.framework.RobotState;

public class MotorTestManager extends CommandManager
{
    public static final XboxController controller = new XboxController(0);
    public static final Spark sparkMax = new Spark(6);

    public static double maxSpeed = 1;

    @Override
    public void init()
    {
        enableDebug();

        addDefaultCommand(
            Commands.forever(
                () -> sparkMax.set(controller.getRawAxis(Axis.kLeftY.value) * maxSpeed)
            )
        );

        addDefaultCommand(
            new MaxSpeedCommand()
        );
    }
}
