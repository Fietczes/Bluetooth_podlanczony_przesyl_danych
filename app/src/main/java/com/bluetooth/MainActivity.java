package com.bluetooth;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private TextView temp1TextView;
    private TextView temp2TextView;
    private TextView wilg1TextView;
    private TextView wilg2TextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        temp1TextView = (TextView) findViewById(R.id.temp1_text_view);
        temp2TextView = (TextView) findViewById(R.id.temp2_text_view);
        wilg1TextView = (TextView) findViewById(R.id.wilg1_text_view);
        wilg2TextView = (TextView) findViewById(R.id.wilg2_text_view);

        // inicjalizacja konfiguracji (kury, gesi..)
        ArrayAdapter<CharSequence> configAdapter = ArrayAdapter.createFromResource(this,
                R.array.config_array, android.R.layout.simple_spinner_item);
        configAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(configAdapter);
        spinner.setOnItemSelectedListener(this);
    }

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

    public void ustawParametry(double temp1, double temp2, double wilg1, double wilg2) {
        temp1TextView.setText("Temperatura1[C] = " + String.valueOf(temp1));
        temp2TextView.setText("Temperatura2[C] = " + String.valueOf(temp2));
        wilg1TextView.setText("Wilgotność1[%] = " + String.valueOf(wilg1));
        wilg2TextView.setText("Wilgotność2[%] = " + String.valueOf(wilg2));
    }
}
