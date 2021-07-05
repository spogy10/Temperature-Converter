package com.jr.poliv.temperatureconverteralpha;

import android.content.Context;
import android.os.Bundle;

import androidx.core.app.NavUtils;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CelsiusToFahrenheit extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_celsius_to__fahrenheit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button button = (Button) findViewById(R.id.button);
        assert button != null;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                display(C_to_F(acceptvar()), '°', 'F');
            }
        });



    }
    double F_to_C(double var)
    {
        double result;
        result = (var-32) / 1.8;
        return result;
    }
    double C_to_F(double var)
    {
        double result;
        result = (var * 1.8) + 32;
        return result;
    }
    double K_to_C(double var)
    {
        double result;
        result = var - 273.15;
        return result;
    }
    double C_to_K(double var)
    {
        double result;
        result = var + 273.15;
        return result;
    }
    double F_to_K(double var)
    {
        double result;
        result = ((var - 32) / 1.8) + 273.15;
        return result;
    }
    double K_to_F(double var)
    {
        double result;
        result = ((var - 273.15) * 1.8) + 32;
        return result;
    }

    void display(double result, char degree, char end_char){
        TextView ans = (TextView) findViewById(R.id.textView);
        ans.setText(Double.toString(result)+" "+degree+end_char);
    }

    public double acceptvar(){
        EditText editText = (EditText) findViewById(R.id.editText);
        return Double.parseDouble(editText.getText().toString());
    }


}
