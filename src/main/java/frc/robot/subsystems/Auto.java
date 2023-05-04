package frc.robot.subsystems;

import frc.robot.RapidReact;
import frc.robot.framework.HashMaps;
import frc.robot.framework.Logging;
import frc.robot.framework.Trajectories;
import frc.robot.framework.commandrobot.ManagerBase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

import edu.wpi.first.math.controller.RamseteController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.FieldObject2d;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.Commands;

import frc.robot.Constants;


import com.pathplanner.lib.PathConstraints;
import com.pathplanner.lib.PathPlannerTrajectory;
import com.pathplanner.lib.auto.BaseAutoBuilder;
import com.pathplanner.lib.auto.RamseteAutoBuilder;
import com.pathplanner.lib.auto.SwerveAutoBuilder;

/**
 * AutoCommandsManager
 */
public class Auto extends ManagerBase
{
    private static final ShuffleboardTab autoTab = Shuffleboard.getTab("Auto");
    public static ShuffleboardTab getAutoTab() {return autoTab;}

    private final SendableChooser<String> chooseStart;
    private final GenericEntry leaveCommunity;
    private final GenericEntry scoreTwice;
    private final GenericEntry balance;
    private final GenericEntry pointless;
    private final GenericEntry pickupTwice;
    private final GenericEntry autoStringEntry;
    private final GenericEntry firstIsHigh;

    private String autoString = "";
    private boolean lastIsHigh;
    private Pose2d startPose = new Pose2d();
    private final FieldObject2d start;
    
    public Auto()
    {
        chooseStart = new SendableChooser<String>();
        chooseStart.setDefaultOption("Cube 1", "Cone 3"); 
        chooseStart.addOption("Cube 2", "Cone 4");
        chooseStart.addOption("Cube 3", "Cone 6");

        autoTab.add("Starting Position", chooseStart)
            .withPosition(0,0)
            .withSize(2, 1);
        
        leaveCommunity = autoTab.add("Leave Community", false)
            .withWidget(BuiltInWidgets.kToggleSwitch)
            .withPosition(0, 1)
            .withSize(2, 1).getEntry();
        scoreTwice = autoTab.add("Score Twice", false)
            .withWidget(BuiltInWidgets.kToggleSwitch)
            .withPosition(0,2)
            .withSize(2, 1).getEntry();
        balance = autoTab.add("Balance", false)
            .withWidget(BuiltInWidgets.kToggleSwitch)
            .withPosition(0, 3)
            .withSize(2, 1).getEntry();
        pointless = autoTab.add("Pointless", false)
            .withWidget(BuiltInWidgets.kToggleButton)
            .withPosition(8, 0)
            .withSize(2, 1).getEntry();
        pickupTwice = autoTab.add("Pickup Twice", false)
            .withWidget(BuiltInWidgets.kToggleSwitch)
            .withPosition(0, 4)
            .withSize(2, 1).getEntry();

        autoTab
            .addString("Recent Log", Logging::mostRecentLog)
            .withWidget(BuiltInWidgets.kTextView)
            .withSize(3, 1)
            .withPosition(3, 4);

        
        autoStringEntry = autoTab.add("Command String", autoString)
            .withWidget(BuiltInWidgets.kTextView)
            .withPosition(8, 1)
            .withSize(2, 1)
            .getEntry();
        firstIsHigh = autoTab
            .add("First Score is High", false)
            .withWidget(BuiltInWidgets.kToggleSwitch)
            .withPosition(8, 2)
            .withSize(2, 1)
            .getEntry();

        // autoTab.add(ChargedUp.getField()).withSize(7, 4).withPosition(2, 0);
        start = RapidReact.field.getObject("Start");

        // Hook tunables
        final Consumer<Double> whenUpdate = x -> updateAutoCommand();

        Constants.DRIVE_TIME_BEFORE_BALANCE.whenUpdate(whenUpdate);
        Constants.DRIVE_TIME_AFTER_BALANCE_CLIP.whenUpdate(whenUpdate);

        Constants.MAX_VEL.whenUpdate(whenUpdate);
        Constants.MAX_ACC.whenUpdate(whenUpdate);

        Constants.CHARGER_STATION_MUL.whenUpdate(whenUpdate);
        Constants.CHARGER_STATION_AT_REST_DEBOUNCE_TIME.whenUpdate(whenUpdate);
    }

