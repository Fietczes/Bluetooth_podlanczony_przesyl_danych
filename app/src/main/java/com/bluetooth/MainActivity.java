package com.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

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
        final Spinner bluetoothSpinner = (Spinner) findViewById(R.id.bluetooth_spinner);

        temp1TextView = (TextView) findViewById(R.id.temp1_text_view);
        temp2TextView = (TextView) findViewById(R.id.temp2_text_view);
        wilg1TextView = (TextView) findViewById(R.id.wilg1_text_view);
        wilg2TextView = (TextView) findViewById(R.id.wilg2_text_view);
        feedbackTextView = (TextView) findViewById(R.id.feedback_text_view);


        Button wyslijButton = (Button) findViewById(R.id.wyslij_button);
        // wysylanie danych
        wyslijButton.setOnClickListener(new View.OnClickListener() {
            BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

            @Override
            public void onClick(View view) {
                startRepeatingTask();
                {
                    btAdapter.startDiscovery();
                    BluetoothDevice device = null;
                    try {
                        btSocket.close();
                    } catch (IOException e2) {
                        errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
                    }

                    String sterownik = bluetoothSpinner.getSelectedItem().toString();

                    try {
                        BluetoothAdapter btInterface;//Tworzymy nasze urzadzenie bluetooth (tzn obiekt posidajacy wszystkie parametry takiego urzadzenia)
                        List<String> s = new ArrayList<String>();//Tworzymy nową listę Stringów


                        btInterface = BluetoothAdapter.getDefaultAdapter();
                        Iterable<BluetoothDevice> pairedDevices = btInterface.getBondedDevices();

                        Iterator<BluetoothDevice> it = pairedDevices.iterator();

                        for (BluetoothDevice bt : pairedDevices) {//for dal wszystkich urzadzen
                            if (bt.getName().equalsIgnoreCase(sterownik))//dla wszystkich
                            {
                                btAdapter = BluetoothAdapter.getDefaultAdapter();
                                address = bt.getAddress();//zeby lanczylo z automatu
                                device = bt;
                            }

                        }

                    } catch (Exception e) {
                        Log.e(TAG, "bład w szukaniu urzadzen" + e.getMessage());
                    }

                    try {
                        btSocket = createBluetoothSocket(device);
                    } catch (IOException e) {
                        errorExit("Fatal Error", "In onResume() and socket create failed: nie utworylo socketa" + e.getMessage() + ".");
                    }

                    btAdapter.cancelDiscovery();

                    try {
                        btSocket.connect();
                    } catch (IOException e) {
                        try {
                            btSocket.close();
                        } catch (IOException e2) {
                            errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
                        }
                    }

                    mConnectedThread = new ConnectedThread(btSocket);//utworzenie objektu klasy rozszerzającego wątek główny
                    mConnectedThread.start();//uruchomienie tego wątku (chyba jest to metoda run w kalsie którą przedstawia ten wontek)

                }
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

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if (Build.VERSION.SDK_INT >= 10) {
            try {
                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[]{UUID.class});
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return device.createRfcommSocketToServiceRecord(MY_UUID);
    }
}
