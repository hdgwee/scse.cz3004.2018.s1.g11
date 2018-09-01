package shadowbotz.shadowbotz.View;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import shadowbotz.shadowbotz.R;

public class LeftFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_left, container, false);

        // Get Fragment belonged Activity
        final FragmentActivity fragmentBelongActivity = getActivity();

        //for onCreateOptionsMenu
        setHasOptionsMenu(true);

        if (view!=null){
            FragmentManager fragmentManager = fragmentBelongActivity.getSupportFragmentManager();
            // Get right Fragment object & manage right fragment's widget
            Fragment rightFragment = fragmentManager.findFragmentById(R.id.fragmentRight);

            EditText statusEditText = view.findViewById(R.id.editTextLeft);

            statusEditText.setEnabled(false);
        }

        return view;
    }
}
