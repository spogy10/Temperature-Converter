package com.jr.poliv.temperatureconverteralpha.services;

public interface CurrencyService {

    double calculateExchangeRate(Currency from, Currency to);
    double calculateCurrency(double value, double exchangeRate, boolean convertFromCurrency1ToCurrency2);
    boolean doesCurrencyExist(String currency);
    boolean doesCurrencyListContainBaseCurrencies();
    double getCurrencyRate(String currency);
    boolean updateExchangeRate();
    boolean saveCurrencySelection(String currency1, String currency2);
    String getFirstCurrencySelectionFromFile();
    String getSecondCurrencySelectionFromFile();
    String[] getCurrencyList();
}