    private Command command = null;

    public Command get()
    {
        if(command == null)
        {
            Logging.errorNoTrace("Tried to get an auto command when none was created!");
            return Commands.none();
        }

        return command;
    }

    private void updateAutoCommand()
    {
        String str = getAutoString(
            chooseStart.getSelected(), 
            leaveCommunity.getBoolean(false), 
            scoreTwice.getBoolean(false), 
            balance.getBoolean(false),
            pickupTwice.getBoolean(false)
        );
        
        if      (chooseStart.getSelected().equals("Cone 3")) startPose = new Pose2d(1.85, 3.85, Rotation2d.fromDegrees(180));
        else if (chooseStart.getSelected().equals("Cone 4")) startPose = new Pose2d(1.85, 3.30, Rotation2d.fromDegrees(180));
        else if (chooseStart.getSelected().equals("Cone 6")) startPose = new Pose2d(1.85, 0.45, Rotation2d.fromDegrees(180));
        
        if (DriverStation.getAlliance() == Alliance.Red)
        {
            // startPose = startPose.relativeTo(new Pose2d(16.5, 8, Rotation2d.fromDegrees(180)));
            if      (chooseStart.getSelected().equals("Cone 3")) startPose = new Pose2d(16.5-1.85, 3.85, Rotation2d.fromDegrees(180));
            else if (chooseStart.getSelected().equals("Cone 4")) startPose = new Pose2d(16.5-1.85, 3.30, Rotation2d.fromDegrees(180));
            else if (chooseStart.getSelected().equals("Cone 6")) startPose = new Pose2d(16.5-1.85, 0.45, Rotation2d.fromDegrees(180));
        }
        // start.setPose(startPose);

        command = buildAuto(str, firstIsHigh.getBoolean(false));

        if(command != null)
        {
            Logging.info("Created the autonomous sequence '" + str + "' successfully!");
        }
        else 
        {
            command = Commands.none();
        }
    }

    String previous = "";

    @Override
    public void always() 
    {
        String current = getAutoString(
            chooseStart.getSelected(), 
            leaveCommunity.getBoolean(false), 
            scoreTwice.getBoolean(false), 
            balance.getBoolean(false),
            pickupTwice.getBoolean(false)
        );

        boolean currentIsHigh = firstIsHigh.getBoolean(false);

        if(!current.equals(previous) || lastIsHigh != currentIsHigh)
        {
            updateAutoCommand();
        }

        previous = current;
        autoString = current;

        lastIsHigh = currentIsHigh;

        pointless.setBoolean(false);
        if (autoString != null) autoStringEntry.setString(autoString);
    }

    //orange juice because i said so. "Cesca is so awesome" - Dylan & Abe (simultaneously)
    

    //// SEQUENCE PARSING ///////

    /**
     * Returns a parseable auto string using inputted parameters retrieved from Shuffleboard
     * Auto string is parsed using {@link #lex}
     * 
     * @param start where the robot starts, 1 for Cone 3, 2 for Cone 4, 3 for Cone 6
     * @param exitComm if the robot exits the community during auto
     * @param scoreTwice if the robot scores twice
     * @param balance if the robot balances
     * @param pickupTwice if the robot tries to pickup a second cube after scoring twice
     * @return the parseable auto string
     */
     
