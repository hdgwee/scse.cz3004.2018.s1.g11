package shadowbotz.shadowbotz.View.Bluetooth;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import shadowbotz.shadowbotz.Config;
import shadowbotz.shadowbotz.R;
import shadowbotz.shadowbotz.View.MainActivity;

public class PersistentStorageActivity extends AppCompatActivity {

    private EditText editTextStringF1, editTextStringF2;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    public PersistentStorageActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persistent_storage);

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();

        editTextStringF1 = findViewById(R.id.editTextStringF1);
        editTextStringF1.setText(
                sharedPref.getString(Config.F1_BUTTON, "")
        );
        editTextStringF1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                editor.putString(Config.F1_BUTTON, editable.toString());
                editor.apply();
            }
        });

        editTextStringF2 = findViewById(R.id.editTextStringF2);
        editTextStringF2.setText(
                sharedPref.getString(Config.F2_BUTTON, "")
        );
        editTextStringF2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                editor.putString(Config.F2_BUTTON, editable.toString());
                editor.apply();
            }
        });

        Button btnSendF1 = findViewById(R.id.btnSendF1);
        btnSendF1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.sendMessage(
                        editTextStringF1.getText().toString());
            }
        });

        Button btnSendF2 = findViewById(R.id.btnSendF2);
        btnSendF2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.sendMessage(
                        editTextStringF2.getText().toString());
            }
        });
    }

}
