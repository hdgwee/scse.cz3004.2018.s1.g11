package shadowbotz.shadowbotz.View;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.andretietz.android.controller.DirectionView;
import com.andretietz.android.controller.InputView;

import org.json.JSONException;
import org.json.JSONObject;

import shadowbotz.shadowbotz.BluetoothObserverSubject.Observer;
import shadowbotz.shadowbotz.BluetoothObserverSubject.Subject;
import shadowbotz.shadowbotz.Config;
import shadowbotz.shadowbotz.Controller.DescriptorStringController;
import shadowbotz.shadowbotz.Controller.ImageAdapter;
import shadowbotz.shadowbotz.Controller.MovementController;
import shadowbotz.shadowbotz.Model.Robot;
import shadowbotz.shadowbotz.Model.BluetoothMessage;
import shadowbotz.shadowbotz.R;

public class RobotFragment extends Fragment implements Observer {


    private Robot robot = new Robot();

    // Observer pattern
    private Subject topic;
    private MovementController movementController;
    private TextView statusTextView;
    private ImageAdapter imageAdapter;

    private Button setWaypoint;
    private Button setRobot;
    private Button sendCoords;

    private boolean autoUpdate=true;
    private boolean canSetWayPoint = false;
    private boolean canSetRobot = false;
    private boolean canSendCoords = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_robot, container, false);

        // Observer pattern
        MainActivity.bluetoothSubject.register(this);
        this.setSubject(MainActivity.bluetoothSubject);

        // Get Fragment belonged Activity
        final FragmentActivity fragmentBelongActivity = getActivity();
        final FloatingActionButton fab =  view.findViewById(R.id.fab);
        final SharedPreferences sharedPreferences = fragmentBelongActivity.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        final DirectionView directionView = (DirectionView) view.findViewById(R.id.viewDirection);

        final Switch switchMovement = view.findViewById(R.id.switchMovement);
        final Switch switchUpdate = view.findViewById(R.id.switchUpdate);
        final Button buttonStart = view.findViewById(R.id.buttonStart);
        final Button buttonStop = view.findViewById(R.id.buttonStop);

        final TextView textviewRobotBody = view.findViewById(R.id.textview_robot_body);
        final TextView textviewRobotHead = view.findViewById(R.id.textview_robot_head);
        final TextView textviewWaypoint = view.findViewById(R.id.textview_waypoint);

        final RelativeLayout leftColumn = view.findViewById(R.id.leftColumn);
        final LinearLayout rightColumn = view.findViewById(R.id.rightColumn);

        setWaypoint = view.findViewById(R.id.button_way_point);
        setRobot = view.findViewById(R.id.button_robot_position);
        sendCoords = view.findViewById(R.id.button_send_coords);
        sendCoords.setEnabled(false);

        statusTextView = view.findViewById(R.id.statusTextView);

        Button buttonL1 = view.findViewById(R.id.buttonL1);
        Button buttonL2 = view.findViewById(R.id.buttonL2);

        imageAdapter = new ImageAdapter(fragmentBelongActivity);

        movementController = new MovementController(imageAdapter, getActivity());

        //for onCreateOptionsMenu
        setHasOptionsMenu(true);

        GridView gridview = (GridView) view.findViewById(R.id.gridview);
        gridview.setAdapter(imageAdapter);

        DescriptorStringController descriptorStringController = new DescriptorStringController(imageAdapter);

        //testing descriptorstring 1
