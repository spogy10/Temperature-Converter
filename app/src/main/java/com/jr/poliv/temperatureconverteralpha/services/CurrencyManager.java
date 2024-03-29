package com.jr.poliv.temperatureconverteralpha.services;

import android.content.SharedPreferences;

import com.jr.poliv.temperatureconverteralpha.Currency;
import com.jr.poliv.temperatureconverteralpha.TemperatureConverter;

import java.util.concurrent.ExecutorService;

public class CurrencyManager {

    private final CurrencyService currencyService;
    private double exchangeRate = 0;
    private String selectedCurrency1, selectedCurrency2;
    private UpdateExchangeRateResult exchangeRateResult;



    public CurrencyManager(UpdateExchangeRateResult exchangeRateResult, SharedPreferences file){
        this.exchangeRateResult = exchangeRateResult;
        currencyService = new MyCurrencyService(file);
        selectedCurrency1 = getFirstCurrencySelectionFromFile();
        selectedCurrency2 = getSecondCurrencySelectionFromFile();
        calculateExchangeRate();
    }

    public String[] getCurrencyList(){
        return currencyService.getCurrencyList();
    }

    public String getSelectedCurrency1(){
        return selectedCurrency1;
    }

    public void setSelectedCurrency1(String currency){
        selectedCurrency1 = currency;
        calculateExchangeRate();
    }

    public String getSelectedCurrency2(){
        return selectedCurrency2;
    }

    public void setSelectedCurrency2(String currency){
        selectedCurrency2 = currency;
        calculateExchangeRate();
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

    public void updateCurrencyExchangeRates(){
        ExecutorService executor = TemperatureConverter.getExecutorService();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                boolean response = currencyService.updateCurrencyExchangeRates();
                updateCurrencyExchangeRatesResult(response);
            }
        });
    }

    public boolean shouldShowUpdateMessageBasedOnLastUpdate(){
        return currencyService.checkDate();
    }







    private void updateCurrencyExchangeRatesResult(boolean result){
        if(result){
            calculateExchangeRate();
            exchangeRateResult.onSuccessfulUpdate();
            return;
        }

        exchangeRateResult.onUpdateFailure();
    }

    private void calculateExchangeRate(){
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
