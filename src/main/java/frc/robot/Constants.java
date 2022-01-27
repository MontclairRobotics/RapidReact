// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.utilities.smoothing.LinearSmoother;
import frc.robot.utilities.smoothing.NullSmoother;
import frc.robot.utilities.smoothing.Smoother;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants 
{
    // Left motor port numbers
    public static final int LEFT_MOTOR_1_PORT = 0;
    public static final int LEFT_MOTOR_2_PORT = 1;
    public static final int LEFT_MOTOR_3_PORT = 2;

    // Right motor port numbers
    public static final int RIGHT_MOTOR_1_PORT = 3;
    public static final int RIGHT_MOTOR_2_PORT = 4;
    public static final int RIGHT_MOTOR_3_PORT = 5;

    // Port for xbox controller
    public static final int XBOX_CONTROLLER_PORT = 0;

    public static final Smoother DRIVE_SMOOTHER 
        = new LinearSmoother(0, -1, 1, 0.1);
    public static final NullSmoother DRIVE_NULL_SMOOTHER
        = new NullSmoother(0, -1, 1);
}
