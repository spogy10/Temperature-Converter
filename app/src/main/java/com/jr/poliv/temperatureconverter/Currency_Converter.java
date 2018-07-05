package com.jr.poliv.temperatureconverter;

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
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Currency_Converter extends AppCompatActivity {
    public String dataFromAsyncTask = "0";
    public Currency[] currencies = null;
    public String folder_name;

    public String date_last_updated;
    double exchangeRate = 0;
    public String currency1, currency2;
    SimpleDateFormat dateFormat;
    EditText editText, editText2;
    Spinner list,list2;
    ProgressBar progressBar;
    ArrayAdapter<String> listAdapter, list2Adapter;
    DialogFragment dialog = new OkDialog();
    SharedPreferences file;
    SharedPreferences.Editor editor;


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
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

        currency1 = "JMD"; currency2 = "USD";

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapCurrencies();
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        folder_name = getString(com.jr.poliv.temperatureconverter.R.string.folder_name);

        date_last_updated = getString(R.string.date_last_updated);
        editText = (EditText) findViewById(com.jr.poliv.temperatureconverter.R.id.editText);
        editText2 = (EditText) findViewById(com.jr.poliv.temperatureconverter.R.id.editText2);
        list = (Spinner) findViewById(R.id.list);
        list2 = (Spinner) findViewById(R.id.list2);
        file = this.getSharedPreferences(folder_name, Context.MODE_PRIVATE);
        editor = file.edit();

        if (!file.contains("USD"))
            Toast.makeText(Currency_Converter.this, R.string.update_exchange_rate, Toast.LENGTH_LONG).show();
        else {
            updateExchangeRate();
            populateDropDownList();
            checkDate();
            restoreCurrencySelectionFromFile();
        }

        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }

            @Override
            public void afterTextChanged(Editable arg0) {
                if (exchangeRate == 0)
                    Toast.makeText(Currency_Converter.this, R.string.update_exchange_rate, Toast.LENGTH_LONG).show();
                else
                if(editText.isFocused())
                    if (!( (editText.getText().toString().equals("")) || (editText.getText().toString().equals(".")) ))
                        setCurr2();
            }

        });

        editText2.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }

            @Override
            public void afterTextChanged(Editable arg0) {
                if (exchangeRate == 0)
                    Toast.makeText(Currency_Converter.this, R.string.update_exchange_rate, Toast.LENGTH_LONG).show();
                else
                if(editText2.isFocused())
                    if (!( (editText2.getText().toString().equals("")) || (editText2.getText().toString().equals(".")) ))
                        setCurr1();
            }

        });

        list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setCurrency1(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        list2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setCurrency2(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }
    public void afterTextChanged (Editable s){
        Toast.makeText(Currency_Converter.this, "did it work", Toast.LENGTH_SHORT).show();

    }

    public void restoreCurrencySelectionFromLocalVariables(){
        if(file.contains("currency1") && (file.contains("currency2"))){
            if(listAdapter.getPosition(file.getString("currency1", "JMD")) != -1) {
                list.setSelection(listAdapter.getPosition(currency1));
            }else{
                list.setSelection(listAdapter.getPosition("JMD"));
            }
            if(list2Adapter.getPosition(file.getString("currency2", "USD")) != -1) {
                list2.setSelection(list2Adapter.getPosition(currency2));
            }else{
                list2.setSelection(list2Adapter.getPosition("USD"));
            }
        }
    }

    public void restoreCurrencySelectionFromFile(){

        if(file.contains("currency1") && (file.contains("currency2"))){
            if(listAdapter.getPosition(file.getString("currency1", "JMD")) != -1) {
                list.setSelection(listAdapter.getPosition(file.getString("currency1", "JMD")));
            }else{
                list.setSelection(listAdapter.getPosition("JMD"));
            }
            if(list2Adapter.getPosition(file.getString("currency2", "USD")) != -1) {
                list2.setSelection(list2Adapter.getPosition(file.getString("currency2", "USD")));
            }else{
                list2.setSelection(list2Adapter.getPosition("USD"));
            }
        }

    }

    public double curr1ToCurr2(double curr1) {
        return curr1 / exchangeRate;
    }

    public double curr2ToCurr1(double curr2) {
        return curr2 * exchangeRate;
    }

    public void setCurr2() {
        editText2.setText(String.format("%.2f", curr1ToCurr2(Double.parseDouble(editText.getText().toString()))));
    }

    public void setCurr1() {
        editText.setText(String.format("%.2f", curr2ToCurr1(Double.parseDouble(editText2.getText().toString()))));
    }

    public void setCurrency1(String currency){
        currency1 = currency;
        updateExchangeRate();
    }

    public void setCurrency2(String currency){
        currency2 = currency;
        updateExchangeRate();
    }

    public void swapCurrencies(){

        if (!file.contains("JMD"))
            Toast.makeText(Currency_Converter.this, R.string.update_exchange_rate, Toast.LENGTH_LONG).show();
        else {
            int tempInt = list.getSelectedItemPosition();
            list.setSelection(list2.getSelectedItemPosition());
            list2.setSelection(tempInt);
        }

    }

    public boolean saveCurrency1(){
        return editor.putString("currency1", currency1).commit();
    }

    public boolean saveCurrency2(){
        return editor.putString("currency2", currency2).commit();
    }

    public boolean saveCurrencies(){
        return (saveCurrency1() && saveCurrency2());
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
            String inputStream = extractExchangeRate(IOUtils.toString(is, "UTF-8"));

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
            String output = IOUtils.toString(is, "UTF-8");
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

    public Currency getExchangeRate(String currency){

        Currency dollar = new Currency();

        if (file.contains(currency)){
            dollar.setCurrency(currency);
            dollar.setRate(Double.parseDouble(file.getString(currency, "0")));
        }
        return dollar;
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
                Uri.parse("android-app://com.jr.poliv.temperatureconverter/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        saveCurrencies();

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
                Uri.parse("android-app://com.jr.poliv.temperatureconverter/http/host/path")
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
                Log.d("Paul", "nah it didn't work");
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
        getMenuInflater().inflate(com.jr.poliv.temperatureconverter.R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == com.jr.poliv.temperatureconverter.R.id.update_exchange_rate) {
            update();
            return true;
        }

        if (id == com.jr.poliv.temperatureconverter.R.id.display_exchange_rate) {

            if (!file.contains("JMD"))
                Toast.makeText(Currency_Converter.this, R.string.update_exchange_rate, Toast.LENGTH_LONG).show();
            else {
                checkDate();
                setDialog(currency1+"$1 = "+currency2+"$ " + exchangeRate);
            }


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

    public void updateExchangeRate(){
        exchangeRate = calculateExchangeRate(currency1, currency2);
    }

    public void populateDropDownList(){
        String currency1 = this.currency1, currency2 = this.currency2;
        String[] currencyList = getCurrencies(); //array of currency, put in listview
        listAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, currencyList);
        list2Adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, currencyList);
        list.setAdapter(listAdapter);
        list2.setAdapter(list2Adapter);

        for (String c : currencyList){
            Log.d("Paul",c);
        }

        setCurrency1(currency1); setCurrency2(currency2);

    }

    public String[] getCurrencies(){
        List<String> currencyList = new LinkedList<String>(Arrays.asList(file.getAll().keySet().toArray(new String[0])));


        for (Iterator<String> iterator = currencyList.iterator(); iterator.hasNext(); ) {
            String value = iterator.next();
            if (value.length() > 3) {
                iterator.remove();
            }
        }

        Collections.sort(currencyList, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });

        String[] currency = currencyList.toArray(new String[0]);

        return currency;
    }

    public Double calculateExchangeRate(String currency1, String currency2){
        return ((getExchangeRate(currency2).getRate())/(getExchangeRate(currency1).getRate()));
    }

    private void showProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar(){
        progressBar.setVisibility(View.GONE);
    }

    public void setDialog(String message) {
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
