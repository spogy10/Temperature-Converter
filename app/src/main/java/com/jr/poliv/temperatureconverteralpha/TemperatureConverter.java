package com.jr.poliv.temperatureconverteralpha;

import android.app.Application;

public class TemperatureConverter extends Application {

    private static TemperatureConverter instance = null;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static TemperatureConverter getInstance(){
        if(instance == null){
            instance = new TemperatureConverter();
        }

        return instance;
    }
}
