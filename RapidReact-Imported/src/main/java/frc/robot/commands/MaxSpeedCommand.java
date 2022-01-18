package frc.robot.commands;

import frc.robot.MotorTestManager;
import frc.robot.framework.Command;

public class MaxSpeedCommand extends Command
{
    @Override
    public void execute() 
    {
        if(MotorTestManager.controller.getAButtonPressed())
        {
            if(MotorTestManager.maxSpeed == 1)
            {
                MotorTestManager.maxSpeed = 0.5;
            }
            else
            {
                MotorTestManager.maxSpeed = 1;
            }
        }
    }

    @Override
    public boolean finished() 
    {
        return false;
    }
}
