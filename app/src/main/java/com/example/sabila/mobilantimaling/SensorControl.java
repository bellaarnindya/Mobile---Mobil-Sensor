package com.example.sabila.mobilantimaling;

import android.app.Notification;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static android.R.id.message;
import static android.view.View.VISIBLE;

/**
 * Created by Sabila on 10/21/2017.
 */

public class SensorControl extends AppCompatActivity {
    String address = null;
    TextView warning;
    private ProgressBar progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    private InputStream mmInStream;
    private OutputStream mmOutStream;
    private String writeMessage;
    Thread dataReceiverThread;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_control);

        //warning = (TextView) findViewById(R.id.controlText);
        Intent newInt = getIntent();
        address = newInt.getStringExtra("THE ADDRESS");
        new ConnectBT().execute();
        receiveData();

    }


    private void receiveData () {

        dataReceiverThread = new Thread (new Runnable(){
            byte[] buffer = new byte[1024];
            int begin = 0;
            int bytes = 0;
            Handler mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    byte[] writeBuf = (byte[]) msg.obj;
                    int begin = (int)msg.arg1;
                    int end = (int)msg.arg2;

                    switch(msg.what) {
                        case 1:
                            writeMessage = new String(writeBuf);
                            writeMessage = writeMessage.substring(begin, end);
                            break;
                    }
                }
            };


            public void run() {
                while (true) {
                    if (!isBtConnected) continue;
                    try {
                        mmInStream = btSocket.getInputStream();
                        mmOutStream = btSocket.getOutputStream();
                    } catch (IOException e) {continue; }
                    if (mmInStream != null) {
                        try {
                            bytes += mmInStream.read(buffer, bytes, buffer.length - bytes);
                            String msg = "";
//                            Log.d("PAGER", "#".getBytes()[0]+"");
                            for (int i = begin; i < bytes; i++) {

                                if (buffer[i] == 10) {
                                    mHandler.obtainMessage(1, begin, i,buffer).sendToTarget();
                                    begin = i + 1;
                                    if (i == bytes-1) {
                                        bytes = 0;
                                        begin = 0;
                                    }
                                    break;
                                }
                            }

                            msg = new String (buffer);
                            for(int i=0; i<msg.length(); i++) {
                                if (msg.charAt(i)=='#') {
                                    msg = msg.substring(0,i);
                                    buffer = new byte[1024];
                                    break;
                                }
                            }
                            Log.d("INPUT DATA", msg);
                            if (msg.equals("ON")){


                            }
                            //Log.d("NASI GORENG IDOLA", writeMessage);
                        } catch (IOException e) {

                        }
                    }
                }

            }
        });
        dataReceiverThread.start();


    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean connectSuccess = true;

        @Override
        protected Void doInBackground(Void... voids) {
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

            return null;
        }

        @Override
        protected void onPreExecute() {
            progress = new ProgressBar(getBaseContext(), null, android.R.attr.progressBarStyleHorizontal);
            progress.setVisibility(VISIBLE);
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (!connectSuccess) {
                Toast.makeText(getApplicationContext(), "Connection Failed. Is it a SPP Bluetooth? Try again.", Toast.LENGTH_LONG).show();
                finish();
            }
            else {
                Toast.makeText(getApplicationContext(), "Connected.", Toast.LENGTH_LONG).show();
                isBtConnected = true;
            }
            progress.setVisibility(View.INVISIBLE);

        }


        private void disconnect() {
            if (btSocket!=null) {
                try {
                    btSocket.close();
                }
                catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "Error.", Toast.LENGTH_LONG).show();
                }
                finish();
            }
        }

    }

}
