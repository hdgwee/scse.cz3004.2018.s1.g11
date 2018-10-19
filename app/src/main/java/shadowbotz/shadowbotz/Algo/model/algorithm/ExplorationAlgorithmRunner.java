package shadowbotz.shadowbotz.Algo.model.algorithm;

import android.app.Activity;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import shadowbotz.shadowbotz.Algo.model.entity.Grid;
import shadowbotz.shadowbotz.Algo.model.entity.Robot;
import shadowbotz.shadowbotz.Algo.model.util.MessageMgr;
import shadowbotz.shadowbotz.Algo.model.util.SocketMgr;
import shadowbotz.shadowbotz.Config;
import shadowbotz.shadowbotz.Model.BluetoothMessage;
import shadowbotz.shadowbotz.View.MainActivity;

import static shadowbotz.shadowbotz.Algo.constant.CommConstants.TARGET_ARDUINO;
import static shadowbotz.shadowbotz.Algo.constant.MapConstants.MAP_COLS;
import static shadowbotz.shadowbotz.Algo.constant.MapConstants.MAP_ROWS;
import static shadowbotz.shadowbotz.Algo.constant.RobotConstants.EAST;
import static shadowbotz.shadowbotz.Algo.constant.RobotConstants.LEFT;
import static shadowbotz.shadowbotz.Algo.constant.RobotConstants.NORTH;
import static shadowbotz.shadowbotz.Algo.constant.RobotConstants.RIGHT;
import static shadowbotz.shadowbotz.Algo.constant.RobotConstants.SOUTH;

/**
 * Algorithm for exploration phase (full exploration)
 */
public class ExplorationAlgorithmRunner implements AlgorithmRunner {

    private int sleepDuration;
    private int phantomWall =0;
    private int forwardCounter=0;
    private static final int START_X = 0;
    private static final int START_Y = 17;
    private static final int CALIBRATION_LIMIT = 5;
    public ExplorationAlgorithmRunner(int speed){
//        sleepDuration = 1000 / speed;
        sleepDuration = 0;
    }

    private Activity activity = null;

    @Override
    public void run(Grid grid, Robot robot, boolean realRun) {
        grid.reset();
        robot.reset();

        // SELECT EITHER ONE OF THE METHODS TO RUN ALGORITHMS.
        runExplorationAlgorithmThorough(grid, robot, realRun);

        // CALIBRATION AFTER EXPLORATION
        calibrateAndTurn(robot, realRun);

        // GENERATE MAP DESCRIPTOR, SEND TO ANDROID
        Log.i("Algo", "End of ExplorationAlgorithmRunner");
        sendMessageToAndroidInternally(MessageMgr.generateMapDescriptorMsgAndroid(grid.generateDescriptorPartOne(), grid.generateDescriptorPartTwo(), "", robot.getCenterPosX(), robot.getCenterPosY(), robot.getHeading()));
        sendMessageToAndroidInternally("{\"message\":\"End of Exploration\"}");
    }

