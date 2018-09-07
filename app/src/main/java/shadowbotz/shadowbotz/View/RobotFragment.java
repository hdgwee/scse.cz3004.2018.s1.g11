package shadowbotz.shadowbotz.View;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
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

import shadowbotz.shadowbotz.Config;
import shadowbotz.shadowbotz.Controller.ImageAdapter;
import shadowbotz.shadowbotz.Controller.MovementController;
import shadowbotz.shadowbotz.R;

public class RobotFragment extends Fragment {


    private int count = 2;
    private int head, body;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_robot, container, false);

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

        final MovementController movementController = new MovementController(imageAdapter);

        //for onCreateOptionsMenu
        setHasOptionsMenu(true);

        GridView gridview = (GridView) view.findViewById(R.id.gridview);
        gridview.setAdapter(imageAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() { //set robot body and head
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                if((position%15>=1 && position%15<=13) && (Math.abs(position/15) >=1 && Math.abs(position/15) <=18)){
                    if(count == 2){
                        body = position;
                        movementController.setBody(body); //set the starting position of the robot
                        count --;
                    }
                    else if(count == 1){
                        head=position;
                        if(head == body+1 || head == body-1 ||head == body+15 ||head == body-15 ){  //make sure head is at the correct position
                            movementController.setHead(head);
                            count --;
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
                if (count==0){
                    Toast.makeText(fragmentBelongActivity,  Math.abs(i/15)+", "+i%15, Toast.LENGTH_SHORT).show();
                    statusTextView.setText("Way point: " +Math.abs(i/15)+", "+i%15);
                }
                else{
                    Toast.makeText(fragmentBelongActivity, "Please set initial position of robot first!", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });



        directionView.setOnButtonListener(new InputView.InputEventListener() {
            @Override public void onInputEvent(View view, int buttons) {

                if(count == 0){
                    switch (buttons&0xff) {
                        case DirectionView.DIRECTION_DOWN:
                            Toast.makeText(fragmentBelongActivity, "Not in use!", Toast.LENGTH_SHORT).show();
                            break;
                        case DirectionView.DIRECTION_RIGHT:

                            if(head == body+1){ //face right
                                head = movementController.turnRightwhenFaceRight(head, body);

                            }
                            else if(head == body-1) { //face left

                                head = movementController.turnRightwhenFaceLeft(head, body);

                            }
                            else if(head == body-15) { //face up

                                head = movementController.turnRightwhenFaceUp(head, body);

                            }
                            else{ //face down
                                head = movementController.turnRightwhenFaceDown(head, body);
                            }
                            break;

                        case DirectionView.DIRECTION_LEFT:
                            if(head == body+1){ //face right
                                head = movementController.turnLeftwhenFaceRight(head, body);
                            }
                            else if(head == body-1) { //face left
                                head = movementController.turnLeftwhenFaceLeft(head, body);
                            }
                            else if(head == body-15) { //face up
                                head = movementController.turnLeftwhenFaceUp(head, body);
                            }
                            else{ //face down
                                head = movementController.turnLeftwhenFaceDown(head, body);
                            }
                            break;
                        case DirectionView.DIRECTION_UP: //move forward
                            if((head%15>=1 && head%15<=13) && (Math.abs(head/15) >=1 && Math.abs(head/15) <=18)){

                                if(head == body+1){ //face right
                                    movementController.moveForwardWhenFaceRight(head, body);
                                    //increase current of head and body by 1 grid forward
                                    head ++;
                                    body++;
                                }
                                else if(head == body-1){ //face left
                                    movementController.moveForwardWhenFaceLeft(head, body);
                                    //increase current of head and body by 1 grid forward
                                    head --;
                                    body --;
                                }
                                else if(head == body-15){ //face up
                                    movementController.moveForwardWhenFaceUp(head, body);
                                    //increase current of head and body by 1 grid forward
                                    head -= 15;
                                    body -= 15;
                                }
                                else{ //face down
                                    movementController.moveForwardWhenFaceDown(head, body);
                                    //increase current of head and body by 1 grid forward
                                    head +=15;
                                    body +=15;
                                }
                            }
                            else{
                                Toast.makeText(fragmentBelongActivity, "Out of bound!", Toast.LENGTH_SHORT).show();
                            }
                            break;
                    }
                    imageAdapter.notifyDataSetChanged();

                }
                else{
                    Toast.makeText(fragmentBelongActivity, "Robot position not set!", Toast.LENGTH_SHORT).show();
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

}
