package com.bluetooth;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;


import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final int RECEIVE_MESSAGE = 1;

    private TextView temp1TextView;
    private TextView temp2TextView;
    private TextView wilg1TextView;
    private TextView wilg2TextView;
    private TextView przekr_odst_TextView;
    private TextView czas_do_klucia_TextView;
    private TextView przekr_czas_TextView;
    private TextView wiatraki_odst_TextView;
    private TextView wiatraki_czas_TextView;
    private TextView feedbackTextView;
    private BluetoothSocket btSocket;
    private ConnectedThread mConnectedThread;



    private List<BluetoothDevice> bluetoothDeviceList = new ArrayList<>();

    Handler h;
    int i;
    float t1;
    private StringBuilder sb = new StringBuilder();
    private int mInterval = 3000; // 10 seconds by default, can be changed later
    private Handler mHandler;
    String rodzaj_jaj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        czas_do_klucia_TextView = (TextView) findViewById(R.id.czas_do_klucia_text_view);
        temp1TextView = (TextView) findViewById(R.id.temp1_text_view);
        temp2TextView = (TextView) findViewById(R.id.temp2_text_view);
        wilg1TextView = (TextView) findViewById(R.id.wilg1_text_view);
        wilg2TextView = (TextView) findViewById(R.id.wilg2_text_view);
        przekr_odst_TextView = (TextView) findViewById(R.id.przekr_odst_text_view);
        przekr_czas_TextView = (TextView) findViewById(R.id.przekr_czas_text_view);
        wiatraki_odst_TextView = (TextView) findViewById(R.id.wiatraki_odst_text_view);
        wiatraki_czas_TextView = (TextView) findViewById(R.id.wiatraki_czas_text_view);
        feedbackTextView = (TextView) findViewById(R.id.feedback_text_view);

        mHandler = new Handler();

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
                            //znajdziemy sam znacznik i dodamy
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
                                    feedbackTextView.setText("Data from Arduino: " + cyfra);
                                    float f = Float.parseFloat(cyfra);
                                    if (zmienna.equals("t1") == true) {
                                        t1 = f;
                                    }

                                    sb.delete(0, i - 1);
                                   // feedbackTextView.setText("Data from Arduino: " + t1);
                                }
                            }
                            //feedbackTextView.setText("Data from Arduino: " + sb);
                            sb.delete(0, sb.length());

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
                final String czas = czas_do_klucia_TextView.getText().toString()
                        .substring(czas_do_klucia_TextView.getText().toString().lastIndexOf("=") + 1);
                final String temp1 = temp1TextView.getText().toString()
                        .substring(temp1TextView.getText().toString().lastIndexOf("=") + 1);
                final String temp2 = temp2TextView.getText().toString()
                        .substring(temp2TextView.getText().toString().lastIndexOf("=") + 1);
                final String wilg1 = wilg1TextView.getText().toString()
                        .substring(temp1TextView.getText().toString().lastIndexOf("=") + 1);
                final String wilg2 = wilg2TextView.getText().toString()
                        .substring(wilg2TextView.getText().toString().lastIndexOf("=") + 1);
                final String odst_przekr = przekr_odst_TextView.getText().toString()
                        .substring(przekr_odst_TextView.getText().toString().lastIndexOf("=") + 1);
                final String czas_przekr = przekr_czas_TextView.getText().toString()
                        .substring(przekr_czas_TextView.getText().toString().lastIndexOf("=") + 1);
                final String odst_wentyl = wiatraki_odst_TextView.getText().toString()
                        .substring(wiatraki_odst_TextView.getText().toString().lastIndexOf("=") + 1);
                final String czas_wentyl = wiatraki_czas_TextView.getText().toString()
                        .substring(wiatraki_czas_TextView.getText().toString().lastIndexOf("=") + 1);

                stopRepeatingTask();
                int opoznienie = 0;
                int odstep=1500;
                new CountDownTimer(opoznienie, 1000) {
                    public void onFinish() {
                        mConnectedThread.write("at1+" + temp1 + "a");

                    }public void onTick(long millisUntilFinished) {
                        // millisUntilFinished    The amount of time until finished.
                    }
                }.start();
                opoznienie=opoznienie+odstep;
                new CountDownTimer(opoznienie, 1000) {
                    public void onFinish() {
                        mConnectedThread.write("at2+" + temp2 + "a");
                    }public void onTick(long millisUntilFinished) {
                        // millisUntilFinished    The amount of time until finished.
                    }
                }.start();
                opoznienie=opoznienie+odstep;
                new CountDownTimer(opoznienie, 1000) {
                    public void onFinish() {
                        mConnectedThread.write("ah1+" + wilg1 + "a");
                    }public void onTick(long millisUntilFinished) {
                        // millisUntilFinished    The amount of time until finished.
                    }
                }.start();
                opoznienie=opoznienie+odstep;
                new CountDownTimer(opoznienie, 1000) {
                    public void onFinish() {
                        mConnectedThread.write("ah2+" + wilg2 + "a");
                    }public void onTick(long millisUntilFinished) {
                        // millisUntilFinished    The amount of time until finished.
                    }
                }.start();
                opoznienie=opoznienie+odstep;
                new CountDownTimer(opoznienie, 1000) {
                    public void onFinish() {
                        mConnectedThread.write("ap1+" + odst_przekr + "a");
                    }public void onTick(long millisUntilFinished) {
                        // millisUntilFinished    The amount of time until finished.
                    }
                }.start();
                opoznienie=opoznienie+odstep;
                new CountDownTimer(opoznienie, 1000) {
                    public void onFinish() {
                        mConnectedThread.write("ap2+" + czas_przekr + "a");
                    }public void onTick(long millisUntilFinished) {
                        // millisUntilFinished    The amount of time until finished.
                    }
                }.start();
                opoznienie=opoznienie+odstep;
                new CountDownTimer(opoznienie, 1000) {
                    public void onFinish() {
                        mConnectedThread.write("aw1+" + odst_wentyl + "a");
                    }public void onTick(long millisUntilFinished) {
                        // millisUntilFinished    The amount of time until finished.
                    }
                }.start();
                opoznienie=opoznienie+odstep;
                new CountDownTimer(opoznienie, 1000) {
                    public void onFinish() {
                        mConnectedThread.write("aw2+" + czas_wentyl + "a");
                    }public void onTick(long millisUntilFinished) {
                        // millisUntilFinished    The amount of time until finished.
                    }
                }.start();
                opoznienie=opoznienie+odstep;
                new CountDownTimer(opoznienie, 1000) {
                    public void onFinish() {
                        mConnectedThread.write("az1+0a");
                    }public void onTick(long millisUntilFinished) {
                        // millisUntilFinished    The amount of time until finished.
                    }
                }.start();
                opoznienie=opoznienie+odstep;
                new CountDownTimer(opoznienie, 1000) {
                    public void onFinish() {
                        mConnectedThread.write("au1+" + czas + "a");
                        Toast.makeText(getApplicationContext(),"Ustawiono program dla " + rodzaj_jaj, Toast.LENGTH_SHORT).show();
                    }public void onTick(long millisUntilFinished) {
                        // millisUntilFinished    The amount of time until finished.
                    }
                }.start();

                startRepeatingTask();

            }
        });



        Button polaczButton = (Button) findViewById(R.id.polacz_button);
        // laczenie ze sterownikiem
        polaczButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

                    mConnectedThread = new ConnectedThread(btSocket);
                    mConnectedThread.start();


                    startRepeatingTask();//Start przetwarzania
                }
                   /*Runnable mStatusChecker = new Runnable() {
                        @Override
                        public void run() {
                            String string = ("r");
                            string.concat("\n");
                            mConnectedThread.write(string);
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    Executor tpe = new ThreadPoolExecutor(1, 1, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

                    tpe.execute(new Runnable() {
                        @Override
                        public void run() {
                            String string = ("r");
                            string.concat("\n");
                            mConnectedThread.write(string);
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    Thread feedbackThread = new Thread(mStatusChecker);
                    feedbackThread.start();*/




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
                 rodzaj_jaj = adapterView.getItemAtPosition(position).toString();
                if (rodzaj_jaj.equals("Kury")) {
                    ustawParametry(18, 37.7 , 37.2 , 57 , 70 , 300 , 30 , 300, 30);
                } else if (rodzaj_jaj.equals("Gęsi")) {
                    ustawParametry(20, 39.2 , 38.7 , 47 , 60 , 200 ,20 , 200, 20);
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

    //METODA ON_PAUSE
    public void onPause() {
        super.onPause();
        stopRepeatingTask();
    }
    //METODA ON_RESUME
    @Override
    public void onResume() {
        super.onResume();
        try {
            btSocket.connect();
            Toast.makeText(getApplicationContext(),"7. onResume()", Toast.LENGTH_SHORT).show();
            startRepeatingTask();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
//ELEMENT WYWOLUJACY PRZEPYTANIA

    void startRepeatingTask() {
        if(btSocket.isConnected())
        {Toast.makeText(getApplicationContext(),String.valueOf(btSocket.isConnected()), Toast.LENGTH_SHORT).show();

               mStatusChecker.run();

        }

    }

    void stopRepeatingTask() {

      //tutaj sprawdzic czy nie nie rzuca Exceptiona
            mHandler.removeCallbacks(mStatusChecker);
        Toast.makeText(getApplicationContext(),"Zatrzymalo przepytywanie", Toast.LENGTH_SHORT).show();

    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {

                String string = ("r");//Wywołujemy z tego wontku kolejny odpowiadający za wysłanie zapytania
                string.concat("\n");
                mConnectedThread.write(string); //this function can change value of mInterval.
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
    }
//KONIEC FUNKCJI DO PRZEPYRTYWANIA


   /* @Override
    public void onPause() {//gdy zapauzowany
        super.onPause();//ocywiście konstruktor domyslny

        try {//zamykamy łacze żeby nie używało za dużo
            btSocket.close();
        } catch (IOException e2) {

        }
    }

    @Override

    public void onResume() {
        super.onResume();

        try {
            btSocket.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    public void ustawParametry(int czas, double temp1, double temp2, double wilg1, double wilg2, int odst_przekr, double czas_przekr, int odst_wentyl, double czas_wentyl  ) {
        czas_do_klucia_TextView.setText("Czas inkubacji[%] =" + String.valueOf(czas));
        temp1TextView.setText("Temperatura1[C] =" + String.valueOf(temp1));
        temp2TextView.setText("Temperatura2[C] =" + String.valueOf(temp2));
        wilg1TextView.setText("Wilgotność1[%] =" + String.valueOf(wilg1));
        wilg2TextView.setText("Wilgotność2[%] =" + String.valueOf(wilg2));
        przekr_odst_TextView.setText("Odstęp obracania[min] =" + String.valueOf(odst_przekr));
        przekr_czas_TextView.setText("Czas obracania [s] =" + String.valueOf(czas_przekr));
        wiatraki_odst_TextView.setText("Odstęp wentylacji [min] =" + String.valueOf(odst_wentyl));
       wiatraki_czas_TextView.setText("Czas wentylacji [s] =" + String.valueOf(czas_wentyl));

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

        public void write(String message) {
            byte[] msgBuffer = message.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