    private void calibrateAndTurn(Robot robot, boolean realRun) {
        if (realRun) {
            while (robot.getHeading() != NORTH) {
                Log.i("Algo", "Turning");
                robot.turn(LEFT);
                SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "L");
                robot.sense(realRun);
            }
            robot.sense1(true);
            robot.sense1(true);
        }
    }

    private void runExplorationAlgorithmThorough(Grid grid, Robot robot, boolean realRun) {
        boolean endZoneFlag = false;
        boolean startZoneFlag = false;

        // CALIBRATE & SENSE
        int calibrationCounter = 0;

        // INITIAL UPDATE OF MAP TO ANDROID
        if (realRun)
            sendMessageToAndroidInternally( MessageMgr.generateMapDescriptorMsgAndroid(grid.generateDescriptorPartOne(), grid.generateDescriptorPartTwo(), "", robot.getCenterPosX(), robot.getCenterPosY(), robot.getHeading()));

        // MAIN LOOP (LEFT-WALL-FOLLOWER)
        while (!endZoneFlag || !startZoneFlag) {

            // CHECK IF TURNING IS NECESSARY
            boolean turned = leftWallFollower(robot, grid, realRun);

            if (turned) {
                //Reset forward movement
                forwardCounter = 0;

                robot.sense(realRun);
                // CALIBRATION
                if (realRun) {
                    calibrationCounter++;
                    // IF CAN CALIBRATE FRONT, TAKE THE OPPORTUNITY
                    // OTHERWISE CALIBRATE LEFT
                    if (robot.canCalibrateFront() && robot.canCalibrateLeft()) {
                        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C");
                        calibrationCounter = 0;
                    } else if (robot.canCalibrateFront()) {
                        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C2");
                        calibrationCounter = 0;
                    } else if (calibrationCounter >= CALIBRATION_LIMIT && robot.canCalibrateLeft()) {
                        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C3");

                        calibrationCounter = 0;
                    }
                }

                if(calibrationCounter == 0) {
                    // SENSE AFTER CALIBRATION
                    senseAndUpdateAndroid(robot, grid, realRun);
                }
            }

            // MOVE FORWARD
            if (realRun) {
                if(!robot.isObstacleAhead()) {
                    SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "M1");
                    robot.move();

                    stepTaken();
                    senseAndUpdateAndroid(robot, grid, realRun);
                    checkPhantomWall(robot, realRun, grid);
                } else
                    continue;
            }

            // CALIBRATION
            if (realRun) {
                calibrationCounter++;
                // IF CAN CALIBRATE FRONT, TAKE THE OPPORTUNITY
                // OTHERWISE CALIBRATE LEFT
                if (robot.canCalibrateFront() && robot.canCalibrateLeft()) {
                    SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C");
                    calibrationCounter = 0;
                } else if (robot.canCalibrateFront()) {
                    SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C2");
                    calibrationCounter = 0;
                } else if (calibrationCounter >= CALIBRATION_LIMIT && robot.canCalibrateLeft()) {
                    SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C3");
                    calibrationCounter = 0;
                }

                if(calibrationCounter == 0) {
                    // SENSE AFTER CALIBRATION
                    senseAndUpdateAndroid(robot, grid, realRun);
                }
            }

            if (Grid.isInEndZone(robot.getPosX(), robot.getPosY())) {
                endZoneFlag = true;
            }
            if (endZoneFlag && Grid.isInStartZone(robot.getPosX() + 2, robot.getPosY())) {
                startZoneFlag = true;
            }

            // IF EXPLORATION COMPLETED & HAVE NOT GO BACK TO START, FIND THE FASTEST PATH BACK TO START POINT
            if(grid.checkExploredPercentage() == 100 && !startZoneFlag){
                Robot fakeRobot = new Robot(grid, new ArrayList<>());
                fakeRobot.setPosX(robot.getPosX());
                fakeRobot.setPosY(robot.getPosY());
                fakeRobot.setHeading(robot.getHeading());
                List<String> returnPath = AlgorithmRunner.runAstar(robot.getPosX(), robot.getPosY(), START_X, START_Y, grid, fakeRobot);
                fakeRobot.setPosX(robot.getPosX());
                fakeRobot.setPosY(robot.getPosY());
                fakeRobot.setHeading(robot.getHeading());
                String compressed = AlgorithmRunner.compressPathWithC2calibration(returnPath, fakeRobot);

                if (returnPath != null) {
                    Log.i("Algo", "Algorithm finished, executing actions");
                    System.out.println(returnPath.toString());
                    if (realRun) {
                        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, compressed);
                    }

                    for (String action : returnPath) {
                        if (action.equals("M")) {
                            robot.move();
                        } else if (action.equals("L")) {
                            robot.turn(LEFT);
                        } else if (action.equals("R")) {
                            robot.turn(RIGHT);
                        } else if (action.equals("U")) {
                            robot.turn(LEFT);
                            robot.turn(LEFT);
                        }
                        if (realRun)
                            sendMessageToAndroidInternally( MessageMgr.generateMapDescriptorMsgAndroid(grid.generateDescriptorPartOne(), grid.generateDescriptorPartTwo(), "", robot.getCenterPosX(), robot.getCenterPosY(), robot.getHeading()));

                        stepTaken();
                    }

                    robot.sense(realRun);
                } else {
                    Log.i("Algo", "Fastest path not found!");
                }

                if (endZoneFlag && Grid.isInStartZone(robot.getPosX() + 2, robot.getPosY())) {
                    startZoneFlag = true;
                }
                //AT THIS STAGE, ROBOT SHOULD HAVE RETURNED BACK TO START POINT.
            }
        }

        //
        // BELOW IS THE 2ND EXPLORATION !!!!!!!!!!!!!
        //
        // INITIALISE NEW GRID TO PREVENT CHECKING PREVIOUSLY EXPLORED CELLS.
        Grid exploreChecker = new Grid();
        for (int x = 0; x < MAP_COLS; x++) {
            for (int y = 0; y < MAP_ROWS; y++) {
                exploreChecker.setExplored(x, y, grid.getIsExplored(x, y));
                exploreChecker.setIsObstacle(x, y, grid.getIsObstacle(x, y));
            }
        }

        // SWEEPING THROUGH UNEXPLORED, BUT REACHABLE CELLS WITHIN ARENA.
        if(grid.checkExploredPercentage() < 100.0){ // CHECK FOR UNEXPLORED CELLS
            Log.i("Algo", "NOT FULLY EXPLORED, DOING A 2ND RUN!");
            for (int y = MAP_ROWS - 1; y >= 0; y--) {
                for (int x = MAP_COLS - 1; x >= 0; x--) {
                    // CHECK FOR UNEXPLORED CELLS && CHECK IF NEIGHBOURS ARE REACHABLE OR NOT
                    if (!grid.getIsExplored(x, y) &&
                            ((checkUnexplored(robot, grid, x + 1, y, realRun)
                                    || checkUnexplored(robot, grid, x - 1, y, realRun)
                                    || checkUnexplored(robot, grid, x, y + 1, realRun)
                                    || checkUnexplored(robot, grid, x, y - 1, realRun)))) {
                        boolean startPointFlag = true;
                        while (startPointFlag) { // SET STARTPOINTFLAG TO TRUE TO INITIATE LEFT-WALL-FOLLOWER
                            startPointFlag = false; // SETSTARTPOINT FLAG TO FALSE TO SAY THAT ROBOT IS NOT AT START POINT.
                            boolean turned = leftWallFollower(robot, grid, realRun);

                            if (turned) {
                                // CALIBRATION
                                if (realRun) {
                                    calibrationCounter++;
                                    // IF CAN CALIBRATE FRONT, TAKE THE OPPORTUNITY
                                    // OTHERWISE CALIBRATE LEFT
                                    if (robot.canCalibrateFront() && robot.canCalibrateLeft()) {
                                        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C");
                                        calibrationCounter = 0;
                                    } else if (robot.canCalibrateFront()) {
                                        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C2");
                                        calibrationCounter = 0;
                                    } else if (calibrationCounter >= CALIBRATION_LIMIT && robot.canCalibrateLeft()) {
                                        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C3");
                                        calibrationCounter = 0;
                                    }
                                }

                                // SENSE AFTER CALIBRATION
                                senseAndUpdateAndroid(robot, grid, realRun);
                            }

                            // MOVE FORWARD
                            if (realRun)
                                SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "M1");
                            robot.move();
                            stepTaken();

                            // CALIBRATION
                            if (realRun) {
                                calibrationCounter++;
                                // IF CAN CALIBRATE FRONT, TAKE THE OPPORTUNITY
                                // OTHERWISE CALIBRATE LEFT
                                if (robot.canCalibrateFront() && robot.canCalibrateLeft()) {
                                    SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C");
                                    calibrationCounter = 0;
                                } else if (robot.canCalibrateFront()) {
                                    SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C2");
                                    calibrationCounter = 0;
                                } else if (calibrationCounter >= CALIBRATION_LIMIT && robot.canCalibrateLeft()) {
                                    SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C3");
                                    calibrationCounter = 0;
                                }
                            }

                            // SENSE AFTER CALIBRATION
                            senseAndUpdateAndroid(robot, grid, realRun);

                            if (grid.checkExploredPercentage() == 100) { // IF FULLEST EXPLORED, EXIT AND GO TO START
                                break;
                            }

                            while (exploreChecker.getIsExplored(robot.getPosX(), robot.getPosY()) != grid.getIsExplored(robot.getPosX(), robot.getPosY())) {
                                if (grid.checkExploredPercentage() == 100) { // IF FULLEST EXPLORED, EXIT AND GO TO START
                                    break;
                                }
                                turned = leftWallFollower(robot, grid, realRun);

                                if (turned) {
                                    // CALIBRATION
                                    if (realRun) {
                                        calibrationCounter++;
                                        // IF CAN CALIBRATE FRONT, TAKE THE OPPORTUNITY
                                        // OTHERWISE CALIBRATE LEFT
                                        if (robot.canCalibrateFront() && robot.canCalibrateLeft()) {
                                            SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C");
                                            calibrationCounter = 0;
                                        } else if (robot.canCalibrateFront()) {
                                            SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C2");
                                            calibrationCounter = 0;
                                        } else if (calibrationCounter >= CALIBRATION_LIMIT && robot.canCalibrateLeft()) {
                                            SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C3");
                                            calibrationCounter = 0;
                                        }
                                    }

                                    // SENSE AFTER CALIBRATION
                                    senseAndUpdateAndroid(robot, grid, realRun);
                                }

                                // MOVE FORWARD
                                if (realRun)
                                    SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "M1");
                                robot.move();
                                stepTaken();

                                // CALIBRATION
                                if (realRun) {
                                    calibrationCounter++;
                                    // IF CAN CALIBRATE FRONT, TAKE THE OPPORTUNITY
                                    // OTHERWISE CALIBRATE LEFT
                                    if (robot.canCalibrateFront() && robot.canCalibrateLeft()) {
                                        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C");
                                        calibrationCounter = 0;
                                    } else if (robot.canCalibrateFront()) {
                                        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C2");
                                        calibrationCounter = 0;
                                    } else if (calibrationCounter >= CALIBRATION_LIMIT && robot.canCalibrateLeft()) {
                                        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C3");
                                        calibrationCounter = 0;
                                    }
                                }

                                // SENSE AFTER CALIBRATION
                                senseAndUpdateAndroid(robot, grid, realRun);
                            }
                        }
                    }
                }
            }
            /*
            FASTEST PATH BACK TO START ONCE THE EXPLORATION IS COMPLETED.
            */
            if(!Grid.isInStartZone(robot.getPosX()+2, robot.getPosY()+2)){
                Robot fakeRobot = new Robot(grid, new ArrayList<>());
                fakeRobot.setPosX(robot.getPosX());
                fakeRobot.setPosY(robot.getPosY());
                fakeRobot.setHeading(robot.getHeading());
                List<String> returnPath = AlgorithmRunner.runAstar(robot.getPosX(), robot.getPosY(), START_X, START_Y, grid, fakeRobot);

                if (returnPath != null) {
                    Log.i("Algo", "RUNNING A* SEARCH!");
                    System.out.println(returnPath.toString());

                    if (realRun) {
                        fakeRobot.setPosX(robot.getPosX());
                        fakeRobot.setPosY(robot.getPosY());
                        fakeRobot.setHeading(robot.getHeading());
                        String compressedPath = AlgorithmRunner.compressPathWithC2calibration(returnPath, fakeRobot);
                        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, compressedPath);
                    } else {
                        for (String action : returnPath) {
//                            robot.sense(realRun);
                            if (action.equals("M")) {
                                robot.move();
                            } else if (action.equals("L")) {
                                robot.turn(LEFT);
                            } else if (action.equals("R")) {
                                robot.turn(RIGHT);
                            } else if (action.equals("U")) {
                                robot.turn(LEFT);
                                robot.turn(LEFT);
                            }
                            stepTaken();
                        }
                    }
                }else {
                    Log.i("Algo", "FASTEST PATH NOT FOUND!!");
                }
            }
        }
        Log.i("Algo", "EXPLORATION COMPLETED!");
        Log.i("Algo", "PERCENTAGE OF AREA EXPLORED: " + grid.checkExploredPercentage() + "%!");
        sendMessageToAndroidInternally( MessageMgr.generateMapDescriptorMsgAndroid(grid.generateDescriptorPartOne(), grid.generateDescriptorPartTwo(), "", robot.getCenterPosX(), robot.getCenterPosY(), robot.getHeading()));
    }

    /**
     * Checks if a turn is necessary and which direction to turn
     * @param robot
     * @param grid
     * @param realRun
     * @return whether a turn is performed
     */
    private boolean leftWallFollower(Robot robot, Grid grid, boolean realRun) {
        if (robot.isObstacleAhead()) {
            if (robot.isObstacleRight() && robot.isObstacleLeft()) {
                Log.i("Algo", "OBSTACLE DETECTED! (ALL 3 SIDES) U-TURNING");
                if (realRun) {
                    SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "L");
                    robot.turn(LEFT);
                    robot.sense(realRun);
                    SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "R");
                    robot.turn(RIGHT);
                    robot.sense(realRun);
                    SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "R");
                    robot.turn(RIGHT);
                    robot.sense(realRun);

                    if(robot.isObstacleAhead()) {
                        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "R");
                        robot.turn(RIGHT);

                    }
                    else {
                        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "*");
                    }

                }
