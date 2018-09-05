package shadowbotz.shadowbotz.View;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import io.realm.Realm;
import shadowbotz.shadowbotz.BluetoothObserverSubject.BluetoothSubject;
import shadowbotz.shadowbotz.Config;
import shadowbotz.shadowbotz.Controller.BluetoothController;
import shadowbotz.shadowbotz.Model.BluetoothMessage;
import shadowbotz.shadowbotz.R;
import shadowbotz.shadowbotz.Service.BluetoothMessagingService;
import shadowbotz.shadowbotz.View.Bluetooth.BluetoothActivity;
import shadowbotz.shadowbotz.View.Bluetooth.DeviceListActivity;
import shadowbotz.shadowbotz.View.Bluetooth.PersistentStorageActivity;

public class MainActivity extends AppCompatActivity {

    public static BluetoothSubject bluetoothSubject = new BluetoothSubject();

    public SharedPreferences sharedPreferences; //TODO: to test sharedpreference
    // PersistentController persistentController = new PersistentController(); //TODO: to test sharedpreference

    private Realm realm;
    private static BluetoothMessagingService mChatService = null;
    private BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Realm.init(getApplicationContext());
        realm = Realm.getDefaultInstance();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE); //TODO: to test sharedpreference
        // persistentController.f1Andf2Button(BluetoothActivity.this, Config.F1_BUTTON, "testing"); //TODO: to test sharedpreference

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, sharedPreferences.getString(Config.F1_BUTTON, ""), Snackbar.LENGTH_LONG) //TODO: to test sharedpreference
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mChatService != null) {
            mChatService.stop();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.secure_connect_scan) {
            // Launch the DeviceListActivity to see devices and do scan
            Intent serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, Config.REQUEST_CONNECT_DEVICE_SECURE);
            return true;
        }
        else if (id == R.id.persistent_storage) {
            // Launch the DeviceListActivity to see devices and do scan
            Intent intent = new Intent(this, PersistentStorageActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_bluetooth_log) {
            Intent intent = new Intent(getApplicationContext(), BluetoothActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.discoverable) {
            // Ensure this device is discoverable by others
            BluetoothController.turnOnDiscoverable(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Config.REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    String address = "";

                    // Get the device MAC address
                    if(data.getExtras() != null) {
                        address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    }

                    BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);

                    mChatService = new BluetoothMessagingService(null, mHandler);
                    mChatService.connect(device, true);
                }
                break;
            case Config.REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    String address = "";

                    // Get the device MAC address
                    if(data.getExtras() != null) {
                        address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    }

                    BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);

                    mChatService = new BluetoothMessagingService(null, mHandler);
                    mChatService.connect(device, true);
                }
                break;
            case Config.REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Toast.makeText(this, R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                }
        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @SuppressLint("StringFormatInvalid")
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == Config.MESSAGE_STATE_CHANGE) {
                switch (msg.arg1) {
                    case BluetoothMessagingService.STATE_CONNECTED:
                        Config.current_bluetooth_state = getString(R.string.title_connected_to, Config.paired_device_name);
                        break;
                    case BluetoothMessagingService.STATE_CONNECTING:
                        Config.current_bluetooth_state = getString(R.string.title_connecting);
                        break;
                    case BluetoothMessagingService.STATE_LISTEN:
                        break;
                    case BluetoothMessagingService.STATE_NONE:
                        Config.current_bluetooth_state = getString(R.string.title_not_connected);
                        break;
                }
            } else if (msg.what == Config.MESSAGE_WRITE) {
                // Send a message to connected bluetooth device
                byte[] writeBuf = (byte[]) msg.obj;
                String writeMessage = new String(writeBuf);

                // Save message in realm
                realm.beginTransaction();
                BluetoothMessage bluetoothMessage = new BluetoothMessage();
                bluetoothMessage.setDeviceName(Config.paired_device_name);
                bluetoothMessage.setDeviceAddress("");
                bluetoothMessage.setMessage(writeMessage);
                bluetoothMessage.setDatetime(System.currentTimeMillis() / 1000L);
                realm.commitTransaction();

                Config.sent_message = writeMessage;

                MainActivity.bluetoothSubject.postMessage(writeMessage, "RECEIVED");
            } else if (msg.what == Config.MESSAGE_READ) {
                // Receive a message to connected bluetooth device
                byte[] readBuf = (byte[]) msg.obj;
                String readMessage = new String(readBuf, 0, msg.arg1);

                // Save message in realm
                realm.beginTransaction();
                BluetoothMessage bluetoothMessage = new BluetoothMessage();
                bluetoothMessage.setDeviceName("");
                bluetoothMessage.setDeviceAddress("");
                bluetoothMessage.setMessage(readMessage);
                bluetoothMessage.setDatetime(System.currentTimeMillis() / 1000L);
                realm.commitTransaction();

                Config.received_message = readMessage;

                MainActivity.bluetoothSubject.postMessage(readMessage, "SENT");
            } else if (msg.what == Config.MESSAGE_DEVICE_NAME) {
                // msg.getData().getString(Config.DEVICE_NAME)
            } else if (msg.what == Config.MESSAGE_TOAST) {
                // msg.getData().getString(Config.TOAST)
            }
        }
    };

    // Method to send message out to Raspberry Pi or AMD Tool
    public static void sendMessage(String message) {
        if(mChatService != null) {
            // Check that there's actually something to send
            if (message.length() > 0) {
                // Get the message bytes and tell the BluetoothMessagingService to write
                byte[] send = message.getBytes();
                mChatService.write(send);
            }
        }
    }
}
