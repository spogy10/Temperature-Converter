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

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Currency_Converter extends AppCompatActivity {

    CurrencyManager currencyManager;

    public String dataFromAsyncTask = "0";

    public String folder_name;

    public String date_last_updated;
    SimpleDateFormat dateFormat;
    EditText etCurrency1, etCurrency2;
    Spinner spCurrencyList1, spCurrencyList2;
    ProgressBar progressBar;
    ArrayAdapter<String> currencyAdapter1, currencyAdapter2;
    DialogFragment dialog = new OkDialog();
    SharedPreferences file;
    SharedPreferences.Editor editor;


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


        dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        folder_name = getString(com.jr.poliv.temperatureconverteralpha.R.string.folder_name);

        date_last_updated = getString(R.string.date_last_updated);

        file = this.getSharedPreferences(folder_name, Context.MODE_PRIVATE);
        editor = file.edit();

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
            Toast.makeText(Currency_Converter.this, R.string.update_exchange_rate, Toast.LENGTH_LONG).show();
            return;
        }

        updateExchangeRate();
        populateDropDownList();
        checkDate();
        restoreCurrencySelectionFromFile();
    }

    public void populateDropDownList(){
        String currency1 = this.selectedCurrency1, currency2 = this.selectedCurrency2;
        String[] currencyList = currencyManager.getCurrencyList(); //array of currency, put in listview
        currencyAdapter1 = new ArrayAdapter<String>(this, R.layout.spinner_item, currencyList);
        currencyAdapter2 = new ArrayAdapter<String>(this, R.layout.spinner_item, currencyList);
        spCurrencyList1.setAdapter(currencyAdapter1);
        spCurrencyList2.setAdapter(currencyAdapter2);

        for (String c : currencyList){
            Log.d("Paul",c);
        }

        currencyManager.setSelectedCurrency1(currency1);
        currencyManager.setSelectedCurrency2(currency2);
    }

    private void setCurrency2ListOnItemSelectedListener() {
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

    private void setCurrency1ListOnItemSelectedListener() {
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
                    Toast.makeText(Currency_Converter.this, R.string.update_exchange_rate, Toast.LENGTH_LONG).show();
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
                    Toast.makeText(Currency_Converter.this, R.string.update_exchange_rate, Toast.LENGTH_LONG).show();
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

    public void restoreCurrencySelectionFromLocalVariables(){
        if(file.contains("currency1") && (file.contains("currency2"))){
            if(currencyAdapter1.getPosition(file.getString("currency1", "JMD")) != -1) {
                spCurrencyList1.setSelection(currencyAdapter1.getPosition(selectedCurrency1));
            }else{
                spCurrencyList1.setSelection(currencyAdapter1.getPosition("JMD"));
            }
            if(currencyAdapter2.getPosition(file.getString("currency2", "USD")) != -1) {
                spCurrencyList2.setSelection(currencyAdapter2.getPosition(selectedCurrency2));
            }else{
                spCurrencyList2.setSelection(currencyAdapter2.getPosition("USD"));
            }
        }
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

        spCurrencyList1.setSelection(currencyPosition);
    }

    public void calculateCurrency2() {
        double currency1 = Double.parseDouble(etCurrency1.getText().toString());
        double currency2 = currencyManager.convertCurrency1ToCurrency2(currency1);
        setEtCurrency2(currency2);
    }

    public void calculateCurrency1() {
        double currency2 = Double.parseDouble(etCurrency2.getText().toString());
        double currency1 = currencyManager.convertCurrency2ToCurrency1(currency2);
        setEtCurrency1(currency1);
    }

    private void setEtCurrency1(double value){
        etCurrency1.setText(String.format("%.2f", value);
    }

    private void setEtCurrency2(double value){
        etCurrency2.setText(String.format("%.2f", value);
    }

    public void swapCurrencies(){

        if (!currencyManager.doesCurrencyListContainBaseCurrencies()){
            Toast.makeText(Currency_Converter.this, R.string.update_exchange_rate, Toast.LENGTH_LONG).show();
            return;
        }

        int tempInt = spCurrencyList1.getSelectedItemPosition();
        spCurrencyList1.setSelection(spCurrencyList2.getSelectedItemPosition());
        spCurrencyList2.setSelection(tempInt);
    }

    public String getInfoFromWebsite() throws IOException {
        InputStream is = null;
        try {
            URL url = new URL("http://www.boj.org.jm/foreign_exchange/fx_trading_summary.php");
            HttpURLConnection in = (HttpURLConnection) url.openConnection();
            in.setReadTimeout(10000 /* milliseconds */);
            in.setConnectTimeout(15000 /* milliseconds */);
            in.setRequestMethod("GET");
            in.setDoInput(true);
            in.connect();
            Log.d("Paul", "The response is: " + in.getResponseCode());
            is = in.getInputStream();
            String inputStream = extractExchangeRate(IOUtils.toString(is, StandardCharsets.UTF_8));

            if (inputStream != null) {
                Log.d("Paul", "It worked");
            }
            return inputStream;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public void getExchangeRates() throws IOException{
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
            String output = IOUtils.toString(is, StandardCharsets.UTF_8);
            //Log.d("Paul", output);

            getCurrencies(output);


        } finally{
            if (is != null)
                is.close();
        }
    }

    public  void getCurrencies(String result){
        try {
            JSONObject jObject = new JSONObject(result);
            JSONObject j2 = new JSONObject(jObject.getString("rates"));

            j2.remove("JMD");

            JSONArray names = j2.names();

            double eurToUsd = Double.parseDouble(j2.getString("USD"));

            Currency[] currencies = new Currency[names.length()];

            for(int i = 0; i < names.length(); i++){
                currencies[i] = new Currency(names.getString(i), ( eurToUsd / Double.parseDouble(j2.getString(names.getString(i)))));
                Log.d("Paul", "THIS IS THE STRING"+ " " + names.getString(i) + " = " + j2.getString(names.getString(i)) + "; reverse usd value = "+ currencies[i].getRate());
            }



            setCurrencies(currencies);


            //String aJsonString = j2.getString("BRL");
            //Log.d("Paul", "THIS IS THE STRING"+ " " + names.getString(0).toString()); //returns first currency name
        } catch (JSONException e) {
            Log.d("Paul", "THIS IS NOT THE STRING"+e.toString());
            e.printStackTrace();
        }

    }

    public void setCurrencies(Currency[] currencies){
        this.currencies = currencies;
    }

//    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
//        Reader reader;
//        reader = new InputStreamReader(stream, "UTF-8");
//        Log.d("Paul", String.valueOf(stream.markSupported()));
//        char[] buffer = new char[len];
//        reader.read(buffer);
//        return new String(buffer);
//    }

    public static String extractExchangeRate(String string) {
        String intro = "<b>10-DAY MOVING AVERAGE RATE</b></td>\n" +
                "                          </tr>\n" +
                "                          <tr>\n" +
                "                            <td  width=\"11%\"><b>CURRENCY</b></td>\n" +
                "                            <td align=\"center\" width=\"44.5%\"><b>PURCHASE</b></td>\n" +
                "                            <td align=\"center\" width=\"44.5%\"><b>SALES</b></td>\n" +
                "                          </tr>\n" +
                "                          \n" +
                "\t\t\t\t\t<tr><td >USD</td><td align=\"right\">";

        String intro2 = "<b>10-DAY MOVING AVERAGE RATE</b></td>\n" +
                "                          </tr>\n" +
                "                          <tr>\n" +
                "                            <td  width=\"11%\"><b>CURRENCY</b></td>\n" +
                "                            <td align=\"center\" width=\"44.5%\"><b>PURCHASE</b></td>\n" +
                "                            <td align=\"center\" width=\"44.5%\"><b>SALES</b></td>\n" +
                "                          </tr>\n" +
                "                          \n" +
                "\t\t\t\t\t<tr><td >USD</td><td align=\"right\">129.1550</td><td align=\"right\">";

    string = string.substring(string.indexOf(intro));
    return string.substring(intro2.length(), intro2.length() + 8);
}

    public void saveExchangeRate(Currency dollar){ //saves currency to file
        editor = file.edit();
        if (editor.putString(dollar.getCurrency(), String.valueOf(dollar.getRate())).commit()) {
            Log.d("Paul", dollar.getCurrency() + " = " + dollar.getRate() + " has been written to file");
        }
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


    public class ReadFromWebsite extends AsyncTask<Void, Void, String> {


        @Override
        protected String doInBackground(Void... voids) {
            try{
                String JMD = getInfoFromWebsite();
                Currency ja = new Currency("JMD", (1/Double.parseDouble(JMD))), usa = new Currency("USD", Double.parseDouble("1"));
                saveExchangeRate(ja);
                saveExchangeRate(usa);
                getExchangeRates();
                return "true";



            } catch (IOException e) {
                Log.e("Paul", e.getMessage());
                Log.e("Paul", "nah it didn't work");
                return "false";
            }
        }

        protected void onPostExecute(String result) {
            Log.d("Paul", "Result is " + result);
            hideProgressBar();
            update(result);
        }


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

        //noinspection SimplifiableIfStatement
        if (id == com.jr.poliv.temperatureconverteralpha.R.id.update_exchange_rate) {
            update();
            return true;
        }

        if (id == com.jr.poliv.temperatureconverteralpha.R.id.display_exchange_rate) {

            if (!currencyManager.doesCurrencyListContainBaseCurrencies()){
                Toast.makeText(Currency_Converter.this, R.string.update_exchange_rate, Toast.LENGTH_LONG).show();
            }
            checkDate();
            String exchangeRateDisplay = currencyManager.displayExchangeRate();
            setDialog(exchangeRateDisplay);
        }

        return super.onOptionsItemSelected(item);
    }

    boolean checkDate(){

        try {
            Date lastCheck = dateFormat.parse(file.getString(date_last_updated, "01-01-2000"));
            Calendar c = Calendar.getInstance();
            c.setTime(lastCheck);
            c.add(Calendar.DATE, 10);
            lastCheck = new Date(c.getTimeInMillis());

            if (new Date().after(lastCheck)){
                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()){
                    DialogFragment newFragment = new UpdateDialog();
                    newFragment.show(getFragmentManager(), "dialog");



                }
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }
        return true;
    }

    private void update(String result){
        dataFromAsyncTask = result;
        Log.d("Paul", "The file name is " + "JMD");
        Log.d("Paul", "Exchange rate successful " + dataFromAsyncTask);
        if (dataFromAsyncTask.equals("true")) {
            updateExchangeRate();
            Log.d("Paul", "The Exchange rate variable has been changed to " + String.format("%.4f", exchangeRate));

            Date current = new Date();
            editor = file.edit();

            if (editor.putString(date_last_updated, dateFormat.format(current)).commit())
                Log.d("Paul", "date written to file");
            Toast.makeText(this, R.string.success, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, R.string.error_getting_rates, Toast.LENGTH_SHORT).show();
        }

        if (currencies != null){
            for (Currency currency : currencies)
                saveExchangeRate(currency);
            currencies = null;
            populateDropDownList();
            restoreCurrencySelectionFromLocalVariables();
        }
    }

    public void update(){

            ConnectivityManager connMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                //try {
                showProgressBar();
                    new ReadFromWebsite().execute();
                /*} catch (InterruptedException e) {
                    setDialog("Update Failed");
                    Log.d("Paul", String.valueOf(e));
                } catch (ExecutionException e) {
                    setDialog("Update Failed");
                    Log.d("Paul", String.valueOf(e));
                } catch (TimeoutException e) {
                    setDialog("System Timed Out");
                    Log.d("Paul", String.valueOf(e));
                }*/

            } else
                setDialog("No Network Connection");

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
}
