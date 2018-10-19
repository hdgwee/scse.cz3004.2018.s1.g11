package shadowbotz.shadowbotz.View.Bluetooth;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import shadowbotz.shadowbotz.Adapter.BluetoothMessageAdapter;
import shadowbotz.shadowbotz.Adapter.ListViewListener;
import shadowbotz.shadowbotz.BluetoothObserverSubject.Observer;
import shadowbotz.shadowbotz.BluetoothObserverSubject.Subject;
import shadowbotz.shadowbotz.Config;
import shadowbotz.shadowbotz.Model.BluetoothMessage;
import shadowbotz.shadowbotz.R;
import shadowbotz.shadowbotz.View.MainActivity;

public class BluetoothLogActivity extends AppCompatActivity implements Observer {

    private EditText editTextOutgoingMessage;
    private Button buttonSend;
    private RecyclerView recyclerChatList;
    private ActionBar actionBar;

    private BluetoothMessageAdapter bluetoothMessageAdapter;
    private ArrayList<BluetoothMessage> bluetoothMessageArrayList = new ArrayList<>();
    private ListViewListener listViewListener = new ListViewListener() {
        @Override
        public void onSelected(int position) {}
    };

    // Observer pattern
    private Subject topic;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_log);

        // Full Screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        this.setFinishOnTouchOutside(false);

        initializeUserInterface();

        // Observer pattern
        MainActivity.bluetoothSubject.register(this);
        this.setSubject(MainActivity.bluetoothSubject);
    }

    // Set up the UI and background operations for chat
    private void initializeUserInterface() {
        actionBar = getSupportActionBar();

        editTextOutgoingMessage = findViewById(R.id.editTextOutgoingMessage);
        buttonSend = findViewById(R.id.buttonSend);
        recyclerChatList = findViewById(R.id.recyclerChatList);

        // Initialize the compose field with a listener for the return key
        editTextOutgoingMessage.setOnEditorActionListener(mWriteListener);

        // Initialize the send button with a listener that for click events
        buttonSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                String message = editTextOutgoingMessage.getText().toString();
                sendMessage(message);
                editTextOutgoingMessage.setText("");
            }
        });

        bluetoothMessageAdapter = new BluetoothMessageAdapter(this, bluetoothMessageArrayList, listViewListener);
        recyclerChatList.setAdapter(bluetoothMessageAdapter);
        bluetoothMessageAdapter.setCurrentDeviceAddress(Config.my_bluetooth_device_address);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerChatList.setLayoutManager(linearLayoutManager);

        Realm realm = Realm.getDefaultInstance();

        RealmQuery<BluetoothMessage> query = realm.where(BluetoothMessage.class);
        RealmResults<BluetoothMessage> result = query.findAll();

        if(result.size() > 10) {
            bluetoothMessageArrayList.addAll(result.subList(result.size()-10, result.size()-1));
        }
        else {
            bluetoothMessageArrayList.addAll(result);
        }

        bluetoothMessageAdapter.update(bluetoothMessageArrayList);
        recyclerChatList.scrollToPosition(bluetoothMessageArrayList.size() - 1);
        realm.close();
    }

    @Override
    public void onResume() {
        // MainActivity.bluetoothSubject.register(this);

        checkCurrentStateOfConnectionToAllowInput();

        super.onResume();
    }

    @Override
    public void onPause() {
        // MainActivity.bluetoothSubject.unregister(this);

        super.onPause();
    }

    // Action listener for the EditText widget to listen for the return key
    private TextView.OnEditorActionListener mWriteListener
            = new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            // If the action is a key-up event on the return key, send the message
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                sendMessage(message);
            }
            return true;
        }
    };

    private void sendMessage(String message) {
        if(Config.current_bluetooth_state.equals(
                getString(R.string.title_bluetooth_connected_to, Config.paired_device_name))) {
            MainActivity.sendMessage(message);
        }
        else {
            checkCurrentStateOfConnectionToAllowInput();
            Toast.makeText(this
                    , "Bluetooth is not connected!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void update() {
        checkCurrentStateOfConnectionToAllowInput();

        BluetoothMessage bluetoothMessage = (BluetoothMessage) topic.getUpdate(this);

        if (bluetoothMessage != null) {
            bluetoothMessageArrayList.add(bluetoothMessage);
            bluetoothMessageAdapter.update(bluetoothMessageArrayList);
            recyclerChatList.scrollToPosition(bluetoothMessageArrayList.size() - 1);
        } else {
            // No new message
        }
    }

    @Override
    public void setSubject(Subject sub) {
        this.topic = sub;
    }

    private void checkCurrentStateOfConnectionToAllowInput() {
        if(!Config.current_bluetooth_state.contains("Connected to")) {
            buttonSend.setEnabled(false);
            editTextOutgoingMessage.setEnabled(false);
        }
        else {
            buttonSend.setEnabled(true);
            editTextOutgoingMessage.setEnabled(true);
        }

        if (actionBar != null) {
            actionBar.setTitle("Bluetooth Log - " + Config.current_bluetooth_state);
        }
    }
}