    private static String getAutoString(String start, boolean exitComm, boolean scoreTwice, boolean balance, boolean pickupTwice)
    {
        String str = "";

        switch(start) 
        {
            case "Cone 3": 
                str += "1";
                if(!exitComm) break; 

                str += "A";
                if(scoreTwice) str += "4";
                if(pickupTwice) str += "D";
                break;

            case "Cone 4": 
                str += "2";
                break;

            case "Cone 6": 
                str += "3";
                if(!exitComm) break; 
               
                str += "C";
                if (scoreTwice) str += "5";
                if (pickupTwice) str += "E";
                break;

            default:
                Logging.errorNoTrace("Invalid start position: " + start + ", go read a self-help book :)");
                return null;
        } 

        if(balance && !(pickupTwice && start.equals("Cone 3"))) str += "B";

        return str;
    }

    /**
     * Lex an autonomous sequence string into its components.
     * Skips over any whitespace characters and appends modifiers 
     * to their bases ("!A" remains conjoined while " A" becomes "A").
     * 
     * @param str The autonomous sequence string
     * @return The components of the path (i.e. '1', 'A', or '!1'), or null if lexing fails
     */
    private static String[] lex(String str)
    {
        if(str.length() == 0)
        {
            Logging.errorNoTrace("Empty auto command provided.");
            return null;
        }

        ArrayList<String> out = new ArrayList<String>();
        boolean isExclaimed = false;

        for(int i = 0; i < str.length(); i++)
        {
            char c = str.charAt(i);

            // Skip whitespace
            if(Character.isWhitespace(c))
            {
                continue;
            }
            // If we have a valid position, add it
            else if(c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == 'A' || c == 'B' || c == 'C' || c == 'a' || c == 'b' || c == 'c' || c == 'D' || c == 'd' || c == 'E' || c == 'e')
            {
                c = Character.toUpperCase(c);//test

                if(isExclaimed) out.add("!" + c);
                else            out.add(""  + c);

                isExclaimed = false;
            }
            // Check exclaimed
            else if(c == '!')
            {
                if(isExclaimed)
                {
                    Logging.errorNoTrace("Dual '!' present in command string! '" + str + "'");
                    return null;
                }

                isExclaimed = true;
            }
            // Error otherwise
            else 
            {
                Logging.errorNoTrace("Unexpected character " + c + " in command string! '" + str + "'");
                return null;
            }
        }

        if(isExclaimed)
        {
            Logging.errorNoTrace("Unterminated exclamation expression in command string '" + str + "'");
            return null;
        }

        return out.toArray(String[]::new);
    }
    
    /**
     * Parse an autonomous sequence string into the commands which it will execute.
     * 
     * First, this method lexes the inpur using {@link #lex(String)}, then
     * generates the list of commands which the sequence will compris, including
     * both actions like "A" or "B" and transitions like "AB" and "1C".
     * 
     * Skips any commands attributed with '!'.
     * 
     * @param str The autonomous sequence string
     * @return A list which contains identifiers for the commands which comprise the autonomous routine, 
     * or null if lexing or parsing fails
     */
    public static String[] parse(String str) 
    {
        //hi
        // Lex
        String[] lex = lex(str);

        // Handle errors
        if(lex == null) 
        {
            return null;
        }

        // Parse
        ArrayList<String> output = new ArrayList<String>();

        for (int i = 0; i < lex.length-1; i++)
        {
            String commd = lex[i];
            String trans = lex[i] + lex[i+1];

            if(!commd.contains("!")) 
            {
                output.add(commd);
            }

            output.add(trans.replace("!", ""));
        }

        if(!lex[lex.length - 1].contains("!"))
        {
            output.add(lex[lex.length - 1]);
        }

        return output.toArray(String[]::new);
    }


