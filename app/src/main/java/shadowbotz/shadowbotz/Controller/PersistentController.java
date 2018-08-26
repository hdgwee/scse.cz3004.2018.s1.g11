package shadowbotz.shadowbotz.Controller;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import shadowbotz.shadowbotz.Config;

public class PersistentController {

    public PersistentController() {
    }

    //Function to save word to shared preferences
    public void f1Andf2Button(Activity activity, String sharedPreferenceVariable, String stringToAdd){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(sharedPreferenceVariable, stringToAdd);
        editor.apply();
    }
}
