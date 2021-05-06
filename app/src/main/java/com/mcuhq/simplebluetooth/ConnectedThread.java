package com.mcuhq.simplebluetooth;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class ConnectedThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private final Handler mHandler;

    public ConnectedThread(BluetoothSocket socket, Handler handler) {
        mmSocket = socket;
        mHandler = handler;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    @Override
    public void run() {
        int buffsize=2048;
        byte[] buffer = new byte[buffsize];  // buffer store for the stream
        int bytes; // bytes returned from read()
        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
                bytes = mmInStream.available();
                SystemClock.sleep(10); //pause and wait for rest of data. Adjust this depending on your sending speed.
                if(bytes != 0)
                {
                    Arrays.fill(buffer, (byte)0);
                    byte ch;
                    int retry=0;
                    while(true)
                    {
                        ch=(byte)mmInStream.read();
                        //Log.d("whileTrue", Character.toString((char)ch));
                        if(ch=='S')
                            break;
                    }

                    for (int i = 0; i < buffsize; i++) {
                        ch = (byte) mmInStream.read();
                        while (ch == -1) {
                            SystemClock.sleep(1);
                            ch = (byte) mmInStream.read();
                        }
                        if (ch == 'E') {
                            break;
                        } else {
                            buffer[i] = ch;
                            bytes = i;
                        }
                    }
                    mHandler.obtainMessage(MainActivity.MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget(); // Send the obtained bytes to the UI activity
                }

            } catch (IOException e) {
                e.printStackTrace();

                break;
            }
        }
    }

    /* Call this from the main activity to send data to the remote device */
    public void write(String input) {
        byte[] bytes = input.getBytes();           //converts entered String into bytes
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) { }
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}