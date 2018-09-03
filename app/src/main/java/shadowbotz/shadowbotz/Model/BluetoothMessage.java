package shadowbotz.shadowbotz.Model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class BluetoothMessage extends RealmObject {

    @PrimaryKey
    private long id;
    private String deviceAddress;
    private String deviceName;
    private String message;
    private long datetime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getDatetime() {
        return datetime;
    }

    public void setDatetime(long datetime) {
        this.datetime = datetime;
    }
}