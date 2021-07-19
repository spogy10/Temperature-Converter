package com.jr.poliv.temperatureconverteralpha.services;

import android.content.SharedPreferences;

import com.jr.poliv.temperatureconverteralpha.Currency;
import com.jr.poliv.temperatureconverteralpha.TemperatureConverter;

public class CurrencyManager {

    private final CurrencyService currencyService;
    private Currency[] currencies = null;
    private double exchangeRate = 0;
    private String selectedCurrency1, selectedCurrency2;



    public CurrencyManager(SharedPreferences file){
        currencyService = new MyCurrencyService(file);
        selectedCurrency1 = TemperatureConverter.getInstance().getString(R.string.default_currency_1);
        selectedCurrency2 = TemperatureConverter.getInstance().getString(R.string.default_currency_2);
        updateExchangeRate();
    }

    public String[] getCurrencyList(){
        return currencyService.getCurrencyList();
    }

    public String getSelectedCurrency1(){
        return selectedCurrency1;
    }

    public void setSelectedCurrency1(String currency){
        selectedCurrency1 = currency;
        updateExchangeRate();
    }

    public String getSelectedCurrency2(){
        return selectedCurrency2;
    }

    public void setSelectedCurrency2(String currency){
        selectedCurrency2 = currency;
        updateExchangeRate();
    }

    public String getFirstCurrencySelectionFromFile(){
        return currencyService.getFirstCurrencySelectionFromFile();
    }

    public String getSecondCurrencySelectionFromFile(){
        return currencyService.getSecondCurrencySelectionFromFile();
    }

    public boolean isExchangeRateSet(){
        return exchangeRate == 0;
    }
    
    public double convertCurrency1ToCurrency2(double value){
        return currencyService.calculateCurrency(value, exchangeRate, true);
    }

    public double convertCurrency2ToCurrency1(double value){
        return currencyService.calculateCurrency(value, exchangeRate, false);
    }

    public boolean doesCurrencyListContainBaseCurrencies(){
        return currencyService.doesCurrencyListContainBaseCurrencies();
    }

    public boolean saveCurrencySelection(){
        return currencyService.saveCurrencySelection(selectedCurrency1, selectedCurrency2);
    }

    public String displayExchangeRate(){
        return selectedCurrency1 +"$1 = "+ selectedCurrency2 +"$ " + exchangeRate;
    }








    private void updateExchangeRate(){
        exchangeRate = calculateExchangeRate(selectedCurrency1, selectedCurrency2);
    }

    private double calculateExchangeRate(String currency1, String currency2){
        Currency from = getCurrencyObject(currency1);
        Currency to = getCurrencyObject(currency2);
        return currencyService.calculateExchangeRate(from, to);
    }

    private Currency getCurrencyObject(String currency){

        Currency dollar = new Currency();

        if (currencyService.doesCurrencyExist(currency)){
            dollar.setCurrency(currency);
            dollar.setRate(currencyService.getCurrencyRate(currency));
        }
        return dollar;
    }




}
