package shadowbotz.shadowbotz.Controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.EditText;

import shadowbotz.shadowbotz.Config;
import shadowbotz.shadowbotz.R;
import shadowbotz.shadowbotz.View.MainActivity;

public class PersistentController {

    public PersistentController() {
    }

    //Function to save word to shared preferences
    public static void f1AndF2Dialog(Activity activity){
        final SharedPreferences sharedPreferences = activity.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        AlertDialog.Builder alert = new AlertDialog.Builder(activity, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        alert.setTitle("Edit L1 & L2");
        final View l1AndL2 = activity.getLayoutInflater().inflate(R.layout.dialog_l1_l2, null);
        alert.setView(l1AndL2);

        final EditText l1 = l1AndL2.findViewById(R.id.editText);
        final EditText l2 = l1AndL2.findViewById(R.id.editText2);

        l1.setText(sharedPreferences.getString(Config.F1_BUTTON, ""));
        l2.setText(sharedPreferences.getString(Config.F2_BUTTON, ""));

        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Config.F1_BUTTON, l1.getText().toString().trim());
                editor.putString(Config.F2_BUTTON, l2.getText().toString().trim());
                editor.apply();
            }
        });

        alert.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // cancel
            }
        });

        alert.show();

    }
}
