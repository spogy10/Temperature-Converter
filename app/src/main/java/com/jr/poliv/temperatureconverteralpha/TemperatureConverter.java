package com.jr.poliv.temperatureconverteralpha;

import android.app.Application;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TemperatureConverter extends Application {

    private static TemperatureConverter instance = null;
    private static ExecutorService executorService = Executors.newFixedThreadPool(1);

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

    public static ExecutorService getExecutorService(){
        return executorService;
    }
}
