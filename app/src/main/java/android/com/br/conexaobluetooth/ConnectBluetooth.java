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

package android.com.br.conexaobluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;


/**
  * This class does all the work for creating and managing Bluetooth
  * connections to other devices. It has a segment that attend the
  * input connections, a wire to connect to a device, and a
  * Thread to perform data transmissions when connected.
 */
public class ConnectBluetooth {
    // Debugging
    private static final String TAG = "ConnectBluetooth";

    private final BluetoothAdapter mAdapter;
    private Handler mHandler;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int mState;

    public static final int STATE_NONE = 0;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;


    public ConnectBluetooth() {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
    }

    public void setmHandler(Handler handler) {
        mHandler = handler;
    }

    public boolean isOnAdapter(){
        return (mAdapter == null) ? false : true;
    }

    public Set<BluetoothDevice> getDispositivos(){
        Set<BluetoothDevice> pairedDevices = mAdapter.getBondedDevices();
        return pairedDevices;
    }

    public boolean isOnBluetooth(){
        if (mAdapter != null) {
            if (!mAdapter.isEnabled()) return true;
        }
        return false;
    }

    private synchronized void setState(int state) {
        Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;

        mHandler.obtainMessage(Constants.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    public synchronized int getState() {
        return mState;
    }

    public synchronized void connect(BluetoothDevice device) {
        Log.d(TAG, "connect to: " + device);

        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    /**
     * Initialize ConnectedThread to begin to manager a Bluetooth connection
     *
     * @param socket The BluetoothSocket where the connection was made
     * @param device The BluetoothDevice that has been turned on
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice
            device) {
        Log.d(TAG, "connected, Socket Type:");

        // Cancel the thread that completed the connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any threads running at connection time
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        // Send the connected device name back to UI Activity
        Message msg = mHandler.obtainMessage(Constants.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DEVICE_NAME, device.getName());
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        setState(STATE_CONNECTED);
    }

    /**
     * Send the data to ConnectedThread asynchronous
     */
    public void write(CharSequence chars) {
        ConnectedThread r;

        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }

        r.write((chars.toString() + "\r\n").getBytes());
    }

    /**
     * Connection failure. notify UI Activity.
     */
    private void connectionFailed() {
        Message msg = mHandler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, "Impossivel conectar ao dispositivo");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    /**
     * This thread runs when trying to make an outbound connection to a device.
     */

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;

            try {
                Method m = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                tmp = (BluetoothSocket) m.invoke(device, 1); //Default connection of ELM327 is 1
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            mmSocket = tmp;
        }

        public void run() {
            //Always cancel discovery because it will slow down a connection
            mAdapter.cancelDiscovery();

            try {
                Log.e(TAG, "iniciando conexão");
                mmSocket.connect();
            } catch (IOException e) {
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() " +
                            " socket during connection failure", e2);
                }
                connectionFailed();
                return;
            }

            synchronized (ConnectBluetooth.this) {
                mConnectThread = null;
            }

            connected(mmSocket, mmDevice);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect " + " socket failed", e);
            }
        }
    }

    /**
     * This thread runs during a connection to a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get input and output streams BluetoothSocket
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;

            // Manter ouvindo o InputStream enquanto conectado
            while (true) {
                try {

                    bytes = mmInStream.read(buffer);

                    mHandler.obtainMessage(Constants.MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget();
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    break;
                }
            }
        }

        /**
         * Write to OutStream connected.
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);

                // Share the message sent back to the UI Activity
                mHandler.obtainMessage(Constants.MESSAGE_WRITE, -1, -1, buffer)
                        .sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
}
