package shadowbotz.shadowbotz.Algo.model.util;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static shadowbotz.shadowbotz.Algo.constant.MapConstants.MAP_ROWS;
import static shadowbotz.shadowbotz.Algo.constant.RobotConstants.EAST;
import static shadowbotz.shadowbotz.Algo.constant.RobotConstants.NORTH;
import static shadowbotz.shadowbotz.Algo.constant.RobotConstants.SOUTH;
import static shadowbotz.shadowbotz.Algo.constant.RobotConstants.WEST;

/**
 * Message generator
 */
public class MessageMgr {

    public static String generateFinalDescriptor(String part1, String part2) {
        return "{finaldescriptor:\"" + part1 + "," + part2 + "\"}";
    }

    /**
     * Generate map string for Android communication, note that on Android the coordinate of
     * the robot is the upper right corner.
     * @param descriptor Map descriptor in Android format
     * @param x Robot's x coordinates
     * @param y Robot's y coordinates
     * @param heading Robot's heading
     * @return Message string for sending to Android
     */
    public static String generateMapDescriptorMsg(String descriptor, int x, int y, int heading) {
        StringBuilder builder = new StringBuilder();
        builder.append("{\"robot\":\"");
        builder.append(descriptor);
        builder.append(",");
        builder.append(MAP_ROWS - y);
        builder.append(",");
        builder.append(x + 1);
        builder.append(",");
        if (heading == NORTH) {
            builder.append(0);
        } else if (heading == EAST) {
            builder.append(90);
        } else if (heading == SOUTH) {
            builder.append(180);
        } else if (heading == WEST) {
            builder.append(270);
        }
        builder.append("\"}");
        return builder.toString();
    }
    
    public static String generateMapDescriptorMsgAndroid(String descriptor1, String descriptor2, String arrows, int x, int y, int heading) { //TODO: add arrows function
        StringBuilder builder = new StringBuilder();
        builder.append("{\"robot\":{");
        builder.append("\"map\":\"");
        builder.append(descriptor1);
        builder.append("\",");
        builder.append("\"obstacle\":\"");
        builder.append(descriptor2);
        builder.append("\",");
        builder.append("\"arrows\":\"");
        builder.append(arrows);
        builder.append("\",");
        builder.append("\"robotCenter\":\"");
        String robotCenter = "("+(x) + ", " + ((MAP_ROWS - y)-1) + ")";
        builder.append(robotCenter);
        builder.append("\",");
        builder.append("\"robotHead\":\"");
        String robotHead ="";
        if (heading == NORTH) {
        	robotHead = "("+(x) + ", " + (MAP_ROWS - y) + ")\"";
        } else if (heading == EAST) {
        	robotHead = "("+(x+1) + ", " + ((MAP_ROWS - y)-1) + ")\"";
        } else if (heading == SOUTH) {
        	robotHead = "("+(x) + ", " + ((MAP_ROWS - y)-2) + ")\"";
        } else if (heading == WEST) {
        	robotHead = "("+(x-1) + ", " + ((MAP_ROWS - y) -1) + ")\"";
        }
        builder.append(robotHead);
        builder.append("}}");
        return builder.toString();
    }

    /**
     * Parse waypoint message from Android, the Y coordinate received
     * starts from the bottom, so it's reversed.
     * @param msg
     * @return
     */
    public static List<Integer> parseMessage(String msg) {
        String[] splitString = msg.split(",", 2);
        List<Integer> waypoint = new ArrayList<>();

        Integer wayPointX, wayPointY;
        try {
            wayPointX = Integer.parseInt(splitString[0]);
            wayPointY = MAP_ROWS - Integer.parseInt(splitString[1]) - 1;
            waypoint.add(wayPointX);
            waypoint.add(wayPointY);
            return waypoint;
        } catch (Exception e) {
            Log.e("Algo.StackTrace", e.getMessage());
            return null;
        }
    }
}
