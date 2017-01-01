package com.jr.poliv.temperatureconverter;

/**
 * Created by poliv on 1/1/2017.
 */

public class Currency {
    private String currency; // abbreviated name of currency eg. USD
    private double rate; // value of 1 USD in said currency

    public Currency(){
        currency = "";
        rate = 0;
    }

    public Currency(String currency, double rate){
        this.currency = currency;
        this.rate = rate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}
