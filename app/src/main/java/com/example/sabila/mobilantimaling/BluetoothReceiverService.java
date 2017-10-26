package com.example.sabila.mobilantimaling;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Sabila on 10/25/2017.
 */

public class BluetoothReceiverService extends Service {

    private InputStream mmInStream;
    private OutputStream mmOutStream;
    private String writeMessage;
    Thread dataReceiverThread;

    public BluetoothReceiverService() {

    }

    @Override
    public int onStartCommand(Intent intent,  int flags, int startId) {


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
                Log.d("HEHE", "masuk service");
                while (true) {
                    if (!BluetoothConnectionService.isBtConnected) continue;
                    try {

                        mmInStream = BluetoothConnectionService.btSocket.getInputStream();
                        mmOutStream = BluetoothConnectionService.btSocket.getOutputStream();
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
                            if (msg.contains("N")){
                                Log.d("ON", "MALING");
                                Intent resultIntent = new Intent(BluetoothReceiverService.this, ResultActivity.class);
                                resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(resultIntent);

                                //Toast.makeText(BluetoothReceiverService.this, "MALING", Toast.LENGTH_SHORT).show();
                            }
                            //Log.d("NASI GORENG IDOLA", writeMessage);
                        } catch (IOException e) {

                        }
                    }
                }

            }
        });
        dataReceiverThread.start();
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
