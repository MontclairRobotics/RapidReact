package frc.robot.framework.frc.controllers;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj.PS4Controller;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.framework.frc.commands.triggers.AnalogTrigger;

public abstract class GameController 
{
    public static enum Axis
    {
        LEFT_X,
        LEFT_Y,

        RIGHT_X,
        RIGHT_Y,

        LEFT_TRIGGER,
        RIGHT_TRIGGER
    }
    public static enum Button
    {
        A_CROSS,
        B_CIRCLE,
        Y_TRIANGLE,
        X_SQUARE,

        START_TOUCHPAD,

        LEFT_BUMPER,
        RIGHT_BUMPER,

        LEFT_STICK,
        RIGHT_STICK,
    }
    public static enum DPad
    {
        UP(0),
        RIGHT(90),
        DOWN(180),
        LEFT(270)
        ;

        private DPad(int angle)
        {
            this.angle = angle;
        }

        private int angle;
        public int getAngle() {return angle;}

        public static boolean get(DPad type, int pov)
        {
            switch(type)
            {
                case UP: return (0 <= pov && pov <= 45) || (360 - 45 <= pov && pov <= 360);
                case RIGHT: return (90 - 45 <= pov && pov <= 90 + 45);
                case DOWN: return (180 - 45 <= pov && pov <= 180 + 45);
                case LEFT: return (270 - 45 <= pov && pov <= 270 + 45);
            }

            throw new RuntimeException("!!!!");
        }
    }

    public static XboxController.Axis toXbox(Axis axisType)
    {
        switch(axisType)
        {
            case LEFT_X:
                return XboxController.Axis.kLeftX;
            case RIGHT_X:
                return XboxController.Axis.kRightX;
            case LEFT_Y:
                return XboxController.Axis.kLeftY;
            case RIGHT_Y: 
                return XboxController.Axis.kRightY;
            case LEFT_TRIGGER:
                return XboxController.Axis.kLeftTrigger;
            case RIGHT_TRIGGER:
                return XboxController.Axis.kRightTrigger;
        }
        return null;
    }

    public static PS4Controller.Axis toPS4(Axis axisType)
    {
        switch(axisType) 
        {
            case LEFT_X:
                return PS4Controller.Axis.kLeftX;
            case RIGHT_X:
                return PS4Controller.Axis.kRightX;
            case LEFT_Y:
                return PS4Controller.Axis.kLeftY;
            case RIGHT_Y:
                return PS4Controller.Axis.kRightY;
            case LEFT_TRIGGER:
                return PS4Controller.Axis.kL2;
            case RIGHT_TRIGGER:
                return PS4Controller.Axis.kR2;
        }
        return null;
    }

    public static XboxController.Button toXbox(Button buttonType)
    {
        switch(buttonType)
        {
            case A_CROSS:
                return XboxController.Button.kA;
            case B_CIRCLE:
                return XboxController.Button.kB;
            case X_SQUARE:
                return XboxController.Button.kX;
            case Y_TRIANGLE:
                return XboxController.Button.kY;

            case START_TOUCHPAD:
                return XboxController.Button.kStart;
            
            case LEFT_BUMPER:
                return XboxController.Button.kLeftBumper;
            case RIGHT_BUMPER:
                return XboxController.Button.kRightBumper;

            case LEFT_STICK:
                return XboxController.Button.kLeftStick;
            case RIGHT_STICK:
                return XboxController.Button.kRightStick;
        }
        return null;
    }

    public static PS4Controller.Button toPS4(Button buttonType)
    {
        switch(buttonType)
        {
            case A_CROSS:
                return PS4Controller.Button.kCross;
            case B_CIRCLE:
                return PS4Controller.Button.kCircle;
            case X_SQUARE:
                return PS4Controller.Button.kSquare;
            case Y_TRIANGLE:
                return PS4Controller.Button.kTriangle;

            case START_TOUCHPAD:
                return PS4Controller.Button.kTouchpad;
            
            case LEFT_BUMPER:
                return PS4Controller.Button.kL1;
            case RIGHT_BUMPER:
                return PS4Controller.Button.kR1;

            case LEFT_STICK:
                return PS4Controller.Button.kL3;
            case RIGHT_STICK:
                return PS4Controller.Button.kR3;
        }
        return null;
    }

    public static enum Type
    {
        XBOX,
        PS4,
    }
    
    public abstract boolean getButtonValue(Button type);
    public abstract boolean getButtonPressed(Button type);
    public abstract boolean getButtonReleased(Button type);
    
    public abstract double getAxisValue(Axis type);
    public abstract double getPOVValue();

    public abstract boolean getDPadRaw(DPad type);
    public abstract Type getType();

    public final Trigger getButton(Button type) 
    {
        return new Trigger(() -> getButtonValue(type));
    }
    public final Trigger getDPad(DPad type)
    {
        return new Trigger(() -> getDPadRaw(type));
    }
    public final AnalogTrigger getAxis(Axis type)
    {
        return new AnalogTrigger(() -> getAxisValue(type));
    }
    public final AnalogTrigger getPOV()
    {
        return new AnalogTrigger(() -> getPOVValue());
    }

    public static GameController xbox(int channel)
    {
        return new GameController()
        {
            private XboxController innerCont = new XboxController(channel);

            @Override
            public boolean getButtonValue(Button type) {
                return innerCont.getRawButton(toXbox(type).value);
            }

            @Override
            public boolean getDPadRaw(DPad type)
            {
                return DPad.get(type, innerCont.getPOV());
            }

            @Override
            public boolean getButtonPressed(Button type) {
                return innerCont.getRawButtonPressed(toXbox(type).value);
            }

            @Override
            public boolean getButtonReleased(Button type) {
                return innerCont.getRawButtonReleased(toXbox(type).value);
            }

            @Override
            public double getAxisValue(Axis type) {
                return innerCont.getRawAxis(toXbox(type).value);
            }

            @Override
            public Type getType() {
                return Type.XBOX;
            }

            @Override
            public double getPOVValue() {
                return innerCont.getPOV();
            }
        };
    }
    public static GameController ps4(int channel)
    {
        return new GameController()
        {
            private PS4Controller innerCont = new PS4Controller(channel);

            @Override
            public boolean getDPadRaw(DPad type)
            {
                return DPad.get(type, innerCont.getPOV());
            }

            @Override
            public boolean getButtonValue(Button type) {
                return innerCont.getRawButton(toPS4(type).value);
            }
            
            @Override
            public boolean getButtonPressed(Button type) {
                return innerCont.getRawButtonPressed(toPS4(type).value);
            }

            @Override
            public boolean getButtonReleased(Button type) {
                return innerCont.getRawButtonReleased(toPS4(type).value);
            }

            @Override
            public double getAxisValue(Axis type) {
                return innerCont.getRawAxis(toPS4(type).value);
            }

            @Override
            public Type getType() {
                return Type.PS4;
            }
            
            @Override
            public double getPOVValue() {
                return innerCont.getPOV();
            }
        };
    }

    public static GameController from(Type type, int channel)
    {
        return type.equals(Type.XBOX) ? xbox(channel) : ps4(channel);
    }
}
