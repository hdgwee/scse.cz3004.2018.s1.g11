package shadowbotz.shadowbotz.BluetoothObserverSubject;

import java.util.ArrayList;
import java.util.List;

public class BluetoothSubject implements Subject {

    private List<Observer> observers;
    private String message;
    private boolean changed;
    private final Object MUTEX= new Object();

    public BluetoothSubject(){
        this.observers=new ArrayList<>();
    }

    @Override
    public void register(Observer obj) {
        if(obj == null) throw new NullPointerException("Null Observer");
        synchronized (MUTEX) {
            if(!observers.contains(obj)) observers.add(obj);
        }
    }

    @Override
    public void unregister(Observer obj) {
        synchronized (MUTEX) {
            observers.remove(obj);
        }
    }

    @Override
    public void notifyObservers() {
        List<Observer> observersLocal = null;
        //synchronization is used to make sure any observer registered after message is received is not notified
        synchronized (MUTEX) {
            if (!changed)
                return;
            observersLocal = new ArrayList<>(this.observers);
            this.changed=false;
        }
        for (Observer obj : observersLocal) {
            obj.update();
        }

    }

    @Override
    public Object getUpdate(Observer obj) {
        return this.message;
    }

    // Method to post message to the topic
    public void postMessage(String msg, String type){
        this.message=msg;
        this.changed=true;
        notifyObservers();
    }

}
