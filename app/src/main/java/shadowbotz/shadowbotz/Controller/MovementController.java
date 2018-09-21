package shadowbotz.shadowbotz.Controller;

import android.app.Activity;
import android.widget.TextView;
import android.widget.Toast;

import shadowbotz.shadowbotz.Model.Robot;
import shadowbotz.shadowbotz.View.MainActivity;

public class MovementController {
    ImageAdapter imageAdapter;
    Activity callingActivity;

    public MovementController(ImageAdapter imageAdapter, Activity callingActivity) {
        this.imageAdapter = imageAdapter;
        this.callingActivity = callingActivity;
    }

    private void checkIfWaypointIsExplored(Robot robot){
        if (!robot.isVisitedWaypoint()) {
            if(     robot.getWaypointPosition() == robot.getBody()-14 ||
                    robot.getWaypointPosition() == robot.getBody()-16 ||
                    robot.getWaypointPosition() == robot.getBody()+14 ||
                    robot.getWaypointPosition() == robot.getBody()+16 ||
                    robot.getWaypointPosition() == robot.getBody()+1 ||
                    robot.getWaypointPosition() == robot.getBody()-1 ||
                    robot.getWaypointPosition() == robot.getBody()+15 ||
                    robot.getWaypointPosition() == robot.getBody()-15 ||
                    robot.getWaypointPosition() == robot.getBody()){

                robot.setVisitedWaypoint(true);
                imageAdapter.mThumbIds[robot.getWaypointPosition()] = 11;
            }
        }
        else{
            imageAdapter.mThumbIds[robot.getWaypointPosition()] = 11;
        }

    }

    public void setBody(Robot robot){
        imageAdapter.mThumbIds[robot.getBody()] = 8;
        //4 corners
        imageAdapter.mThumbIds[robot.getBody()-14] = 8; //set the whole body
        imageAdapter.mThumbIds[robot.getBody()-16] = 8;
        imageAdapter.mThumbIds[robot.getBody()+14] = 8;
        imageAdapter.mThumbIds[robot.getBody()+16] = 8;

        //the rest
        imageAdapter.mThumbIds[robot.getBody()+1] = 8;
        imageAdapter.mThumbIds[robot.getBody()-1] = 8;
        imageAdapter.mThumbIds[robot.getBody()+15] = 8;
        imageAdapter.mThumbIds[robot.getBody()-15] = 8;
        imageAdapter.notifyDataSetChanged();
    }

    public void setHead(Robot robot){
        imageAdapter.mThumbIds[robot.getHead()] = 9;
        imageAdapter.notifyDataSetChanged();
        robot.setWaypointPosition(-1); //to indicate no waypoint
    }

    public void clearRobot(Robot robot){
        imageAdapter.mThumbIds[robot.getBody()] = 0;
        //4 corners
        imageAdapter.mThumbIds[robot.getBody()-14] = 0; //set the whole body
        imageAdapter.mThumbIds[robot.getBody()-16] = 0;
        imageAdapter.mThumbIds[robot.getBody()+14] = 0;
        imageAdapter.mThumbIds[robot.getBody()+16] = 0;

        //the rest
        imageAdapter.mThumbIds[robot.getBody()+1] = 0;
        imageAdapter.mThumbIds[robot.getBody()-1] = 0;
        imageAdapter.mThumbIds[robot.getBody()+15] = 0;
        imageAdapter.mThumbIds[robot.getBody()-15] = 0;

        robot.setBody(-1);
        robot.setHead(-1);

        imageAdapter.notifyDataSetChanged();
    }

    public void clearWayPoint(Robot robot){
        imageAdapter.mThumbIds[robot.getWaypointPosition()] = 0;
        robot.setWaypointPosition(-1);
        imageAdapter.notifyDataSetChanged();
    }

    public void setWayPoint(Robot robot, TextView statusTextView) {
        // if (    robot.getWaypointPosition() == robot.getBody()||
        //         robot.getWaypointPosition() == robot.getBody()-14 ||
        //         robot.getWaypointPosition() == robot.getBody()-16 ||
        //         robot.getWaypointPosition() == robot.getBody()+14 ||
        //         robot.getWaypointPosition() == robot.getBody()+16 ||
        //         robot.getWaypointPosition() == robot.getBody()+1 ||
        //         robot.getWaypointPosition() == robot.getBody()-1 ||
        //         robot.getWaypointPosition() == robot.getBody()+15 ||
        //         robot.getWaypointPosition() == robot.getBody()-15){
        //
        //     Toast.makeText(callingActivity, "Unable to set waypoint here", Toast.LENGTH_SHORT).show();
        // }
        // else{
            imageAdapter.mThumbIds[robot.getWaypointPosition()] = 10; //unexplored waypoint
            statusTextView.setText("Way point: "+robot.getWaypointPosition()%15+", "+Math.abs(19-(Math.abs(robot.getWaypointPosition()/15))));

            imageAdapter.notifyDataSetChanged();
        // }
    }

