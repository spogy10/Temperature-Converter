package com.jr.poliv.temperatureconverteralpha;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jr.poliv.temperatureconverteralpha.services.CurrencyManager;
import com.jr.poliv.temperatureconverteralpha.services.UpdateExchangeRateResult;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class CurrencyConverter extends AppCompatActivity implements UpdateExchangeRateResult {

    private CurrencyManager currencyManager;
    private String folder_name;

    EditText etCurrency1, etCurrency2;
    Spinner spCurrencyList1, spCurrencyList2;
    ProgressBar progressBar;
    ArrayAdapter<String> currencyAdapter1, currencyAdapter2;
    DialogFragment dialog = new OkDialog();


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.-
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client2;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_converter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setFABAction();

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        etCurrency1 = (EditText) findViewById(com.jr.poliv.temperatureconverteralpha.R.id.editText);
        etCurrency2 = (EditText) findViewById(com.jr.poliv.temperatureconverteralpha.R.id.editText2);
        spCurrencyList1 = (Spinner) findViewById(R.id.list);
        spCurrencyList2 = (Spinner) findViewById(R.id.list2);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        folder_name = getString(com.jr.poliv.temperatureconverteralpha.R.string.folder_name);

        SharedPreferences file = this.getSharedPreferences(folder_name, Context.MODE_PRIVATE);

        currencyManager = new CurrencyManager(this, file);

        startupTasks();

        setetCurrency1OnTextChange();

        setetCurrency2OnTextChange();

        setCurrency1ListOnItemSelectedListener();

        setCurrency2ListOnItemSelectedListener();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void startupTasks() {
        if (!currencyManager.doesCurrencyListContainBaseCurrencies()){
            Toast.makeText(CurrencyConverter.this, R.string.update_exchange_rate, Toast.LENGTH_LONG).show();
            return;
        }

        populateDropDownListAndRestoreSelectedCurrencies();
        checkDate();
    }

    private void populateDropDownListAndRestoreSelectedCurrencies(){
        populateDropDownList();
        restoreCurrencySelectionFromFile();
    }

    private void populateDropDownList(){
        String[] currencyList = currencyManager.getCurrencyList(); //array of currency, put in listview
        currencyAdapter1 = new ArrayAdapter<String>(this, R.layout.spinner_item, currencyList);
        currencyAdapter2 = new ArrayAdapter<String>(this, R.layout.spinner_item, currencyList);
        spCurrencyList1.setAdapter(currencyAdapter1);
        spCurrencyList2.setAdapter(currencyAdapter2);

        for (String c : currencyList){
            Log.d("Paul",c);
        }
    }

    private void setCurrency2ListOnItemSelectedListener(){
        spCurrencyList2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String currency = parent.getItemAtPosition(position).toString();
                currencyManager.setSelectedCurrency2(currency);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setCurrency1ListOnItemSelectedListener(){
        spCurrencyList1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String currency = parent.getItemAtPosition(position).toString();
                currencyManager.setSelectedCurrency1(currency);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setetCurrency2OnTextChange() {
        etCurrency2.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }

            @Override
            public void afterTextChanged(Editable arg0) {
                if (currencyManager.isExchangeRateSet()){
                    Toast.makeText(CurrencyConverter.this, R.string.update_exchange_rate, Toast.LENGTH_LONG).show();
                    return;
                }

                if(!etCurrency2.isFocused()) return;

                if (!( (etCurrency2.getText().toString().equals("")) || (etCurrency2.getText().toString().equals(".")) )){
                    calculateCurrency1();
                }
            }

        });
    }

    private void setetCurrency1OnTextChange() {
        etCurrency1.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }

            @Override
            public void afterTextChanged(Editable arg0) {
                if (currencyManager.isExchangeRateSet()){
                    Toast.makeText(CurrencyConverter.this, R.string.update_exchange_rate, Toast.LENGTH_LONG).show();
                    return;
                }
                if(!etCurrency1.isFocused()) return;

                if (!( (etCurrency1.getText().toString().equals("")) || (etCurrency1.getText().toString().equals(".")) )){
                    calculateCurrency2();
                }
            }

        });
    }

    private void setFABAction() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapCurrencies();
            }
        });
    }

    private void restoreCurrencySelectionFromFile(){
        restoreCurrencySelection1();
        restoreCurrencySelection2();
    }

    private void restoreCurrencySelection1(){
        final int NOT_IN_LIST = -1;
        String currency = currencyManager.getFirstCurrencySelectionFromFile();

        int currencyPosition = currencyAdapter1.getPosition(currency);

        if(currencyPosition == NOT_IN_LIST) {
            currencyPosition = currencyAdapter1.getPosition("JMD");
        }

        spCurrencyList1.setSelection(currencyPosition);
    }

    private void restoreCurrencySelection2(){
        final int NOT_IN_LIST = -1;
        String currency = currencyManager.getSecondCurrencySelectionFromFile();

        int currencyPosition = currencyAdapter2.getPosition(currency);

        if(currencyPosition == NOT_IN_LIST) {
            currencyPosition = currencyAdapter2.getPosition("JMD");
        }

        spCurrencyList2.setSelection(currencyPosition);
    }

    private void calculateCurrency2() {
        double currency1 = Double.parseDouble(etCurrency1.getText().toString());
        double currency2 = currencyManager.convertCurrency1ToCurrency2(currency1);
        setEtCurrency2(currency2);
    }

    private void calculateCurrency1() {
        double currency2 = Double.parseDouble(etCurrency2.getText().toString());
        double currency1 = currencyManager.convertCurrency2ToCurrency1(currency2);
        setEtCurrency1(currency1);
    }

    private void setEtCurrency1(double value){
        etCurrency1.setText(String.format("%.2f", value));
    }

    private void setEtCurrency2(double value){
        etCurrency2.setText(String.format("%.2f", value));
    }

    private void swapCurrencies(){

        if (!currencyManager.doesCurrencyListContainBaseCurrencies()){
            Toast.makeText(CurrencyConverter.this, R.string.update_exchange_rate, Toast.LENGTH_LONG).show();
            return;
        }

        int tempInt = spCurrencyList1.getSelectedItemPosition();
        spCurrencyList1.setSelection(spCurrencyList2.getSelectedItemPosition());
        spCurrencyList2.setSelection(tempInt);
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

        if(currencyManager != null){
            currencyManager.saveCurrencySelection();
        }

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

    private void showProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar(){
        progressBar.setVisibility(View.GONE);
    }

    private void setDialog(String message) {
        Bundle args = new Bundle();
        args.putString("message", message);
        dialog.setArguments(args);
        dialog.show(getFragmentManager(), "");
    }

    public static class OkDialog extends DialogFragment {
        public OkDialog() {

        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Bundle args = getArguments();
            String message = args.getString("message", "");

            return new AlertDialog.Builder(getActivity())
                    .setTitle("")
                    .setMessage(message)
                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .create();
        }
    }

    private void checkDate(){
        try {
            boolean showUpdateMessage = currencyManager.shouldShowUpdateMessageBasedOnLastUpdate();

            if (showUpdateMessage){
                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()){
                    DialogFragment newFragment = new UpdateDialog();
                    newFragment.show(getFragmentManager(), "dialog");
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateCurrencyExchangeRates() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            showProgressBar();
            currencyManager.updateCurrencyExchangeRates();
        } else {
            setDialog("No Network Connection");
        }
    }

    @Override
    public void onSuccessfulUpdate(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                populateDropDownListAndRestoreSelectedCurrencies();
                hideProgressBar();
                Toast.makeText(CurrencyConverter.this, R.string.success, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onUpdateFailure(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideProgressBar();
                Toast.makeText(CurrencyConverter.this, R.string.error_getting_rates, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.jr.poliv.temperatureconverteralpha.R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == com.jr.poliv.temperatureconverteralpha.R.id.update_exchange_rate) {
            updateCurrencyExchangeRates();
            return true;
        }

        if (id == com.jr.poliv.temperatureconverteralpha.R.id.display_exchange_rate) {

            if (!currencyManager.doesCurrencyListContainBaseCurrencies()){
                Toast.makeText(CurrencyConverter.this, R.string.update_exchange_rate, Toast.LENGTH_LONG).show();
            }
            checkDate();
            String exchangeRateDisplay = currencyManager.displayExchangeRate();
            setDialog(exchangeRateDisplay);
        }

        return super.onOptionsItemSelected(item);
    }
}
