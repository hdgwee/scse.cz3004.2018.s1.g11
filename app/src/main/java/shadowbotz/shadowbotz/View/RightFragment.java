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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.andretietz.android.controller.DirectionView;
import com.andretietz.android.controller.InputView;

import shadowbotz.shadowbotz.Config;
import shadowbotz.shadowbotz.R;

public class RightFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_right, container, false);

        // Get Fragment belonged Activity
        final FragmentActivity fragmentBelongActivity = getActivity();
        final FloatingActionButton fab =  view.findViewById(R.id.fab);
        final SharedPreferences sharedPreferences = fragmentBelongActivity.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE); //TODO: to test sharedpreference;
        final EditText leftEditText;
        final DirectionView directionView = (DirectionView) view.findViewById(R.id.viewDirection);

        final Switch switchMovement = view.findViewById(R.id.switchMovement);
        final Switch switchUpdate = view.findViewById(R.id.switchUpdate);
        final Button buttonStart = view.findViewById(R.id.buttonStart);
        final Button buttonStop = view.findViewById(R.id.buttonStop);

        Button buttonL1 = view.findViewById(R.id.buttonL1);
        Button buttonL2 = view.findViewById(R.id.buttonL2);

        //for onCreateOptionsMenu
        setHasOptionsMenu(true);

        FragmentManager fragmentManager = fragmentBelongActivity.getSupportFragmentManager();

        // Get right Fragment object & manage right fragment's widget
        Fragment leftFragment = fragmentManager.findFragmentById(R.id.fragmentLeft);
        leftEditText = leftFragment.getView().findViewById(R.id.editTextLeft);

        directionView.setOnButtonListener(new InputView.InputEventListener() {
            @Override public void onInputEvent(View view, int buttons) {
                switch (buttons&0xff) {
                    case DirectionView.DIRECTION_DOWN:
                        Toast.makeText(fragmentBelongActivity, "Down", Toast.LENGTH_SHORT).show();
                        break;
                    case DirectionView.DIRECTION_LEFT:
                        Toast.makeText(fragmentBelongActivity, "Left", Toast.LENGTH_SHORT).show();
                        break;
                    case DirectionView.DIRECTION_RIGHT:
                        Toast.makeText(fragmentBelongActivity, "Right", Toast.LENGTH_SHORT).show();
                        break;
                    case DirectionView.DIRECTION_UP:
                        Toast.makeText(fragmentBelongActivity, "Up", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });


        switchUpdate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    fab.setVisibility(View.INVISIBLE);
                    fab.setActivated(false);
                    Toast.makeText(fragmentBelongActivity, "Auto updating", Toast.LENGTH_SHORT).show();
                }
                else{
                    fab.setVisibility(View.VISIBLE);
                    fab.setActivated(true);

                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(fragmentBelongActivity, "Manual Update", Toast.LENGTH_SHORT).show();
//                            leftEditText.setText(sharedPreferences.getString(Config.F1_BUTTON, "")); //to edit the left fragment
                        }
                    });
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
                Toast.makeText(fragmentBelongActivity, "Start", Toast.LENGTH_SHORT).show();
                buttonStart.setEnabled(false);
                buttonStop.setEnabled(true);
            }
        });

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(fragmentBelongActivity, "Stop", Toast.LENGTH_SHORT).show();
                buttonStop.setEnabled(false);
                buttonStart.setEnabled(true);
            }
        });

        buttonL1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(fragmentBelongActivity, sharedPreferences.getString(Config.F1_BUTTON, ""), Toast.LENGTH_SHORT).show();
            }
        });

        buttonL2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(fragmentBelongActivity, sharedPreferences.getString(Config.F2_BUTTON, ""), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