    private int turnRightwhenFaceRight(Robot robot){
        imageAdapter.mThumbIds[robot.getHead()] = 8;
        robot.setHead(robot.getBody()+15);
        imageAdapter.mThumbIds[robot.getHead()] = 9;
        imageAdapter.notifyDataSetChanged();
        return robot.getHead();
    }
    private int turnRightwhenFaceLeft(Robot robot){
        imageAdapter.mThumbIds[robot.getHead()] = 8;
        robot.setHead(robot.getBody()-15);
        imageAdapter.mThumbIds[robot.getHead()] = 9;
        imageAdapter.notifyDataSetChanged();
        return robot.getHead();
    }
    private int turnRightwhenFaceUp(Robot robot){
        imageAdapter.mThumbIds[robot.getHead()] = 8;
        robot.setHead(robot.getBody()+1);
        imageAdapter.mThumbIds[robot.getHead()] = 9;
        imageAdapter.notifyDataSetChanged();
        return robot.getHead();
    }
    private int turnRightwhenFaceDown(Robot robot){
        imageAdapter.mThumbIds[robot.getHead()] = 8;
        robot.setHead(robot.getBody()-1);
        imageAdapter.mThumbIds[robot.getHead()] = 9;
        imageAdapter.notifyDataSetChanged();
        return robot.getHead();
    }


    private int turnLeftwhenFaceRight(Robot robot){
        imageAdapter.mThumbIds[robot.getHead()] = 8;
        robot.setHead(robot.getBody()-15);
        imageAdapter.mThumbIds[robot.getHead()] = 9;
        imageAdapter.notifyDataSetChanged();
        return robot.getHead();
    }
    private int turnLeftwhenFaceLeft(Robot robot){
        imageAdapter.mThumbIds[robot.getHead()] = 8;
        robot.setHead(robot.getBody()+15);
        imageAdapter.mThumbIds[robot.getHead()] = 9;
        imageAdapter.notifyDataSetChanged();
        return robot.getHead();
    }
    private int turnLeftwhenFaceUp(Robot robot){
        imageAdapter.mThumbIds[robot.getHead()] = 8;
        robot.setHead(robot.getBody()-1);
        imageAdapter.mThumbIds[robot.getHead()] = 9;
        imageAdapter.notifyDataSetChanged();
        return robot.getHead();
    }
    private int turnLeftwhenFaceDown(Robot robot){
        imageAdapter.mThumbIds[robot.getHead()] = 8;
        robot.setHead(robot.getBody()+1);
        imageAdapter.mThumbIds[robot.getHead()] = 9;
        imageAdapter.notifyDataSetChanged();
        return robot.getHead();
    }

    private void moveForwardWhenFaceRight(Robot robot){
        imageAdapter.mThumbIds[robot.getHead()+1] = 9;

        //4 corners
        imageAdapter.mThumbIds[(robot.getBody()-14)+1] = 8; //set the whole body
        imageAdapter.mThumbIds[(robot.getBody()-16)+1] = 8;
        imageAdapter.mThumbIds[(robot.getBody()+14)+1] = 8;
        imageAdapter.mThumbIds[(robot.getBody()+16)+1] = 8;

        //the rest
        imageAdapter.mThumbIds[(robot.getBody())+1] = 8;
        imageAdapter.mThumbIds[(robot.getBody()-1)+1] = 8;
        imageAdapter.mThumbIds[(robot.getBody()+15)+1] = 8;
        imageAdapter.mThumbIds[(robot.getBody()-15)+1] = 8;

        //convert back of robot to explored
        imageAdapter.mThumbIds[(robot.getBody()-1)] = 1;
        imageAdapter.mThumbIds[(robot.getBody()-16)] = 1;
        imageAdapter.mThumbIds[(robot.getBody()+14)] = 1;
    }

    private void moveForwardWhenFaceLeft(Robot robot){
        imageAdapter.mThumbIds[robot.getHead()-1] = 9;

        //4 corners
        imageAdapter.mThumbIds[(robot.getBody()-14)-1] = 8; //set the whole body
        imageAdapter.mThumbIds[(robot.getBody()-16)-1] = 8;
        imageAdapter.mThumbIds[(robot.getBody()+14)-1] = 8;
        imageAdapter.mThumbIds[(robot.getBody()+16)-1] = 8;

        //the rest
        imageAdapter.mThumbIds[(robot.getBody())-1] = 8;
        imageAdapter.mThumbIds[(robot.getBody()+1)-1] = 8;
        imageAdapter.mThumbIds[(robot.getBody()+15)-1] = 8;
        imageAdapter.mThumbIds[(robot.getBody()-15)-1] = 8;

        //convert back of robot to explored
        imageAdapter.mThumbIds[(robot.getBody()+1)] = 1;
        imageAdapter.mThumbIds[(robot.getBody()+16)] = 1;
        imageAdapter.mThumbIds[(robot.getBody()-14)] = 1;
    }

