package shadowbotz.shadowbotz;

public class Config {

    //TODO: Add static strings here
    //Keys for Sharedpreferences
    public static final String SHARED_PREF_NAME = "ShadowBotz";//This would be the name of our shared preferences

    public static final String F1_BUTTON = "f1";
    public static final String F2_BUTTON = "f2";
    //End of Sharedpreferences

    // Message types sent from the BluetoothMessagingService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothMessagingService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    public static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    public static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    public static final int REQUEST_ENABLE_BT = 3;

    public static String current_bluetooth_state = "";
    public static String paired_device_name = "";

    public static String received_message = "";
    public static String sent_message = "";
}
