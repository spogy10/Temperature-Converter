package com.jr.poliv.temperatureconverteralpha;

import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity_for_Temp extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    EditText celsius, fahrenheit, kelvin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_for_temp);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        celsius = (EditText) findViewById(R.id.Celsius);
        fahrenheit = (EditText) findViewById(R.id.Fahrenheit);
        kelvin = (EditText) findViewById(R.id.Kelvin);
        

        focusMethod(celsius, 3, "°C");

        celsius.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(celsius.isFocused())
                    if (!( (celsius.getText().toString().equals("")) || (celsius.getText().toString().equals(".")) || (celsius.getText().toString().equals("+")) || (celsius.getText().toString().equals("-")) ))
                    {
                        exponentChecker(celsius);
                        setFahrenheit("celsius");
                        setKelvin("celsius");
                    }

            }
        });


        focusMethod(fahrenheit, 3, "°F");

        fahrenheit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {



            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {




            }

            @Override
            public void afterTextChanged(Editable s) {

                if(fahrenheit.isFocused()) {
                    if (!((fahrenheit.getText().toString().equals("")) || (fahrenheit.getText().toString().equals(".")) || (fahrenheit.getText().toString().equals("+")) || (fahrenheit.getText().toString().equals("-")))) {
                            exponentChecker(fahrenheit);
                            setCelsius("fahrenheit");
                            setKelvin("fahrenheit");
                    }
                }

            }
        });

        focusMethod(kelvin, 2, "K");

        kelvin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(kelvin.isFocused())
                    if (!( (kelvin.getText().toString().equals("")) || (kelvin.getText().toString().equals(".")) || (kelvin.getText().toString().equals("+")) || (kelvin.getText().toString().equals("-")) ))
                    {
                        exponentChecker(kelvin);
                        setCelsius("kelvin");
                        setFahrenheit("kelvin");
                    }

            }
        });




        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    void setCelsius(String choice){

        switch (choice) {
            case "fahrenheit": celsius.setText(String.format("%s °C", Double.toString(Calc.F_to_C(Double.parseDouble(fahrenheit.getText().toString())))));
                break;

            case "kelvin": celsius.setText(String.format("%s °C", Double.toString(Calc.K_to_C(Double.parseDouble(kelvin.getText().toString())))));
                break;
        }

    }

    void setFahrenheit(String choice){

        switch (choice) {

            case "celsius": fahrenheit.setText(String.format("%s °F", Double.toString(Calc.C_to_F(Double.parseDouble(celsius.getText().toString())))));
                break;

            case "kelvin": fahrenheit.setText(String.format("%s °F", Double.toString(Calc.K_to_F(Double.parseDouble(kelvin.getText().toString())))));
                break;

        }

    }

    void setKelvin(String choice){

        switch (choice) {

            case "celsius": kelvin.setText(String.format("%s K", Double.toString(Calc.C_to_K(Double.parseDouble(celsius.getText().toString())))));
                break;

            case "fahrenheit": kelvin.setText(String.format("%s K", Double.toString(Calc.F_to_K(Double.parseDouble(fahrenheit.getText().toString())))));


        }
    }

    void focusMethod(final EditText text, final int amountOfEndCharacters, final String endString){

        text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    if (!(text.getText().toString().matches("[0-9.]*"))) {
                        int length = text.getText().length();

                        text.getText().delete(length - amountOfEndCharacters, length);
                    }
                }
                else {
                    text.setText(String.format("%s %s", text.getText().toString(), endString));
                }

            }
        });
    }

    void exponentChecker(EditText text){


        int length = text.getText().length();
        String temp = text.getText().toString();
        if( temp.substring(length - 1, length).equals("E"))
            text.getText().delete(length - 1, length);

        if( temp.substring(length - 1, length).equals("-"))
            text.getText().delete(length - 2, length);



    }


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.jr.poliv.temperatureconverteralpha/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.jr.poliv.temperatureconverteralpha/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