//                    SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "U");
                //if (!realRun)
                stepTaken();
            } else if (robot.isObstacleLeft()) {
                Log.i("Algo", "OBSTACLE DETECTED! (FRONT + LEFT) TURNING RIGHT");
                if (realRun)
                    SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "R");
                robot.turn(RIGHT);
                stepTaken();
            } else {
                Log.i("Algo", "OBSTACLE DETECTED! (FRONT) TURNING LEFT");
                if (realRun)
                    SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "L");
                robot.turn(LEFT);
                stepTaken();
            }
            Log.i("Algo", "-----------------------------------------------");

            return true; // TURNED
        } else if (!robot.isObstacleLeft()) {
            phantomWall ++;
            if(phantomWall <=4) {
                Log.i("Algo", "NO OBSTACLES ON THE LEFT! TURNING LEFT==>>>>>>" + phantomWall);
                if (realRun)
                    SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "L");
                robot.turn(LEFT);
                stepTaken();
                Log.i("Algo", "-----------------------------------------------");

                return true; // TURNED
            }
            else {
                return false;
            }

        }
        return false; // DIDN'T TURN
    }

    private boolean checkUnexplored(Robot robot, Grid grid, int x, int y, boolean realRun){
        Robot fakeRobot = new Robot(grid, new ArrayList<>());
        fakeRobot.setPosX(robot.getPosX());
        fakeRobot.setPosY(robot.getPosY());
        fakeRobot.setHeading(robot.getHeading());
        List<String> returnPath = AlgorithmRunner.runAstar(robot.getPosX(), robot.getPosY(), x, y, grid, fakeRobot);
        if (returnPath != null) {
            Log.i("Algo", "Algorithm finished, executing actions");
            System.out.println(returnPath.toString());

            for (String action : returnPath) {
                robot.sense(realRun);
                if (realRun)
                    sendMessageToAndroidInternally( MessageMgr.generateMapDescriptorMsgAndroid(grid.generateDescriptorPartOne(), grid.generateDescriptorPartTwo(), "", robot.getCenterPosX(), robot.getCenterPosY(), robot.getHeading()));

                //if (!realRun)
                stepTaken();
                if (action.equals("M")) {
                    if (realRun)
                        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "M1");
                    robot.move();
                } else if (action.equals("L")) {
                    if (realRun)
                        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "L");
                    robot.turn(LEFT);
                } else if (action.equals("R")) {
                    if (realRun)
                        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "R");
                    robot.turn(RIGHT);
                } else if (action.equals("U")) {
                    if (realRun)
                        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "U");
                    robot.turn(LEFT);
                    robot.turn(LEFT);
                }
            }
            return true;
        } else {
            return false;
        }
    }

    private void stepTaken(){
        /*
            MAKE IT MOVE SLOWLY SO CAN SEE STEP BY STEP MOVEMENT
             */
        try {
            Thread.sleep(sleepDuration);
        } catch (Exception e) {
            Log.e("Algo.StackTrace", e.getMessage());
        }
    }


    private void senseAndUpdateAndroid(Robot robot, Grid grid, boolean realRun) {
        robot.sense(realRun);
        if (realRun) {
            sendMessageToAndroidInternally( MessageMgr.generateMapDescriptorMsgAndroid(grid.generateDescriptorPartOne(), grid.generateDescriptorPartTwo(), "", robot.getCenterPosX(), robot.getCenterPosY(), robot.getHeading()));
        }
    }

    /*To test overcoming phantom wall*/

    private void checkPhantomWall(Robot robot, boolean realRun, Grid grid) {
        forwardCounter++;
        if(forwardCounter < 2) {
            if(phantomWall >=4) {
                seekWall(robot, realRun, grid);
                phantomWall = 0;
                forwardCounter = 0;
            }
        } else {
            phantomWall = 0;
        }
    }

    private void seekWall(Robot robot, boolean realRun, Grid grid) {
        System.out.print("Seeking closest left wall...");
        faceNearestWall(robot, realRun, grid);

        while(!robot.isObstacleAhead()) {

            SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "M1");
            robot.move();
            sendMessageToAndroidInternally( MessageMgr.generateMapDescriptorMsgAndroid(grid.generateDescriptorPartOne(), grid.generateDescriptorPartTwo(), "", robot.getCenterPosX(), robot.getCenterPosY(), robot.getHeading()));
            robot.sense(realRun);
        }
        SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "R");
        robot.turn(RIGHT);
        sendMessageToAndroidInternally( MessageMgr.generateMapDescriptorMsgAndroid(grid.generateDescriptorPartOne(), grid.generateDescriptorPartTwo(), "", robot.getCenterPosX(), robot.getCenterPosY(), robot.getHeading()));
        robot.sense(realRun);


    }

    private void faceNearestWall(Robot robot, boolean realRun, Grid grid) {
        int minX = robot.getPosX();
        int maxX = 14 - robot.getPosX();

        int minY = robot.getPosY();
        int maxY = 19 - robot.getPosY();

        int neartestWallDistance = Math.min(Math.min(minX, maxX), Math.min(minX, maxY));

        if(minX == neartestWallDistance) {
            if(robot.getHeading() == NORTH) {
                SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "L");
                robot.turn(LEFT);
                sendMessageToAndroidInternally( MessageMgr.generateMapDescriptorMsgAndroid(grid.generateDescriptorPartOne(), grid.generateDescriptorPartTwo(), "", robot.getCenterPosX(), robot.getCenterPosY(), robot.getHeading()));
                robot.sense(realRun);
            }
            else if(robot.getHeading() == SOUTH) {
                SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "R");
                robot.turn(RIGHT);
                sendMessageToAndroidInternally( MessageMgr.generateMapDescriptorMsgAndroid(grid.generateDescriptorPartOne(), grid.generateDescriptorPartTwo(), "", robot.getCenterPosX(), robot.getCenterPosY(), robot.getHeading()));
                robot.sense(realRun);
            }
            else if(robot.getHeading() == EAST) {
                SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "L");
                robot.turn(LEFT);
                robot.sense(realRun);
                SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "L");
                robot.turn(LEFT);
                sendMessageToAndroidInternally( MessageMgr.generateMapDescriptorMsgAndroid(grid.generateDescriptorPartOne(), grid.generateDescriptorPartTwo(), "", robot.getCenterPosX(), robot.getCenterPosY(), robot.getHeading()));
                robot.sense(realRun);
            }
            else {
                //Dont need to turn
            }
        }
        else if(maxY == neartestWallDistance) {
            if(robot.getHeading() == NORTH) {
                //Dont need to turn
            }
            else if(robot.getHeading() == SOUTH) {
                SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "L");
                robot.turn(LEFT);
                robot.sense(realRun);
                SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "L");
                robot.turn(LEFT);
                sendMessageToAndroidInternally( MessageMgr.generateMapDescriptorMsgAndroid(grid.generateDescriptorPartOne(), grid.generateDescriptorPartTwo(), "", robot.getCenterPosX(), robot.getCenterPosY(), robot.getHeading()));
                robot.sense(realRun);
            }
            else if(robot.getHeading() == EAST) {
                SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "L");
                robot.turn(LEFT);
                sendMessageToAndroidInternally( MessageMgr.generateMapDescriptorMsgAndroid(grid.generateDescriptorPartOne(), grid.generateDescriptorPartTwo(), "", robot.getCenterPosX(), robot.getCenterPosY(), robot.getHeading()));
                robot.sense(realRun);
            }
            else {
                SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "R");
                robot.turn(RIGHT);
                sendMessageToAndroidInternally( MessageMgr.generateMapDescriptorMsgAndroid(grid.generateDescriptorPartOne(), grid.generateDescriptorPartTwo(), "", robot.getCenterPosX(), robot.getCenterPosY(), robot.getHeading()));
                robot.sense(realRun);
            }
        }
        else if(minY == neartestWallDistance) {
            if(robot.getHeading() == NORTH) {
                SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "L");
                robot.turn(LEFT);
                robot.sense(realRun);
                SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "L");
                robot.turn(LEFT);
                sendMessageToAndroidInternally( MessageMgr.generateMapDescriptorMsgAndroid(grid.generateDescriptorPartOne(), grid.generateDescriptorPartTwo(), "", robot.getCenterPosX(), robot.getCenterPosY(), robot.getHeading()));
                robot.sense(realRun);
            }
            else if(robot.getHeading() == SOUTH) {
                //Dont need to turn
            }
            else if(robot.getHeading() == EAST) {
                SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "R");
                robot.turn(RIGHT);
                sendMessageToAndroidInternally( MessageMgr.generateMapDescriptorMsgAndroid(grid.generateDescriptorPartOne(), grid.generateDescriptorPartTwo(), "", robot.getCenterPosX(), robot.getCenterPosY(), robot.getHeading()));
                robot.sense(realRun);
            }
            else {
                SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "L");
                robot.turn(LEFT);
                sendMessageToAndroidInternally( MessageMgr.generateMapDescriptorMsgAndroid(grid.generateDescriptorPartOne(), grid.generateDescriptorPartTwo(), "", robot.getCenterPosX(), robot.getCenterPosY(), robot.getHeading()));
                robot.sense(realRun);
            }
        }
        else { //maxX
            if(robot.getHeading() == NORTH) {
                SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "R");
                robot.turn(RIGHT);
                sendMessageToAndroidInternally( MessageMgr.generateMapDescriptorMsgAndroid(grid.generateDescriptorPartOne(), grid.generateDescriptorPartTwo(), "", robot.getCenterPosX(), robot.getCenterPosY(), robot.getHeading()));
                robot.sense(realRun);
            }
            else if(robot.getHeading() == SOUTH) {
                SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "L");
                robot.turn(LEFT);
                sendMessageToAndroidInternally( MessageMgr.generateMapDescriptorMsgAndroid(grid.generateDescriptorPartOne(), grid.generateDescriptorPartTwo(), "", robot.getCenterPosX(), robot.getCenterPosY(), robot.getHeading()));
                robot.sense(realRun);
            }
            else if(robot.getHeading() == EAST) {
                //DOnt need turn
            }
            else {
                SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "L");
                robot.turn(LEFT);
                robot.sense(realRun);
                SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "L");
                robot.turn(LEFT);
                sendMessageToAndroidInternally( MessageMgr.generateMapDescriptorMsgAndroid(grid.generateDescriptorPartOne(), grid.generateDescriptorPartTwo(), "", robot.getCenterPosX(), robot.getCenterPosY(), robot.getHeading()));
                robot.sense(realRun);
            }
        }
    }

    // private ArrayList<String> calculateShortestTurn(Robot robot, int target) {
    //     ArrayList<String> actions = new ArrayList<>();
    //     int currentHeading = robot.getHeading();
    //
    //     if(currentHeading == target) {
    //         return actions;
    //     }
    //     else {
    //         if(currentHeading == )
    //     }
    //
    //     return actions;
    // }
    
    private void sendMessageToAndroidInternally(String writeMessage) {
        BluetoothMessage temp = new BluetoothMessage();
        temp.setDeviceName(Config.my_bluetooth_device_name);
        temp.setDeviceAddress(Config.my_bluetooth_device_address);
        temp.setMessage(writeMessage);
        temp.setDatetime(Calendar.getInstance().getTime());

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        BluetoothMessage bluetoothMessage = realm.createObject(BluetoothMessage.class
                , UUID.randomUUID().toString());
        bluetoothMessage.setDeviceName(Config.my_bluetooth_device_name);
        bluetoothMessage.setDeviceAddress(Config.my_bluetooth_device_address);
        bluetoothMessage.setMessage(writeMessage);
        bluetoothMessage.setDatetime(Calendar.getInstance().getTime());
        realm.commitTransaction();
        realm.close();

        Config.sent_message = writeMessage;

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MainActivity.bluetoothSubject.postMessage(temp);
            }
        });
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}