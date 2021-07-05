package com.jr.poliv.temperatureconverteralpha.services;

import android.content.SharedPreferences;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class MyCurrencyService implements CurrencyService {

    private final SharedPreferences file;

    public MyCurrencyService(SharedPreferences file){
        this.file = file;
    }

    @Override
    public double calculateExchangeRate(Currency from, Currency to){
        return from.getRate()/to.getRate();
    }

    @Override
    public double calculateCurrency(double value, double exchangeRate, boolean convertFromCurrency1ToCurrency2) {
        if(convertFromCurrency1ToCurrency2){
            exchangeRate = 1/exchangeRate;
        }
        return value * exchangeRate;
    }

    @Override
    public boolean doesCurrencyExist(String currency){
        return file.contains(currency);
    }

    @Override
    public boolean doesCurrencyListContainBaseCurrencies() {
        return doesCurrencyExist("USD") && doesCurrencyExist("JMD");
    }

    @Override
    public double getCurrencyRate(String currency){
        return Double.parseDouble(file.getString(currency, "0"))
    }

    @Override
    public boolean updateExchangeRate() {
        return false;
    }

    @Override
    public String getFirstCurrencySelectionFromFile() {
        return file.getString("currency1", "JMD");
    }

    @Override
    public String getSecondCurrencySelectionFromFile() {
        return file.getString("currency2", "USD");
    }

    @Override
    public String[] getCurrencyList(){
        List<String> currencyList = new LinkedList<String>(Arrays.asList(file.getAll().keySet().toArray(new String[0])));


        for (Iterator<String> iterator = currencyList.iterator(); iterator.hasNext(); ) {
            String value = iterator.next();
            if (value.length() > 3) {
                iterator.remove();
            }
        }

        currencyList.sort(currencyList, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });

        return currencyList.toArray(new String[0]);
    }

    @Override
    public boolean saveCurrencySelection(String currency1, String currency2) {
        Map<String, String> saveCurrencyMap = new LinkedHashMap<>();
        saveCurrencyMap.put("currency1", currency1);
        saveCurrencyMap.put("currency2", currency2);
        return saveKeyValuePairs(saveCurrencyMap);
    }

    private void saveKeyValuePairs(Map<String, String> map){
        SharedPreferences.Editor editor = file.edit();

        for (String key in map.keySet()){
            editor.putString(key, map.get(key));
        }

        return editor.commit();
    }
}