//        descriptorStringController.descriptorString1("7000000000000000000000000000000000000000000000000000000000000000000000000000");
        descriptorStringController.descriptorString1("FFC07F80FF01FE03FFFFFFF3FFE7FFCFFF9C7F38FE71FCE3F87FF0FFE1FFC3FF87FF0E0E1C1F");

        //testing descriptorString 2
        descriptorStringController.descriptorString2("00000100001C80000000001C0000080000060001C00000080000");
        descriptorStringController.descriptorString2("00000100001C80000000001C0000080000060001C00000080000");

        //testing setting of arrows
        descriptorStringController.splitImageString("(6, 5, D),(3, 9, R),(1, 15, D),(7, 19, L),(14, 14, U)");

        //End of testing

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() { //set robot body and head
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                if(canSetRobot){
                    if((position%15>=1 && position%15<=13) && Math.abs(19-(Math.abs(position/15))) >=1 && Math.abs(19-(Math.abs(position/15))) <=18){
                        if(!robot.isBodyPosition()){
                            robot.setBody(position);
                            movementController.setBody(robot); //set the starting position of the robot
                            robot.setBodyPosition(true);
                            // MainActivity.sendMessage("Way point: " +robot.getBody()%15 +", "+ Math.abs(19-(Math.abs(position/15))));

                            String output = robot.getBody()%15 +", "+ Math.abs(19-(Math.abs(position/15)));
                            textviewRobotBody.setText(output);
                        }
                        else if(!robot.isHeadPosition()){
                            robot.setHead(position);
                            if(robot.getHead() == robot.getBody()+1 || robot.getHead() == robot.getBody()-1 || robot.getHead() == robot.getBody()+15 || robot.getHead() == robot.getBody()-15 ){  //make sure head is at the correct position
                                movementController.setHead(robot);
                                robot.setHeadPosition(true);
                                // MainActivity.sendMessage("Way point: " +robot.getHead()%15 +", "+ Math.abs(19-(Math.abs(position/15))));

                                canSetRobot = false;
                                setWaypoint.setEnabled(true);
                                sendCoords.setEnabled(true);

                                leftColumn.setVisibility(View.VISIBLE);
                                rightColumn.setVisibility(View.VISIBLE);

                                String output = robot.getHead()%15 +", "+ Math.abs(19-(Math.abs(position/15)));
                                textviewRobotHead.setText(output);
                            }
                            else{
                                Toast.makeText(fragmentBelongActivity, "Invalid head position", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Toast.makeText(fragmentBelongActivity, "Robot Position Set!", Toast.LENGTH_SHORT).show();
                        }
                        imageAdapter.notifyDataSetChanged();

                    }
                    else{
                        Toast.makeText(fragmentBelongActivity, "Out of Range!", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(fragmentBelongActivity, "Please press set robot button", Toast.LENGTH_SHORT).show();
                }

            }
        });
        gridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { //set way point
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (canSetWayPoint){
                    robot.setWaypointPosition(i);
                    movementController.setWayPoint(robot, statusTextView);
                    robot.setWaypoint(true);
                    // MainActivity.sendMessage("Way point: "+ robot.getWaypointPosition()%15  +", "+Math.abs(19-(Math.abs(robot.getWaypointPosition()/15))));
                    canSetWayPoint = false;
                    setRobot.setEnabled(true);

                    leftColumn.setVisibility(View.VISIBLE);
                    rightColumn.setVisibility(View.VISIBLE);

                    String output = robot.getWaypointPosition()%15  +", "+Math.abs(19-(Math.abs(robot.getWaypointPosition()/15)));
                    textviewWaypoint.setText(output);
                }
                else{
                    Toast.makeText(fragmentBelongActivity, "Please press set waypoint button", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        setWaypoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(robot.isWaypoint()){
                    movementController.clearWayPoint(robot);
                }
                canSetWayPoint = true;
                robot.setWaypoint(false);
                setRobot.setEnabled(false);
                leftColumn.setVisibility(View.INVISIBLE);
                rightColumn.setVisibility(View.INVISIBLE);
            }
        });

        setRobot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (robot.isBodyPosition()){
                    movementController.clearRobot(robot);
                }
                canSetRobot = true;
                robot.setHeadPosition(false);
                robot.setBodyPosition(false);
                setWaypoint.setEnabled(false);
                leftColumn.setVisibility(View.INVISIBLE);
                rightColumn.setVisibility(View.INVISIBLE);
            }
        });

        sendCoords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject data = new JSONObject();

                try {
                    data.put("robot_head_x", robot.getHead()%15);
                    data.put("robot_head_y", Math.abs(19-(Math.abs(robot.getHead()/15))));
                    data.put("robot_body_x", robot.getBody()%15);
                    data.put("robot_body_y", Math.abs(19-(Math.abs(robot.getBody()/15))));
                    data.put("waypoint_x", robot.getWaypointPosition()%15);
                    data.put("waypoint_y", Math.abs(19-(Math.abs(robot.getWaypointPosition()/15))));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JSONObject jsonObject = new JSONObject();

                try {
                    jsonObject.put("coordinate", data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                MainActivity.sendMessage(jsonObject.toString());
            }
        });

        directionView.setOnButtonListener(new InputView.InputEventListener() {
            @Override public void onInputEvent(View view, int buttons) {

                if(robot.isWaypoint() && robot.isBodyPosition() && robot.isHeadPosition()){
                    switch (buttons&0xff) {
                        case DirectionView.DIRECTION_DOWN:
                            Toast.makeText(fragmentBelongActivity, "Not in use!", Toast.LENGTH_SHORT).show();
                            break;
                        case DirectionView.DIRECTION_RIGHT:
                            movementController.turnRight(robot);
                            break;

                        case DirectionView.DIRECTION_LEFT:
                            movementController.turnLeft(robot);
                            break;
                        case DirectionView.DIRECTION_UP: //move forward
                            movementController.moveForward(robot);
                            break;
                    }
                }
                else{
                    Toast.makeText(fragmentBelongActivity, "WayPoint not set!", Toast.LENGTH_SHORT).show();
                }

            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(fragmentBelongActivity, "Manual Update", Toast.LENGTH_SHORT).show();
                //TODO: implement manual updating
            }
        });

        switchUpdate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    fab.setVisibility(View.GONE);
                    fab.setActivated(false);
                    Toast.makeText(fragmentBelongActivity, "Auto updating", Toast.LENGTH_SHORT).show();
                    //TODO: implement auto updating
                }
                else{
                    fab.setVisibility(View.VISIBLE);
                    fab.setActivated(true);
                }
            }
        });

        //set auto buttons to invisible
        buttonStart.setVisibility(View.GONE);
        buttonStart.setActivated(false);
        buttonStop.setVisibility(View.GONE);
        buttonStop.setActivated(false);
        buttonStop.setEnabled(false);

        switchMovement.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){ // if auto
                    directionView.setVisibility(View.GONE);
                    directionView.setActivated(false);
                    buttonStart.setVisibility(View.VISIBLE);
                    buttonStart.setActivated(true);
                    buttonStop.setVisibility(View.VISIBLE);
                    buttonStop.setActivated(true);

                }
                else{
                    directionView.setVisibility(View.VISIBLE);
                    directionView.setActivated(true);
                    buttonStart.setVisibility(View.GONE);
                    buttonStart.setActivated(false);
                    buttonStop.setVisibility(View.GONE);
                    buttonStop.setActivated(false);
                }
            }
        });

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: implement start function
                Toast.makeText(fragmentBelongActivity, "Start", Toast.LENGTH_SHORT).show();
                buttonStart.setEnabled(false);
                buttonStop.setEnabled(true);
            }
        });

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: implement stop function
                Toast.makeText(fragmentBelongActivity, "Stop", Toast.LENGTH_SHORT).show();
                buttonStop.setEnabled(false);
                buttonStart.setEnabled(true);
            }
        });

        buttonL1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.sendMessage(sharedPreferences.getString(Config.F1_BUTTON, ""));
                Toast.makeText(fragmentBelongActivity, sharedPreferences.getString(Config.F1_BUTTON, ""), Toast.LENGTH_SHORT).show();
            }
        });

        buttonL2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.sendMessage(sharedPreferences.getString(Config.F2_BUTTON, ""));
                Toast.makeText(fragmentBelongActivity, sharedPreferences.getString(Config.F2_BUTTON, ""), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void update() {
        BluetoothMessage bluetoothMessage = (BluetoothMessage) topic.getUpdate(this);

        try {
            JSONObject msg = new JSONObject(bluetoothMessage.getMessage());

            if(msg != null) {
                if (msg.getString("message") != null && msg.getString("message").length() > 0) {
                    // To identify the message (JSON Format)
                    // { "message": "<message>" }
                    statusTextView.setText(msg.getString("message"));
                } else if (msg.getJSONObject("arrow") != null) {
                /* To identify the arrows (JSON Format)
                { "arrow":
                    {
                        "x": 10,
                        "y": 10,
                        "direction": "left",
                        "from": "down"
                    }
                }
                */
                    if (msg.getJSONObject("arrow").getString("direction").equals("up")) {
                        imageAdapter.mThumbIds[Math.abs(19 - msg.getJSONObject("arrow").getInt("y")) * 15 + msg.getJSONObject("arrow").getInt("x")] = 3;

                    } else if (msg.getJSONObject("arrow").getString("direction").equals("down")) {
                        imageAdapter.mThumbIds[Math.abs(19 - msg.getJSONObject("arrow").getInt("y")) * 15 + msg.getJSONObject("arrow").getInt("x")] = 4;

                    } else if (msg.getJSONObject("arrow").getString("direction").equals("left")) {
                        imageAdapter.mThumbIds[Math.abs(19 - msg.getJSONObject("arrow").getInt("y")) * 15 + msg.getJSONObject("arrow").getInt("x")] = 5;

                    } else if (msg.getJSONObject("arrow").getString("direction").equals("right")) {
                        imageAdapter.mThumbIds[Math.abs(19 - msg.getJSONObject("arrow").getInt("y")) * 15 + msg.getJSONObject("arrow").getInt("x")] = 6;

                    }
                    imageAdapter.notifyDataSetChanged();
                } else if (msg.getString("status") != null && msg.getString("status").length() > 0) {
                    if (robot.isHeadPosition() && robot.isBodyPosition() && robot.isWaypoint()) {
                        try {
                            statusTextView.setText(msg.getString("status"));
                            switch (msg.getString("status")) {
                                case "moving right":
                                    movementController.turnRight(robot);
                                    break;
                                case "moving left":
                                    movementController.turnLeft(robot);
                                    break;
                                case "moving forward":
                                    movementController.moveForward(robot);
                                    break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setSubject(Subject sub) {
        this.topic = sub;
    }

}
