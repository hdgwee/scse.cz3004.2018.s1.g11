package shadowbotz.shadowbotz.View;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.Set;

import io.realm.Realm;
import shadowbotz.shadowbotz.BluetoothObserverSubject.BluetoothSubject;
import shadowbotz.shadowbotz.Config;
import shadowbotz.shadowbotz.Controller.BluetoothController;
import shadowbotz.shadowbotz.Controller.PersistentController;
import shadowbotz.shadowbotz.Model.BluetoothMessage;
import shadowbotz.shadowbotz.R;
import shadowbotz.shadowbotz.Service.BluetoothMessagingService;
import shadowbotz.shadowbotz.View.Bluetooth.BluetoothActivity;
import shadowbotz.shadowbotz.View.Bluetooth.DeviceListActivity;

public class MainActivity extends AppCompatActivity {

    public static BluetoothSubject bluetoothSubject = new BluetoothSubject();

    public SharedPreferences sharedPreferences;
    // PersistentController persistentController = new PersistentController();

    private Realm realm;
    private static BluetoothMessagingService bluetoothMessagingService = null;
    private BluetoothAdapter bluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private BluetoothDevice device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Realm.init(getApplicationContext());
        realm = Realm.getDefaultInstance();

        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        // persistentController.f1Andf2Button(BluetoothActivity.this, Config.F1_BUTTON, "testing");

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, sharedPreferences.getString(Config.F1_BUTTON, ""), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        requestForAccessFineLocationPermission();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            // Device does not have Bluetooth
        } else if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, Config.REQUEST_ENABLE_BT);
        }

        if (bluetoothAdapter != null) {
            pairedDevices = bluetoothAdapter.getBondedDevices();
        }

        bluetoothMessagingService = new BluetoothMessagingService(null, mHandler);
        bluetoothMessagingService.start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Config.REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    String address = "";

                    // Get the device MAC address
                    if (data.getExtras() != null) {
                        address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    }

                    device = bluetoothAdapter.getRemoteDevice(address);
                    for (BluetoothDevice bt : pairedDevices) {
                        if (bt.getAddress().equals(device.getAddress())) {
                            Config.paired_device_name = device.getName();
                            break;
                        }
                    }

                    if(Config.paired_device_name.length() == 0) {
                        Config.paired_device_name = device.getAddress();
                    }

                    bluetoothMessagingService.connect(device, true);
                }
                break;
            case Config.REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    String address = "";

                    // Get the device MAC address
                    if (data.getExtras() != null) {
                        address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    }

                    device = bluetoothAdapter.getRemoteDevice(address);
                    for (BluetoothDevice bt : pairedDevices) {
                        if (bt.getAddress().equals(device.getAddress())) {
                            Config.paired_device_name = device.getName();
                            break;
                        }
                    }

                    if(Config.paired_device_name.length() == 0) {
                        Config.paired_device_name = device.getAddress();
                    }

                    bluetoothMessagingService.connect(device, true);
                }
                break;
            case Config.REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Toast.makeText(this, R.string.bluetooth_not_enabled,
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case Config.REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    Toast.makeText(this
                            , "Please enable \"Access Fine Location\" permission to continue..."
                            , Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (bluetoothMessagingService != null) {
            bluetoothMessagingService.stop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
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

        if (id == R.id.secure_connect_scan) {
            // Launch the DeviceListActivity to see devices and do scan
            Intent serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, Config.REQUEST_CONNECT_DEVICE_SECURE);
            return true;
        } else if (id == R.id.persistent_storage) {
            // Open persistent string dialog for user input
            PersistentController.f1AndF2Dialog(this);
            return true;
        } else if (id == R.id.action_bluetooth_log) {
            Intent intent = new Intent(getApplicationContext(), BluetoothActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.discoverable) {
            // Ensure this device is discoverable by others
            BluetoothController.turnOnDiscoverable(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @SuppressLint("StringFormatInvalid")
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == Config.MESSAGE_STATE_CHANGE) {
                if (msg.arg1 == BluetoothMessagingService.STATE_CONNECTED) {
                    Config.current_bluetooth_state = getString(R.string.title_bluetooth_connected_to, Config.paired_device_name);
                }
                else if (msg.arg1 == BluetoothMessagingService.STATE_CONNECTING) {
                    Config.current_bluetooth_state = getString(R.string.title_bluetooth_connecting);
                }
                else if (msg.arg1 == BluetoothMessagingService.STATE_LISTEN) {
                    Config.current_bluetooth_state = getString(R.string.title_bluetooth_listening);
                }
                else if (msg.arg1 == BluetoothMessagingService.STATE_NONE) {
                    Config.current_bluetooth_state = getString(R.string.title_bluetooth_disconnected);
                }

                ActionBar actionBar = getSupportActionBar();

                if (actionBar != null) {
                    actionBar.setTitle("shadowbotz - " + Config.current_bluetooth_state);
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

    private void requestForAccessFineLocationPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?

            // if (ActivityCompat.shouldShowRequestPermissionRationale(this,
            //         Manifest.permission.ACCESS_FINE_LOCATION)) {

            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.

            // } else {

            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    Config.REQUEST_ACCESS_FINE_LOCATION);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.

            // }
        }
        // else {
        // Permission has already been granted
        // }
    }

    // Method to send message out to Raspberry Pi or AMD Tool
    public static void sendMessage(String message) {
        if (bluetoothMessagingService != null) {
            // Check that there's actually something to send
            if (message.length() > 0) {
                // Get the message bytes and tell the BluetoothMessagingService to write
                byte[] send = message.getBytes();
                bluetoothMessagingService.write(send);
            }
        }
    }
}