    /**
     * Get the action command which corresponds to the given auto sequence string segment. 
     * This method can either take in transitions or actions.
     * 
     * @return The command, or none() with a log if an error occurs
     */
    public static CommandBase fromStringToCommand(String str, String full, boolean firstIsHigh, BaseAutoBuilder autoBuilder, ArrayList<PathPlannerTrajectory> trajectories)
    {
        // Single actions
        if(str.length() == 1) //DEPLOY CODE PLSSSSS
        {
            switch(str)
            {
                case "A": 
                case "C": //return drivetrain.commands.goToAngleAbsolute(Rotation2d.fromDegrees(0)).withTimeout(0.3).andThen(pickup(0.75));

                case "D": //{
                  //  double angle = DriverStation.getAlliance() == Alliance.Blue ? 270 : 90;
                  //  return drivetrain.commands.goToAngleAbsolute(Rotation2d.fromDegrees(angle)).withTimeout(0.3).andThen(pickup(1.1));
               // }
                case "E": //{
                   // double angle = DriverStation.getAlliance() == Alliance.Blue ? 90 : 270;
                   // return drivetrain.commands.goToAngleAbsolute(Rotation2d.fromDegrees(angle)).withTimeout(0.3).andThen(pickup(1.1));
               // }
                case "1":
                case "2":
                case "3": 
                // {
                //     if(firstIsHigh)
                //     {
                //         boolean resetAfterScoring = (full.length() == 2 && full.charAt(1) == 'B');
                //         return scoreHighShelf(true, resetAfterScoring);
                //     }
                //     else 
                //     {
                //         return scoreCubeLow(false);
                //     } 
                // }

                case "4":
                case "5": 
                // {
                //     // Parallelization may occur here
                //     if(full.endsWith(str)) 
                //     {
                //         return scoreCubeLow(false);
                //     }
                //     else 
                //     {
                //         return Commands.none();
                //     }
                // }

                case "B": //return drivetrain.commands.driveForTimeRelative(Constants.Auto.DRIVE_TIME_BEFORE_BALANCE.get(), 0, DriveConstants.MAX_SPEED_MPS, 0)
                    // .until(() -> Math.abs(drivetrain.getChargeStationAngle()) > 10)
                    // .andThen(waitSeconds(1))
                    // .andThen(drivetrain.commands.driveForTimeRelative(Constants.Auto.DRIVE_TIME_AFTER_BALANCE_CLIP.get(), 0, DriveConstants.MAX_SPEED_MPS, 0))
                    // .andThen(balanceOriginal());

                default: 
                {
                    Logging.error("Invalid path point! Please be better.");
                    return null;
                }
            }
        }
        // Transition
        else if(str.length() == 2)
        {
            if(!Trajectories.exists(str))  
            {
                Logging.errorNoTrace("No transition '" + str + "' found!");
                return null;
            }

            PathConstraints constraints = Constants.constraints();

            if(str.charAt(1) == 'B')
            {
                constraints = new PathConstraints(4, 2);
            }

            PathPlannerTrajectory nextTrajectory = Trajectories.get(str, constraints);

            trajectories.add(nextTrajectory);

            CommandBase cmd = RapidReact.auto.trajectory(autoBuilder, nextTrajectory);

            // Check for first path and add a reset if it is
            if(trajectories.size() == 1)
            {
                cmd = autoBuilder.resetPose(nextTrajectory).andThen(cmd);
            }

            return cmd.withName(str);
        }
        // Error
        else 
        {
            Logging.errorNoTrace("More than two or zero characters in received string!");
            return null;
        }
    }


