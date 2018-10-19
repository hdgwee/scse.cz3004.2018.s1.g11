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
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Set;
import java.util.UUID;

import io.realm.Realm;
import shadowbotz.shadowbotz.Algo.model.util.SocketMgr;
import shadowbotz.shadowbotz.BluetoothObserverSubject.BluetoothSubject;
import shadowbotz.shadowbotz.Config;
import shadowbotz.shadowbotz.Controller.BluetoothController;
import shadowbotz.shadowbotz.Controller.PersistentController;
import shadowbotz.shadowbotz.Model.BluetoothMessage;
import shadowbotz.shadowbotz.R;
import shadowbotz.shadowbotz.Service.BluetoothMessagingService;
import shadowbotz.shadowbotz.View.Bluetooth.BluetoothLogActivity;
import shadowbotz.shadowbotz.View.Bluetooth.DeviceListActivity;

@SuppressLint("HardwareIds")
public class MainActivity extends AppCompatActivity {

    public static BluetoothSubject bluetoothSubject = new BluetoothSubject();

    public SharedPreferences sharedPreferences;
    // PersistentController persistentController = new PersistentController();

    private static BluetoothMessagingService bluetoothMessagingService = null;
    private BluetoothAdapter bluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private ActionBar actionBar;

    private PowerManager.WakeLock wl;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Realm.init(this);

        setContentView(R.layout.activity_main);

        // Full Screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setElevation(0);
        }

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

            Config.my_bluetooth_device_name = bluetoothAdapter.getName();
            Config.my_bluetooth_device_address = bluetoothAdapter.getAddress();
        }

        bluetoothMessagingService = new BluetoothMessagingService(null, mHandler);
        bluetoothMessagingService.start();

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (pm != null) {
            wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "shadowbotz");
            wl.acquire(10*60*1000L /*10 minutes*/);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Config.REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                BluetoothDevice device;

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
                            Config.paired_device_address = device.getAddress();
                            break;
                        }
                    }

                    if(Config.paired_device_name.length() == 0) {
                        Config.paired_device_name = Config.paired_device_address;
                    }

                    bluetoothMessagingService.connect(device, true);
                }
                else {
                    bluetoothMessagingService.start();
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
                            Config.paired_device_address = device.getAddress();
                            break;
                        }
                    }

                    if(Config.paired_device_name.length() == 0) {
                        Config.paired_device_name = Config.paired_device_address;
                    }

                    bluetoothMessagingService.connect(device, true);
                }
                else {
                    bluetoothMessagingService.start();
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

        if(wl != null) {
            wl.release();
        }

        if (bluetoothMessagingService != null) {
            Log.e("BtMsgSvr", "bluetoothMessagingService.stop()");
            bluetoothMessagingService.stop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (actionBar != null) {
            actionBar.setTitle("shadowbotz - " + Config.current_bluetooth_state);
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click back again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
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
            // bluetoothMessagingService.stop();

            // Intent serverIntent = new Intent(this, DeviceListActivity.class);
            // startActivityForResult(serverIntent, Config.REQUEST_CONNECT_DEVICE_SECURE);

            SocketMgr.getInstance().openConnection();
            return true;
        } else if (id == R.id.discoverable) {
            // Ensure this device is discoverable by others
            BluetoothController.turnOnDiscoverable(this);
            return true;
        } else if (id == R.id.persistent_storage) {
            // Open persistent string dialog for user input
            PersistentController.f1AndF2Dialog(this);
            return true;
        } else if (id == R.id.action_bluetooth_log) {
            Intent intent = new Intent(getApplicationContext(), BluetoothLogActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_toggle_competitive_mode) {

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

                    Log.e("BtMsgSvrState", "Connected");
                }
                else if (msg.arg1 == BluetoothMessagingService.STATE_CONNECTING) {
                    Config.current_bluetooth_state = getString(R.string.title_bluetooth_connecting);

                    Log.e("BtMsgSvrState", "Connecting");
                }
                else if (msg.arg1 == BluetoothMessagingService.STATE_LISTEN) {
                    Config.current_bluetooth_state = getString(R.string.title_bluetooth_listening);

                    Log.e("BtMsgSvrState", "Listening");
                }
                else if (msg.arg1 == BluetoothMessagingService.STATE_NONE) {
                    Config.current_bluetooth_state = getString(R.string.title_bluetooth_disconnected);

                    Log.e("BtMsgSvrState", "Disconnected");
                }

                if (actionBar != null) {
                    actionBar.setTitle("SHADOWBOTZ - " + Config.current_bluetooth_state);
                }

            } else if (msg.what == Config.MESSAGE_WRITE) {
                // Send a message to connected bluetooth device
                byte[] writeBuf = (byte[]) msg.obj;
                String writeMessage = new String(writeBuf);

                // Save message in realm
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                BluetoothMessage bluetoothMessage = realm.createObject(BluetoothMessage.class
                        , UUID.randomUUID().toString());
                bluetoothMessage.setDeviceName(Config.my_bluetooth_device_name);
                bluetoothMessage.setDeviceAddress(Config.my_bluetooth_device_address);
                bluetoothMessage.setMessage(writeMessage);
                bluetoothMessage.setDatetime(Calendar.getInstance().getTime());
                realm.commitTransaction();
                realm.close();

                Config.sent_message = writeMessage;

                MainActivity.bluetoothSubject.postMessage(bluetoothMessage);

                writeToTextFile(writeMessage);
            } else if (msg.what == Config.MESSAGE_READ) {
                // Receive a message to connected bluetooth device
                byte[] readBuf = (byte[]) msg.obj;
                String readMessage = new String(readBuf, 0, msg.arg1);

                // Save message in realm
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                BluetoothMessage bluetoothMessage = realm.createObject(BluetoothMessage.class
                        , UUID.randomUUID().toString());
                bluetoothMessage.setDeviceName(Config.paired_device_name);
                bluetoothMessage.setDeviceAddress(Config.paired_device_address);
                bluetoothMessage.setMessage(readMessage);
                bluetoothMessage.setDatetime(Calendar.getInstance().getTime());
                realm.commitTransaction();
                realm.close();

                Config.received_message = readMessage;

                MainActivity.bluetoothSubject.postMessage(bluetoothMessage);

                writeToTextFile(readMessage);
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

    private void writeToTextFile(String content) {
        FileOutputStream outputStream;

        try {
            File file = new File(Environment.getExternalStorageDirectory(), "/shadowbotz_log.txt");

            if(!file.exists()) {
                outputStream = new FileOutputStream(file, true);
                outputStream.write(content.getBytes());
                outputStream.close();
            }
            else {
                outputStream = new FileOutputStream(file, true);
                outputStream.write(("\n" + content).getBytes());
                outputStream.close();
            }
        } catch (Exception e) {
            Log.e("Algo", e.getMessage());
        }
    }
}
