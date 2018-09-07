/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package shadowbotz.shadowbotz.View.Bluetooth;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import shadowbotz.shadowbotz.BluetoothObserverSubject.Observer;
import shadowbotz.shadowbotz.BluetoothObserverSubject.Subject;
import shadowbotz.shadowbotz.Config;
import shadowbotz.shadowbotz.R;
import shadowbotz.shadowbotz.View.MainActivity;

/**
 * This fragment controls Bluetooth to communicate with other devices.
 */
public class BluetoothMessageLogFragment extends Fragment implements Observer {

    // Layout Views
    private ListView listViewForMessage;
    private EditText editTextOutgoingMessage;
    private Button buttonSend;

    // Array adapter for the conversation thread
    private ArrayAdapter<String> mConversationArrayAdapter;

    // Observer pattern
    private Subject topic;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bluetooth_message, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        listViewForMessage = view.findViewById(R.id.listViewForMessage);
        editTextOutgoingMessage = view.findViewById(R.id.editTextOutgoingMessage);
        buttonSend = view.findViewById(R.id.buttonSend);

        initializeUserInterface();

        // Observer pattern
        MainActivity.bluetoothSubject.register(this);
        this.setSubject(MainActivity.bluetoothSubject);
    }

    // Set up the UI and background operations for chat
    private void initializeUserInterface() {
        // Initialize the array adapter for the conversation thread
        mConversationArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.message);

        listViewForMessage.setAdapter(mConversationArrayAdapter);

        // Initialize the compose field with a listener for the return key
        editTextOutgoingMessage.setOnEditorActionListener(mWriteListener);

        // Initialize the send button with a listener that for click events
        buttonSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                View view = getView();
                if (null != view) {
                    TextView textView = view.findViewById(R.id.editTextOutgoingMessage);
                    String message = textView.getText().toString();
                    sendMessage(message);
                }
            }
        });
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
            Toast.makeText(getActivity()
                    , "Bluetooth is NOT connected!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void update() {
        String msg = (String) topic.getUpdate(this);

        if (msg != null) {
            mConversationArrayAdapter.add(msg);
        } else {
            // No new message
        }
    }

    @Override
    public void setSubject(Subject sub) {
        this.topic = sub;
    }
}
