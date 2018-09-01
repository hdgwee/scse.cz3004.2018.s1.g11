package shadowbotz.shadowbotz.View;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import shadowbotz.shadowbotz.Config;
import shadowbotz.shadowbotz.Controller.PersistentController;
import shadowbotz.shadowbotz.R;

public class RightFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_right, container, false);

        // Get Fragment belonged Activity
        final FragmentActivity fragmentBelongActivity = getActivity();

        //for onCreateOptionsMenu
        setHasOptionsMenu(true);

        final SharedPreferences sharedPreferences; //TODO: to test sharedpreference
        PersistentController persistentController = new PersistentController();//TODO: to test sharedpreference

        sharedPreferences = fragmentBelongActivity.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE); //TODO: to test sharedpreference
//        persistentController.f1Andf2Button(fragmentBelongActivity, Config.F1_BUTTON, "testing"); //TODO: to test sharedpreference

        if (view!=null){

            FragmentManager fragmentManager = fragmentBelongActivity.getSupportFragmentManager();

            // Get right Fragment object & manage right fragment's widget
            Fragment leftFragment = fragmentManager.findFragmentById(R.id.fragmentLeft);
            final EditText leftEditText = leftFragment.getView().findViewById(R.id.editTextLeft);

            FloatingActionButton fab =  view.findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Snackbar.make(view, sharedPreferences.getString(Config.F1_BUTTON, ""), Snackbar.LENGTH_LONG) //TODO: to test sharedpreference
                            .setAction("Action", null).show();
                    leftEditText.setText(sharedPreferences.getString(Config.F1_BUTTON, ""));
                }
            });



        }

        return view;
    }
}
