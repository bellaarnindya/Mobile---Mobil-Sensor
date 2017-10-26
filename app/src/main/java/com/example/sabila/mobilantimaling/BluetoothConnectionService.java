package com.example.sabila.mobilantimaling;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.RunnableFuture;


/**
 * Created by Sabila on 10/25/2017.
 */

public class BluetoothConnectionService extends Service {
    private boolean connectSuccess = true;
    BluetoothAdapter myBluetooth = null;
    public static BluetoothSocket btSocket = null;
    public static boolean isBtConnected = false;
    Thread connectBluetooth;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    String address = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        address = intent.getStringExtra("THE ADDRESS");
        connectBluetooth = new Thread(new Runnable() {
            public void run() {
                    try {
                        if (btSocket == null || !isBtConnected) {
                            myBluetooth = BluetoothAdapter.getDefaultAdapter();
                            BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);
                            btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                            btSocket.connect();
                        }
                    }
                    catch (IOException e) {
                        if(btSocket != null) {
                            try {
                                btSocket.close();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                            btSocket = null;
                        }
                        connectSuccess = false;
                    }
                    if (!connectSuccess) {
                        new Handler(Looper.getMainLooper()).post(
                                new Runnable() {
                                    public void run() {
                                        // yourContext is Activity or Application context
                                        Toast.makeText(getApplicationContext(), "Connection Failed.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                        );
                    }
                    else {
                        isBtConnected = true;
                        new Handler(Looper.getMainLooper()).post(
                                new Runnable() {
                                    public void run() {
                                        // yourContext is Activity or Application context
                                        Toast.makeText(getApplicationContext(), "Connected.", Toast.LENGTH_SHORT).show();
                                        MainActivity.updateConnectedDeviceName();

                                    }
                                }
                        );

                        Intent intentReceiver = new Intent(BluetoothConnectionService.this, BluetoothReceiverService.class);
                        startService(intentReceiver);
                    }
                }

        });
        connectBluetooth.start();
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        disconnect();
        super.onDestroy();
    }

    private void disconnect() {
        if (btSocket!=null) {
            try {
                btSocket.close();
            }
            catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Error.", Toast.LENGTH_LONG).show();
            }

        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public BluetoothConnectionService() {
    }
}
