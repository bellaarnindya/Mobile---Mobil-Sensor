package com.example.sabila.mobilantimaling;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Sabila on 10/21/2017.
 */

public class BtAdapter extends ArrayAdapter<String> {
    public BtAdapter(@NonNull Context context, @NonNull List objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String bluetoothDevices = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_main, parent, false);
        }
        TextView btName = (TextView) convertView.findViewById(R.id.btList);

        btName.setText(bluetoothDevices);

        return convertView;
    }
}
