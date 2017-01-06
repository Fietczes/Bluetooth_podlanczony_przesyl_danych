package com.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView temp1TextView;
    private TextView temp2TextView;
    private TextView wilg1TextView;
    private TextView wilg2TextView;
    private TextView feedbackTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner configSpinner = (Spinner) findViewById(R.id.config_spinner);
        Spinner bluetoothSpinner = (Spinner) findViewById(R.id.bluetooth_spinner);

        temp1TextView = (TextView) findViewById(R.id.temp1_text_view);
        temp2TextView = (TextView) findViewById(R.id.temp2_text_view);
        wilg1TextView = (TextView) findViewById(R.id.wilg1_text_view);
        wilg2TextView = (TextView) findViewById(R.id.wilg2_text_view);
        feedbackTextView = (TextView) findViewById(R.id.feedback_text_view);


        Button wyslijButton = (Button) findViewById(R.id.wyslij_button);
        // wysylanie danych
        wyslijButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        Button polaczButton = (Button) findViewById(R.id.polacz_button);
        // laczenie ze sterownikiem
        polaczButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // inicjalizacja konfiguracji (kury, gesi..)
        ArrayAdapter<CharSequence> configAdapter = ArrayAdapter.createFromResource(this,
                R.array.config_array, android.R.layout.simple_spinner_item);
        configAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        configSpinner.setAdapter(configAdapter);
        configSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String currentItem = adapterView.getItemAtPosition(position).toString();
                if (currentItem.equals("Kury")) {
                    ustawParametry(1.0, 1.0, 1.0, 1.0);
                } else if (currentItem.equals("Gęsi")) {
                    ustawParametry(2.0, 2.0, 2.0, 2.0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // inicjalizacja listy sparowanych urzadzen bluetooth
        List<String> bluetoothList = new ArrayList<>();
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Iterable<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        for (BluetoothDevice bluetoothDevice : pairedDevices) {
            bluetoothList.add(bluetoothDevice.getName());
        }

        ArrayAdapter<String> bluetoothListAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, bluetoothList);
        bluetoothListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bluetoothSpinner.setAdapter(bluetoothListAdapter);
    }

    public void ustawParametry(double temp1, double temp2, double wilg1, double wilg2) {
        temp1TextView.setText("Temperatura1[C] = " + String.valueOf(temp1));
        temp2TextView.setText("Temperatura2[C] = " + String.valueOf(temp2));
        wilg1TextView.setText("Wilgotność1[%] = " + String.valueOf(wilg1));
        wilg2TextView.setText("Wilgotność2[%] = " + String.valueOf(wilg2));
    }
}
