package frc.robot.framework.wpilib.controllers;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj.PS4Controller;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.framework.wpilib.triggers.AnalogTrigger;

public abstract class InputController 
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
    
    public abstract boolean getButton(Button type);
    public abstract boolean getButtonPressed(Button type);
    public abstract boolean getButtonReleased(Button type);
    
    public abstract double getAxis(Axis type);
    public abstract double getPOV();

    public abstract boolean getDPad(DPad type);
    public abstract Type getType();

    public final Trigger getButtonTrigger(Button type) 
    {
        return new Trigger(() -> getButton(type));
    }
    public final Trigger getDPadTrigger(DPad type)
    {
        return new Trigger(() -> getDPad(type));
    }
    public final AnalogTrigger getAxisTrigger(Axis type)
    {
        return new AnalogTrigger(() -> getAxis(type));
    }
    public final AnalogTrigger getPOVTrigger()
    {
        return new AnalogTrigger(() -> getPOV());
    }

    public static InputController xbox(int channel)
    {
        return new InputController()
        {
            private XboxController innerCont = new XboxController(channel);

            @Override
            public boolean getButton(Button type) {
                return innerCont.getRawButton(toXbox(type).value);
            }

            @Override
            public boolean getDPad(DPad type)
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
            public double getAxis(Axis type) {
                return innerCont.getRawAxis(toXbox(type).value);
            }

            @Override
            public Type getType() {
                return Type.XBOX;
            }

            @Override
            public double getPOV() {
                return innerCont.getPOV();
            }
        };
    }
    public static InputController ps4(int channel)
    {
        return new InputController()
        {
            private PS4Controller innerCont = new PS4Controller(channel);

            @Override
            public boolean getDPad(DPad type)
            {
                return DPad.get(type, innerCont.getPOV());
            }

            @Override
            public boolean getButton(Button type) {
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
            public double getAxis(Axis type) {
                return innerCont.getRawAxis(toPS4(type).value);
            }

            @Override
            public Type getType() {
                return Type.PS4;
            }
            
            @Override
            public double getPOV() {
                return innerCont.getPOV();
            }
        };
    }

    public static InputController from(Type type, int channel)
    {
        return type.equals(Type.XBOX) ? xbox(channel) : ps4(channel);
    }
}