    private void moveForwardWhenFaceUp(Robot robot){
        imageAdapter.mThumbIds[robot.getHead()-15] = 9;

        //4 corners
        imageAdapter.mThumbIds[(robot.getBody()-14)-15] = 8; //set the whole body
        imageAdapter.mThumbIds[(robot.getBody()-16)-15] = 8;
        imageAdapter.mThumbIds[(robot.getBody()+14)-15] = 8;
        imageAdapter.mThumbIds[(robot.getBody()+16)-15] = 8;

        //the rest
        imageAdapter.mThumbIds[(robot.getBody())-15] = 8;
        imageAdapter.mThumbIds[(robot.getBody()-1)-15] = 8;
        imageAdapter.mThumbIds[(robot.getBody()+15)-15] = 8;
        imageAdapter.mThumbIds[(robot.getBody()+1)-15] = 8;

        //convert back of robot to explored
        imageAdapter.mThumbIds[(robot.getBody()+14)] = 1;
        imageAdapter.mThumbIds[(robot.getBody()+15)] = 1;
        imageAdapter.mThumbIds[(robot.getBody()+16)] = 1;
    }

    private void moveForwardWhenFaceDown(Robot robot){
        imageAdapter.mThumbIds[robot.getHead()+15] = 9;

        //4 corners
        imageAdapter.mThumbIds[(robot.getBody()-14)+15] = 8; //set the whole body
        imageAdapter.mThumbIds[(robot.getBody()-16)+15] = 8;
        imageAdapter.mThumbIds[(robot.getBody()+14)+15] = 8;
        imageAdapter.mThumbIds[(robot.getBody()+16)+15] = 8;

        //the rest
        imageAdapter.mThumbIds[(robot.getBody())+15] = 8;
        imageAdapter.mThumbIds[(robot.getBody()-1)+15] = 8;
        imageAdapter.mThumbIds[(robot.getBody()+1)+15] = 8;
        imageAdapter.mThumbIds[(robot.getBody()-15)+15] = 8;

        //convert back of robot to explored
        imageAdapter.mThumbIds[(robot.getBody()-14)] = 1;
        imageAdapter.mThumbIds[(robot.getBody()-15)] = 1;
        imageAdapter.mThumbIds[(robot.getBody()-16)] = 1;
    }


    public void turnLeft(Robot robot){
        if(robot.getHead() == robot.getBody()+1){ //face right
            robot.setHead(turnLeftwhenFaceRight(robot));
        }
        else if(robot.getHead() == robot.getBody()-1) { //face left
            robot.setHead(turnLeftwhenFaceLeft(robot));
        }
        else if(robot.getHead() == robot.getBody()-15) { //face up
            robot.setHead(turnLeftwhenFaceUp(robot));
        }
        else{ //face down
            robot.setHead(turnLeftwhenFaceDown(robot));
        }
        checkIfWaypointIsExplored(robot);
        MainActivity.sendMessage("tl");
        imageAdapter.notifyDataSetChanged();
    }


    public void turnRight(Robot robot){
        if(robot.getHead() == robot.getBody()+1){ //face right
            robot.setHead(turnRightwhenFaceRight(robot));
        }
        else if(robot.getHead() == robot.getBody()-1) { //face left
            robot.setHead(turnRightwhenFaceLeft(robot));

        }
        else if(robot.getHead() == robot.getBody()-15) { //face up

            robot.setHead(turnRightwhenFaceUp(robot));

        }
        else{ //face down
            robot.setHead(turnRightwhenFaceDown(robot));
        }
        checkIfWaypointIsExplored(robot);
        MainActivity.sendMessage("tr");
        imageAdapter.notifyDataSetChanged();
    }

    public void moveForward(Robot robot){
        if((robot.getHead()%15>=1 && robot.getHead()%15<=13) && (Math.abs(robot.getHead()/15) >=1 && Math.abs(robot.getHead()/15) <=18)){
            if(robot.getHead() == robot.getBody()+1){ //face right
                moveForwardWhenFaceRight(robot);
                //increase current of head and body by 1 grid forward
                robot.setHead(robot.getHead()+1);
                robot.setBody(robot.getBody()+1);
            }
            else if(robot.getHead() == robot.getBody()-1){ //face left
                moveForwardWhenFaceLeft(robot);
                //increase current of head and body by 1 grid forward
                robot.setHead(robot.getHead()-1);
                robot.setBody(robot.getBody()-1);
            }
            else if(robot.getHead() == robot.getBody()-15){ //face up
                moveForwardWhenFaceUp(robot);
                //increase current of head and body by 1 grid forward
                robot.setHead(robot.getHead()-15);
                robot.setBody(robot.getBody()-15);
            }
            else{ //face down
                moveForwardWhenFaceDown(robot);
                //increase current of head and body by 1 grid forward
                robot.setHead(robot.getHead()+15);
                robot.setBody(robot.getBody()+15);
            }
            checkIfWaypointIsExplored(robot);
            MainActivity.sendMessage("f");
            imageAdapter.notifyDataSetChanged();
        }
        else{
            Toast.makeText(callingActivity, "Out of bound!", Toast.LENGTH_SHORT).show();
        }
    }

}
