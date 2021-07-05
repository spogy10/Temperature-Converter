package com.jr.poliv.temperatureconverteralpha.services;

import android.content.SharedPreferences;
import android.util.Log;

import com.jr.poliv.temperatureconverteralpha.Currency;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MyCurrencyService implements CurrencyService {

    private final SharedPreferences file;
    private final String JMD_KEY = "JMD";
    private final String USD_KEY = "USD";
    private final String FIRST_CURRENCY_KEY = "currency1";
    private final String SECOND_CURRENCY_KEY = "currency2";
    private final String DATE_LAST_UPDATED_KEY = "date last updated";
    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

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
        return doesCurrencyExist(USD_KEY) && doesCurrencyExist(JMD_KEY);
    }

    @Override
    public double getCurrencyRate(String currency){
        return Double.parseDouble(file.getString(currency, "0"));
    }

    @Override
    public String getFirstCurrencySelectionFromFile() {
        return file.getString(FIRST_CURRENCY_KEY, JMD_KEY);
    }

    @Override
    public String getSecondCurrencySelectionFromFile() {
        return file.getString(SECOND_CURRENCY_KEY, USD_KEY);
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

        currencyList.sort((s1, s2) -> s1.compareToIgnoreCase(s2));

        return currencyList.toArray(new String[0]);
    }

    @Override
    public boolean saveCurrencySelection(String currency1, String currency2) {
        Map<String, String> saveCurrencyMap = new LinkedHashMap<>();
        saveCurrencyMap.put(FIRST_CURRENCY_KEY, currency1);
        saveCurrencyMap.put(SECOND_CURRENCY_KEY, currency2);
        return saveKeyValuePairs(saveCurrencyMap);
    }

    private SharedPreferences.Editor prepareEditor(){
        return file.edit();
    }

    private boolean saveEdits(SharedPreferences.Editor editor){
        return editor.commit();
    }

    private boolean saveKeyValuePairs(Map<String, String> map){
        SharedPreferences.Editor editor = prepareEditor();

        for (String key : map.keySet()){
            editor.putString(key, map.get(key));
        }

        return saveEdits(editor);
    }

    @Override
    public boolean updateExchangeRate() {
        try {
            String jmdExchangeRate = getJmdExchangeRate();
            saveDefaultCurrencies(jmdExchangeRate);
            getAndSaveExchangeRates();
            updateLastUpdatedDate();
            return true;
        } catch (IOException e) {
            Log.e("Paul", e.getMessage());
            Log.e("Paul", "nah it didn't work");
        }
        return false;
    }

    private String getJmdExchangeRate(){
        //todo: insert code to get jmd rate from boj
    }

    private void saveDefaultCurrencies(String jmdExchangeRateString){
        double jmdExchangeRate = 1/Double.parseDouble(jmdExchangeRateString);
        double usdExchangeRate = 1d;

        Currency jmd = new Currency(JMD_KEY, jmdExchangeRate);
        Currency usd = new Currency(USD_KEY, usdExchangeRate);

        LinkedList<Currency> defaultCurrencies = new LinkedList<>(Arrays.asList(jmd, usd));

        saveExchangeRates(defaultCurrencies);
    }

    private void addCurrencyToEditor(SharedPreferences.Editor editor, Currency currency){
        String key = currency.getCurrency();
        String value = String.valueOf(currency.getRate());
        editor.putString(key, value);
    }

    private boolean saveExchangeRates(List<Currency> currencies){
        SharedPreferences.Editor editor = prepareEditor();
        for(Currency currency : currencies){
            addCurrencyToEditor(editor, currency);
        }
        return saveEdits(editor);
    }

    private void getAndSaveExchangeRates() throws IOException {
        String currencyApiResult = getExchangeRates();
        processAndSaveCurrencies(currencyApiResult);
    }

    private String getExchangeRates() throws IOException {
        InputStream is = null;
        try{
            URL url = new URL("http://data.fixer.io/api/latest?access_key=90fb7ac8a78ad68ec1f6636c1317e439"); //http://api.fixer.io/latest?base=USD
            HttpURLConnection in = (HttpURLConnection) url.openConnection();
            in.setReadTimeout(10000 /* milliseconds */);
            in.setConnectTimeout(15000 /* milliseconds */);
            in.setRequestMethod("GET");
            in.setDoInput(true);
            in.connect();
            Log.d("Paul", "The response is: " + in.getResponseCode());
            is = in.getInputStream();
            return IOUtils.toString(is, StandardCharsets.UTF_8);
        } finally{
            if (is != null)
                is.close();
        }
    }

    private void processAndSaveCurrencies(String result){
        try {
            JSONObject jObject = new JSONObject(result);
            JSONObject j2 = new JSONObject(jObject.getString("rates"));

            j2.remove("JMD");

            JSONArray names = j2.names();

            double eurToUsd = Double.parseDouble(j2.getString("USD"));

            SharedPreferences.Editor editor = prepareEditor();

            for(int i = 0; i < names.length(); i++){
                String currencyName = names.getString(i);
                double currencyValue = eurToUsd / Double.parseDouble(j2.getString(names.getString(i)));
                Currency currency = new Currency(currencyName, currencyValue);
                addCurrencyToEditor(editor, currency);
                Log.d("Paul", "THIS IS THE STRING"+ " " + currencyName + " = " + currencyValue + ".");
            }

            saveEdits(editor);
        } catch (JSONException e) {
            Log.d("Paul", "THIS IS NOT THE STRING"+e.toString());
            e.printStackTrace();
        }
    }

    private void updateLastUpdatedDate(){
        SharedPreferences.Editor editor = prepareEditor();
        Date current = new Date();
        editor.putString(DATE_LAST_UPDATED_KEY, DATE_FORMAT.format(current));
        saveEdits(editor);
    }

}
