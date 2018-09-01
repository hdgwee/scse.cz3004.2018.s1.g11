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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Switch;
import android.widget.Toast;

import com.andretietz.android.controller.DirectionView;
import com.andretietz.android.controller.InputView;

import shadowbotz.shadowbotz.Config;
import shadowbotz.shadowbotz.Controller.ImageAdapter;
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

        final EditText leftEditText = view.findViewById(R.id.editTextLeft);

        Button buttonL1 = view.findViewById(R.id.buttonL1);
        Button buttonL2 = view.findViewById(R.id.buttonL2);

        final ImageAdapter imageAdapter = new ImageAdapter(fragmentBelongActivity);

        //for onCreateOptionsMenu
        setHasOptionsMenu(true);

        EditText statusEditText = view.findViewById(R.id.editTextLeft);
        statusEditText.setEnabled(false);

        GridView gridview = (GridView) view.findViewById(R.id.gridview);
        gridview.setAdapter(imageAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() { //set robot body and head
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                if((position%15>=1 && position%15<=13) && (Math.abs(position/15) >=1 && Math.abs(position/15) <=18)){
                    if(count == 2){
                        body = position;
                        imageAdapter.mThumbIds[body] = 8;
                        //4 corners
                        imageAdapter.mThumbIds[body-14] = 8; //set the whole body
                        imageAdapter.mThumbIds[body-16] = 8;
                        imageAdapter.mThumbIds[body+14] = 8;
                        imageAdapter.mThumbIds[body+16] = 8;

                        //the rest
                        imageAdapter.mThumbIds[body+1] = 8;
                        imageAdapter.mThumbIds[body-1] = 8;
                        imageAdapter.mThumbIds[body+15] = 8;
                        imageAdapter.mThumbIds[body-15] = 8;
                        count --;
                    }
                    else if(count == 1){
                        head=position;
                        if(head == body+1 || head == body-1 ||head == body+15 ||head == body-15 ){
                            imageAdapter.mThumbIds[head] = 9;
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
                Toast.makeText(fragmentBelongActivity,  Math.abs(i/15)+", "+i%15,
                        Toast.LENGTH_SHORT).show();
                leftEditText.setText("Way point: " +Math.abs(i/15)+", "+i%15); //to edit the left fragment
                return false;
            }
        });



        directionView.setOnButtonListener(new InputView.InputEventListener() {
            @Override public void onInputEvent(View view, int buttons) {
                //TODO: implment manual movement
                switch (buttons&0xff) {
                    case DirectionView.DIRECTION_DOWN:
                        Toast.makeText(fragmentBelongActivity, "Not in use!", Toast.LENGTH_SHORT).show();
                        break;
                    case DirectionView.DIRECTION_RIGHT:

                        if(head == body+1){ //face right
                            imageAdapter.mThumbIds[head] = 8;
                            head = body+15;
                            imageAdapter.mThumbIds[head] = 9;

                        }
                        else if(head == body-1) { //face left
                            imageAdapter.mThumbIds[head] = 8;
                            head = body-15;
                            imageAdapter.mThumbIds[head] = 9;

                        }
                        else if(head == body-15) { //face up
                            imageAdapter.mThumbIds[head] = 8;
                            head = body+1;
                            imageAdapter.mThumbIds[head] = 9;

                        }
                        else{ //face down
                            imageAdapter.mThumbIds[head] = 8;
                            head = body-1;
                            imageAdapter.mThumbIds[head] = 9;
                        }
                        break;
                    case DirectionView.DIRECTION_LEFT:
                        if(head == body+1){ //face right
                            imageAdapter.mThumbIds[head] = 8;
                            head = body-15;
                            imageAdapter.mThumbIds[head] = 9;

                        }
                        else if(head == body-1) { //face left
                            imageAdapter.mThumbIds[head] = 8;
                            head = body+15;
                            imageAdapter.mThumbIds[head] = 9;

                        }
                        else if(head == body-15) { //face up
                            imageAdapter.mThumbIds[head] = 8;
                            head = body-1;
                            imageAdapter.mThumbIds[head] = 9;

                        }
                        else{ //face down
                            imageAdapter.mThumbIds[head] = 8;
                            head = body+1;
                            imageAdapter.mThumbIds[head] = 9;
                        }
                        break;
                    case DirectionView.DIRECTION_UP: //move forward
                        if((head%15>=1 && head%15<=13) && (Math.abs(head/15) >=1 && Math.abs(head/15) <=18)){

                            if(head == body+1){ //face right
                                imageAdapter.mThumbIds[head+1] = 9;

                                //4 corners
                                imageAdapter.mThumbIds[(body-14)+1] = 8; //set the whole body
                                imageAdapter.mThumbIds[(body-16)+1] = 8;
                                imageAdapter.mThumbIds[(body+14)+1] = 8;
                                imageAdapter.mThumbIds[(body+16)+1] = 8;

                                //the rest
                                imageAdapter.mThumbIds[(body)+1] = 8;
                                imageAdapter.mThumbIds[(body-1)+1] = 8;
                                imageAdapter.mThumbIds[(body+15)+1] = 8;
                                imageAdapter.mThumbIds[(body-15)+1] = 8;

                                //convert back of robot to explored
                                imageAdapter.mThumbIds[(body-1)] = 1;
                                imageAdapter.mThumbIds[(body-16)] = 1;
                                imageAdapter.mThumbIds[(body+14)] = 1;
                                head ++;
                                body++;
                            }
                            else if(head == body-1){ //face left
                                imageAdapter.mThumbIds[head-1] = 9;

                                //4 corners
                                imageAdapter.mThumbIds[(body-14)-1] = 8; //set the whole body
                                imageAdapter.mThumbIds[(body-16)-1] = 8;
                                imageAdapter.mThumbIds[(body+14)-1] = 8;
                                imageAdapter.mThumbIds[(body+16)-1] = 8;

                                //the rest
                                imageAdapter.mThumbIds[(body)-1] = 8;
                                imageAdapter.mThumbIds[(body+1)-1] = 8;
                                imageAdapter.mThumbIds[(body+15)-1] = 8;
                                imageAdapter.mThumbIds[(body-15)-1] = 8;

                                //convert back of robot to explored
                                imageAdapter.mThumbIds[(body+1)] = 1;
                                imageAdapter.mThumbIds[(body+16)] = 1;
                                imageAdapter.mThumbIds[(body-14)] = 1;
                                head --;
                                body --;
                            }
                            else if(head == body-15){ //face up
                                imageAdapter.mThumbIds[head-15] = 9;

                                //4 corners
                                imageAdapter.mThumbIds[(body-14)-15] = 8; //set the whole body
                                imageAdapter.mThumbIds[(body-16)-15] = 8;
                                imageAdapter.mThumbIds[(body+14)-15] = 8;
                                imageAdapter.mThumbIds[(body+16)-15] = 8;

                                //the rest
                                imageAdapter.mThumbIds[(body)-15] = 8;
                                imageAdapter.mThumbIds[(body-1)-15] = 8;
                                imageAdapter.mThumbIds[(body+15)-15] = 8;
                                imageAdapter.mThumbIds[(body+1)-15] = 8;

                                //convert back of robot to explored
                                imageAdapter.mThumbIds[(body+14)] = 1;
                                imageAdapter.mThumbIds[(body+15)] = 1;
                                imageAdapter.mThumbIds[(body+16)] = 1;
                                head -= 15;
                                body -= 15;
                            }
                            else{ //face down
                                imageAdapter.mThumbIds[head+15] = 9;

                                //4 corners
                                imageAdapter.mThumbIds[(body-14)+15] = 8; //set the whole body
                                imageAdapter.mThumbIds[(body-16)+15] = 8;
                                imageAdapter.mThumbIds[(body+14)+15] = 8;
                                imageAdapter.mThumbIds[(body+16)+15] = 8;

                                //the rest
                                imageAdapter.mThumbIds[(body)+15] = 8;
                                imageAdapter.mThumbIds[(body-1)+15] = 8;
                                imageAdapter.mThumbIds[(body+1)+15] = 8;
                                imageAdapter.mThumbIds[(body-15)+15] = 8;

                                //convert back of robot to explored
                                imageAdapter.mThumbIds[(body-14)] = 1;
                                imageAdapter.mThumbIds[(body-15)] = 1;
                                imageAdapter.mThumbIds[(body-16)] = 1;
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
                //TODO: implement send over bluetooth
                Toast.makeText(fragmentBelongActivity, sharedPreferences.getString(Config.F1_BUTTON, ""), Toast.LENGTH_SHORT).show();
            }
        });

        buttonL2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: implement send over bluetooth
                Toast.makeText(fragmentBelongActivity, sharedPreferences.getString(Config.F2_BUTTON, ""), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

}
