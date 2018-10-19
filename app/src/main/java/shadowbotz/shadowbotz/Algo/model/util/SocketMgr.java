package shadowbotz.shadowbotz.Algo.model.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * A singleton class for using the socket
 */
public class SocketMgr {

    private static SocketMgr mInstance;
    private static Socket mSocket;
    private static PrintWriter mSocketWriter;
    private BufferedReader mSocketReader;
    // private static final int PORT = 5560;
    // private static final String ADDRESS = "localhost";
    private static final int PORT = 5182;
    private static final String ADDRESS = "192.168.11.11";

    private SocketMgr() { }

    public static SocketMgr getInstance() {
        if (mInstance == null)
            mInstance = new SocketMgr();
        return mInstance;
    }

    public void openConnection() {
        try {
            mSocket = new Socket(ADDRESS, PORT);
            //mSocket.setTcpNoDelay(true);
            mSocketWriter = new PrintWriter(mSocket.getOutputStream(), true);
            mSocketReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
            Log.i("Algo", "Socket connection successful");
        } catch (IOException e) {
            Log.e("Algo.StackTrace", e.getMessage());
            Log.i("Algo", "Socket connection failed");

        }
    }

    public void closeConnection() {
        mSocketWriter.close();
        try {
            mSocketReader.close();
            mSocket.close();
        } catch (IOException e) {
            Log.e("Algo.StackTrace", e.getMessage());
        }
        Log.i("Algo", "Socket connection closed");
    }

    public boolean isConnected() {
        return mSocket != null && mSocket.isConnected();
    }

    public void sendMessage(String dest, String msg) {
        mSocketWriter.println(dest + msg);
        Log.i("Algo", "Sent message: " + dest + msg);
    }

    public String receiveMessage(boolean sensor) {
        try {
            if (sensor)
                mSocket.setSoTimeout(0);
            else
                mSocket.setSoTimeout(0);
        } catch (SocketException e) {

        }
        try {
            String msg = mSocketReader.readLine();
            Log.i("Algo", "Received message: " + msg);
            return msg;
        } catch (SocketTimeoutException e) {
            Log.i("Algo", "Sensor reading timeout!!!");
        } catch (IOException e) {
            Log.e("Algo.StackTrace", e.getMessage());
        }
        return null;
    }

    public void clearInputBuffer() {
        String input;
        try {
            while ((input = mSocketReader.readLine()) != null) {
                Log.i("Algo", "Discarded message: " + input);
            }
        } catch (IOException e) {
            Log.e("Algo.StackTrace", e.getMessage());
        }
    }
}
