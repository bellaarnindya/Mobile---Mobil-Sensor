package com.example.sabila.mobilantimaling;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import static android.R.id.list;

public class MainActivity extends AppCompatActivity {

    private Button btnPaired;
    private ListView deviceList;
    private BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> pairedDevices;
    private String EXTRA_ADDRESS = "THE ADDRESS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPaired = (Button)findViewById(R.id.buttonPaired);
        deviceList = (ListView)findViewById(R.id.deviceList);

        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        if (myBluetooth==null) {
            Toast.makeText(this, "Bluetooth device not available", Toast.LENGTH_LONG).show();
            finish();
        }
        else{
            if (myBluetooth.isEnabled()) {}
            else{
                Intent turnBTOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnBTOn,1);
            }

        }
    }


    public void showPairedDevicies(View view) {
        pairedDevicesList();
    }

    private void pairedDevicesList(){
        pairedDevices = myBluetooth.getBondedDevices();
        ArrayList<String> list = new ArrayList();

        if (pairedDevices.size()>0) {
            for (BluetoothDevice bt : pairedDevices) {
                list.add(bt.getName() + "\n" + bt.getAddress());
            }
        }
        else{
            Toast.makeText(this, "No paired bluetooth devices found", Toast.LENGTH_LONG).show();
        }
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_bluetooth, list);
        deviceList.setAdapter(adapter);
        deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick (AdapterView av, View v, int arg2, long arg3) {
                String info = ((TextView) v).getText().toString();
                String address = info.substring(info.length()-17);

                Intent intent = new Intent(MainActivity.this, SensorControl.class);
                intent.putExtra(EXTRA_ADDRESS, address);
                startActivity(intent);
            }
        });
    }
}
