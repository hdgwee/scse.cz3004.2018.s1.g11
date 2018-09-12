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
import shadowbotz.shadowbotz.Controller.ImageAdapter;
import shadowbotz.shadowbotz.Controller.MovementController;
import shadowbotz.shadowbotz.Model.Robot;
import shadowbotz.shadowbotz.R;

public class RobotFragment extends Fragment implements Observer {


    private Robot robot = new Robot();

    // Observer pattern
    private Subject topic;
    private MovementController movementController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_robot, container, false);

        // Observer pattern
        MainActivity.bluetoothSubject.register(this);
        this.setSubject(MainActivity.bluetoothSubject);

        // Get Fragment belonged Activity
        final FragmentActivity fragmentBelongActivity = getActivity();
        final FloatingActionButton fab =  view.findViewById(R.id.fab);
        final SharedPreferences sharedPreferences = fragmentBelongActivity.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE); //TODO: to test sharedpreference;
        final DirectionView directionView = (DirectionView) view.findViewById(R.id.viewDirection);

        final Switch switchMovement = view.findViewById(R.id.switchMovement);
        final Switch switchUpdate = view.findViewById(R.id.switchUpdate);
        final Button buttonStart = view.findViewById(R.id.buttonStart);
        final Button buttonStop = view.findViewById(R.id.buttonStop);

        final TextView statusTextView = view.findViewById(R.id.statusTextView);

        Button buttonL1 = view.findViewById(R.id.buttonL1);
        Button buttonL2 = view.findViewById(R.id.buttonL2);

        final ImageAdapter imageAdapter = new ImageAdapter(fragmentBelongActivity);

        movementController = new MovementController(imageAdapter, getActivity());

        //for onCreateOptionsMenu
        setHasOptionsMenu(true);

        GridView gridview = (GridView) view.findViewById(R.id.gridview);
        gridview.setAdapter(imageAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() { //set robot body and head
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                if((position%15>=1 && position%15<=13) && (Math.abs(position/15) >=1 && Math.abs(position/15) <=18)){
                    if(!robot.isBodyPosition()){
                        robot.setBody(position);
                        movementController.setBody(robot); //set the starting position of the robot
                        robot.setBodyPosition(true);
                    }
                    else if(!robot.isHeadPosition()){
                        robot.setHead(position);
                            if(robot.getHead() == robot.getBody()+1 || robot.getHead() == robot.getBody()-1 || robot.getHead() == robot.getBody()+15 || robot.getHead() == robot.getBody()-15 ){  //make sure head is at the correct position
                                movementController.setHead(robot);
                                robot.setHeadPosition(true);
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
        });
        gridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { //set way point
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (robot.isHeadPosition()&& robot.isBodyPosition() && !robot.isWaypoint()){
                    robot.setWaypointPosition(i);
                    movementController.setWayPoint(robot, statusTextView);
                    robot.setWaypoint(true);
                }
                else if(robot.isHeadPosition()&& robot.isBodyPosition() && robot.isWaypoint()){
                    Toast.makeText(fragmentBelongActivity, "Waypoint has been set.", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(fragmentBelongActivity, "Please set initial position of robot first!", Toast.LENGTH_SHORT).show();
                }
                return true;
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
                            MainActivity.sendMessage("tr");
                            break;

                        case DirectionView.DIRECTION_LEFT:
                            movementController.turnLeft(robot);
                            MainActivity.sendMessage("tl");
                            break;
                        case DirectionView.DIRECTION_UP: //move forward
                            movementController.moveForward(robot);
                            MainActivity.sendMessage("f");
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
                    fab.setVisibility(View.INVISIBLE);
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
        buttonStart.setVisibility(View.INVISIBLE);
        buttonStart.setActivated(false);
        buttonStop.setVisibility(View.INVISIBLE);
        buttonStop.setActivated(false);
        buttonStop.setEnabled(false);

        switchMovement.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){ // if auto
                    directionView.setVisibility(View.INVISIBLE);
                    directionView.setActivated(false);
                    buttonStart.setVisibility(View.VISIBLE);
                    buttonStart.setActivated(true);
                    buttonStop.setVisibility(View.VISIBLE);
                    buttonStop.setActivated(true);

                }
                else{
                    directionView.setVisibility(View.VISIBLE);
                    directionView.setActivated(true);
                    buttonStart.setVisibility(View.INVISIBLE);
                    buttonStart.setActivated(false);
                    buttonStop.setVisibility(View.INVISIBLE);
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
        String stringMessage = (String) topic.getUpdate(this);

        JSONObject msg = null;
        try {
            msg = new JSONObject(stringMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Todo: Bug fix
        if (msg != null) {
            try {
                // Todo: Unable to detect string send from AMD Tool bug
                switch (msg.getString("status")) {
                    case "moving right":
                        Log.e("RobotFragmentMovement", "moving right");
                        movementController.turnRight(robot);
                        break;
                    case "moving left":
                        Log.e("RobotFragmentMovement", "moving left");
                        movementController.turnLeft(robot);
                        break;
                    case "moving forward":
                        movementController.moveForward(robot);
                        break;
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        else {
            // No new message
        }
    }

    @Override
    public void setSubject(Subject sub) {
        this.topic = sub;
    }

}