    /**
     * Create the autonomous command from the given parsed autonomous sequence string.
     * 
     * @return The command, with none() inserted into places in the sequence where an error occured.
     */
    public static CommandBase buildAuto(String full, boolean firstIsHigh, String[] list)
    {
        Trajectories.clearAll();
        CommandBase[] commandList = new CommandBase[list.length];
        ArrayList<PathPlannerTrajectory> allTrajectories = new ArrayList<>();
        

        // Get the auto builder //
        HashMap<String, Command> markers = HashMaps.of(
            // "Elevator Mid Peg"   , elevatorToConeHigh(),
            // "Elevator Mid Shelf" , elevatorToCubeHigh(),
            // "Score Low"          , scoreCubeLow(false), 
            // "Intake On"          , shwooperSuck(),
            // "Retract"            , elevatorStingerReturn(),
            // "Intake Off"         , Commands.sequence(stopShwooper(), closeGrabber()),
            // "Pickup Pipeline"    , waitForPipe(() -> DetectionType.CONE)
        );

        String debugAuto = "";
        RamseteAutoBuilder builder = RapidReact.auto.autoBuilder(markers);

        // Iterate all of the string segments
        for (int i = 0; i < list.length; i++)
        {
            commandList[i] = fromStringToCommand(list[i], full, firstIsHigh, builder, allTrajectories);

            // Error out here if necessary
            if(commandList[i] == null)
            {
                Logging.info("SOMEHTING NULLL JGINSJGSNGJIFNGJ");
                return null;
            }

            debugAuto = debugAuto + commandList[i].getName() + ", ";
        }

        // Calculate the sum trajectory
        if(allTrajectories.size() > 0)
        {
            Trajectories.displayAll(allTrajectories);
        }

        // Parallelize the first score
        if(!firstIsHigh && commandList.length > 1)
        {
            CommandBase first = commandList[0];
            commandList[0] = Commands.none();
            commandList[1] = commandList[1].alongWith(first);
        }
        
        // Return the sum command (with a navx set-180)
        Logging.info("[AUTO BUILD]" + debugAuto);

        // return timed(Commands.runOnce(gyroscope::setSouth) //TODO figure out starting gyroscope direction
        //     .andThen(Commands.sequence(commandList))
        //     .withName("Auto " + full));

        return timed(Commands.runOnce(RapidReact.navx::zeroYaw)
            .andThen(Commands.sequence(commandList))
            .withName("Auto " + full));
    }
    
    /**
     * Create the autonomous command from the given parsed autonomous sequence string.
     * 
     * @return The command, with none() inserted into places in the sequence where an error occured,
     * or none() itself if parsing or lexing fails.
     */
    public static CommandBase buildAuto(String full, boolean firstIsHigh) 
    {
        String[] parts = Auto.parse(full);

        if(parts == null) 
        {
            return null;
        }
        Logging.info(Arrays.toString(parts));

        return buildAuto(full, firstIsHigh, parts);
    }


    public RamseteAutoBuilder autoBuilder(HashMap<String, Command> markers)
    {
        return new RamseteAutoBuilder( //TODO do we want to use PID? how do we handle motor voltages?
            RapidReact.drivetrain::getRobotPose,
            RapidReact.drivetrain::setRobotPose,
            new RamseteController(),
            Constants.KINEMATICS,
            RapidReact.drivetrain::set,
            markers,
            true,
            RapidReact.drivetrain
        );
    }

    /**
     * Create a full autonomous command using the given path planner trajectory.
     * @param trajectory The trajectory
     * @param markers The autonomous markers
     * @return The command
     */
    public CommandBase trajectory(BaseAutoBuilder autoBuilder, PathPlannerTrajectory trajectory)
    {
        return autoBuilder.followPathWithEvents(trajectory);
    }
    /**
     * Create a full autonomous command using the given path planner trajectory name.
     * @param trajectoryName The trajectory name
     * @return The command
     */
    public CommandBase trajectory(BaseAutoBuilder autoBuilder, String trajectoryName)
    {
        return trajectory(autoBuilder, Trajectories.get(trajectoryName, Constants.constraints()))
            .withName("Trajectory " + trajectoryName);
    }

    public static CommandBase timed(Command cmd)
    {
        double[] time = {0};
        return cmd
            .beforeStarting(() -> time[0] = Timer.getFPGATimestamp())
            .andThen(log(() -> "Finished " + cmd.getName() + " in " + (Timer.getFPGATimestamp() - time[0]) + " seconds!"));
    }

    public static CommandBase log(Supplier<String> str) 
    {
        return Commands.runOnce(() -> Logging.info(str.get()));
    }



}

