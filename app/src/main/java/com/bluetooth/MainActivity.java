package com.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final int RECEIVE_MESSAGE = 1;

    private TextView temp1TextView;
    private TextView temp2TextView;
    private TextView wilg1TextView;
    private TextView wilg2TextView;
    private TextView feedbackTextView;
    private BluetoothSocket btSocket;

    private List<BluetoothDevice> bluetoothDeviceList = new ArrayList<>();

    Handler h;
    int i;
    float t1;
    private StringBuilder sb = new StringBuilder();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        temp1TextView = (TextView) findViewById(R.id.temp1_text_view);
        temp2TextView = (TextView) findViewById(R.id.temp2_text_view);
        wilg1TextView = (TextView) findViewById(R.id.wilg1_text_view);
        wilg2TextView = (TextView) findViewById(R.id.wilg2_text_view);
        feedbackTextView = (TextView) findViewById(R.id.feedback_text_view);

        h = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case RECEIVE_MESSAGE:
                        byte[] readBuf = (byte[]) msg.obj;
                        String strIncom = new String(readBuf, 0, msg.arg1);
                        sb.append(strIncom);
                        int endOfLineIndex = sb.indexOf("\r\n");
                        if (endOfLineIndex > 0) {
                            String sbprint = sb.substring(0, endOfLineIndex);
                            if (sb.indexOf("#") != (-1)) {
                                i = sb.indexOf("#");
                                // and clear
                                if (sb.charAt(i) == '#') {

                                    String zmienna = new String();
                                    i++;
                                    while (sb.charAt(i) != '~') {
                                        zmienna += sb.charAt(i++);
                                    }
                                    i++;
                                    String cyfra = new String();
                                    while (sb.charAt(i) != '#') {
                                        cyfra += sb.charAt(i++);
                                    }
                                    float f = Float.parseFloat(cyfra);
                                    if (zmienna.equals("t1") == true) {
                                        t1 = f;
                                    }

                                    sb.delete(0, i - 1);

                                }
                            }
                            sb.delete(0, sb.length());
                            feedbackTextView.setText("Data from Arduino: " + sbprint);
                        }
                        break;

                }
            }

            ;
        };

        Spinner configSpinner = (Spinner) findViewById(R.id.config_spinner);
        final Spinner bluetoothSpinner = (Spinner) findViewById(R.id.bluetooth_spinner);


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
            BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

            @Override
            public void onClick(View view) {
                // startRepeatingTask();
                {
                    try {
                        btSocket = createBluetoothSocket(bluetoothDeviceList.get(bluetoothSpinner.getSelectedItemPosition()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        btSocket.connect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ConnectedThread mConnectedThread = new ConnectedThread(btSocket);
                    mConnectedThread.start();

                }
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
            bluetoothDeviceList.add(bluetoothDevice);
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

    class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    h.obtainMessage(RECEIVE_MESSAGE, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
    }
}
