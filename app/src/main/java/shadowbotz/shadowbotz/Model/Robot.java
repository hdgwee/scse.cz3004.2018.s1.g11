package shadowbotz.shadowbotz.Model;

public class Robot {
    private int head;
    private int body;
    private int waypointPosition;

    private boolean visitedWaypoint = false;

    private boolean bodyPosition = false;
    private boolean headPosition = false;
    private boolean waypoint = false;

    public Robot(int head, int body) {
        this.head = head;
        this.body = body;
    }

    public Robot(){ }

    public int getHead() {
        return head;
    }

    public void setHead(int head) {
        this.head = head;
    }

    public int getBody() {
        return body;
    }

    public void setBody(int body) {
        this.body = body;
    }

    public int getWaypointPosition() {
        return waypointPosition;
    }

    public void setWaypointPosition(int waypointPosition) {
        this.waypointPosition = waypointPosition;
    }

    public boolean isBodyPosition() {
        return bodyPosition;
    }

    public void setBodyPosition(boolean bodyPosition) {
        this.bodyPosition = bodyPosition;
    }

    public boolean isHeadPosition() {
        return headPosition;
    }

    public void setHeadPosition(boolean headPosition) {
        this.headPosition = headPosition;
    }

    public boolean isWaypoint() {
        return waypoint;
    }

    public void setWaypoint(boolean waypoint) {
        this.waypoint = waypoint;
    }

    public boolean isVisitedWaypoint() {
        return visitedWaypoint;
    }

    public void setVisitedWaypoint(boolean visitedWaypoint) {
        this.visitedWaypoint = visitedWaypoint;
    }
}
