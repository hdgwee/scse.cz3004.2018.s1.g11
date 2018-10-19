package shadowbotz.shadowbotz.Algo.model.algorithm;

import android.app.Activity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import shadowbotz.shadowbotz.Algo.model.entity.Grid;
import shadowbotz.shadowbotz.Algo.model.entity.Robot;
import shadowbotz.shadowbotz.Algo.model.util.SocketMgr;

import static shadowbotz.shadowbotz.Algo.constant.CommConstants.TARGET_ARDUINO;
import static shadowbotz.shadowbotz.Algo.constant.RobotConstants.LEFT;
import static shadowbotz.shadowbotz.Algo.constant.RobotConstants.RIGHT;
import static shadowbotz.shadowbotz.Algo.constant.RobotConstants.WEST;

/**
 * Fastest path algorithm using A* search + customized score functions
 */
public class FastestPathAlgorithmRunner implements AlgorithmRunner {

    private int sleepDuration = 0;
    private int mWayPointX = -1;
    private int mWayPointY = -1;

    private static final int START_X = 0;
    private static final int START_Y = 17;
    private static final int GOAL_X = 12;
    private static final int GOAL_Y = 0;

    public FastestPathAlgorithmRunner(int speed) {
        // sleepDuration = 0 / speed;
    }

    public FastestPathAlgorithmRunner(int speed, int x, int y) {
        // sleepDuration = 1000 / speed;
        sleepDuration = 0;
        mWayPointX = x;
        mWayPointY = y;
    }

    @Override
    public void setActivity(Activity activity) {

    }

    @Override
    public void run(Grid grid, Robot robot, boolean realRun) {
        Log.i("Algo", "Start of FastestPathAlgorithmRunner");
        robot.reset();

        int wayPointX = mWayPointX, wayPointY = mWayPointY;

        if (!realRun) {
            // ignore waypoint for simulation
            wayPointX = START_X;
            wayPointY = START_Y;
        }

        // run from start to waypoint and from waypoint to goal
        Log.i("Algo", "Fastest path algorithm started with waypoint " + wayPointX + "," + wayPointY);
        Robot fakeRobot = new Robot(new Grid(), new ArrayList<>());
        List<String> path1 = AlgorithmRunner.runAstar(START_X, START_Y, wayPointX, wayPointY, grid, fakeRobot);
        List<String> path2 = AlgorithmRunner.runAstar(wayPointX, wayPointY, GOAL_X, GOAL_Y, grid, fakeRobot);

        if (path1 != null && path2 != null) {
            Log.i("Algo", "Algorithm finished, executing actions");
            path1.addAll(path2);
            System.out.println(path1.toString());
            if (realRun) {
                // SEND ENTIRE PATH AT ONCE
                Robot fakeRobotWithRealGrid = new Robot(grid, new ArrayList<>());
                String compressedPath = AlgorithmRunner.compressPathWithC2calibration(path1, fakeRobotWithRealGrid);
                String fastestPath = "F," + compressedPath + "F";

                // if(robot.getHeading() == NORTH) {
                //     fastestPath += ",R,C";
                // }
                // else if(robot.getHeading() == SOUTH) {
                //     fastestPath += ",L,C";
                // }
                // else if(robot.getHeading() == EAST) {
                //     fastestPath += ",L,L,C";
                // }

                SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, fastestPath);
                robot.sense(realRun);

                // Robot must face west to perform final calibration to ensure it is inside finishing point
                while (robot.getHeading() != WEST) {
                    Log.i("Algo", "Turning");
                    robot.turn(RIGHT);
                    SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "R");
                    robot.sense(realRun);
                }
                SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "C");
                robot.sense(realRun);


                System.out.println(fastestPath);
                // SIMULATE AT THE SAME TIME
                for (String action : path1) {
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
                    takeStep();
                }
            } else {
                for (String action : path1) {
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
                    takeStep();
                }
            }
        } else {
            Log.i("Algo", "Fastest path not found!");
        }

        Log.i("Algo", "End of FastestPathAlgorithmRunner");
    }

    /**
     * Pause the simulation for sleepDuration
     */
    private void takeStep() {
        try {
            Thread.sleep(sleepDuration);
        } catch (Exception e) {
            Log.e("Algo.StackTrace", e.getMessage());
        }
    }
}
