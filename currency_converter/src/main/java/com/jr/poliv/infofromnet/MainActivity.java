package com.jr.poliv.infofromnet;

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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class MainActivity extends AppCompatActivity {
    public String dataFromAsyncTask = "0";
    public String folder_name;
    public String file_name;
    double exchangeRate = 0;
    EditText editText, editText2;
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
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        folder_name = getString(R.string.folder_name);
        file_name = getString(R.string.file_name);
        editText = (EditText) findViewById(R.id.editText);
        editText2 = (EditText) findViewById(R.id.editText2);
        file = this.getSharedPreferences(folder_name, Context.MODE_PRIVATE);


        if (!file.contains(file_name))
            Toast.makeText(MainActivity.this, "Update Exchange Rate", Toast.LENGTH_LONG).show();
        else {
            exchangeRate = Double.parseDouble(file.getString(file_name, "0"));
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
                    Toast.makeText(MainActivity.this, "Update Exchange Rate", Toast.LENGTH_LONG).show();
                else
                    if(editText.isFocused())
                        if (!( (editText.getText().toString().equals("")) || (editText.getText().toString().equals(".")) ))
                            setUS();
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
                    Toast.makeText(MainActivity.this, "Update Exchange Rate", Toast.LENGTH_LONG).show();
                else
                    if(editText2.isFocused())
                        if (!( (editText2.getText().toString().equals("")) || (editText2.getText().toString().equals(".")) ))
                            setJA();
            }

        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void afterTextChanged (Editable s){
        Toast.makeText(MainActivity.this, "did it work", Toast.LENGTH_SHORT).show();

    }

    public double jaToUs(double ja) {
        return ja / exchangeRate;
    }

    public double usTOJa(double us) {
        return us * exchangeRate;
    }

    public void setUS() {
        editText2.setText(String.format("%.2f", jaToUs(Double.parseDouble(editText.getText().toString()))));
    }

    public void setJA() {
        editText.setText(String.format("%.2f", usTOJa(Double.parseDouble(editText2.getText().toString()))));
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
                "                            <td><b>CURRENCY</b></td>\n" +
                "                            <td align=\"center\" width=\"256px\"><b>PURCHASE</b></td>\n" +
                "                            <td align=\"center\" width=\"198px\"><b>SALES</b></td>\n" +
                "                          </tr>\n" +
                "                          \n" +
                "\t\t\t\t\t<tr><td >USD</td><td align=\"right\">";
        string = string.substring(string.indexOf(intro));
        return string.substring(intro.length(), intro.length() + 8);
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
                Uri.parse("android-app://com.jr.poliv.infofromnet/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

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
                Uri.parse("android-app://com.jr.poliv.infofromnet/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


    public class ReadFromWebsite extends AsyncTask<Object, Void, String> {

        @Override
        protected String doInBackground(Object[] params) {
            try {
                return getInfoFromWebsite();

            } catch (IOException e) {
                Log.d("Paul", "nah it didn't work");
                return "It didn't work";
            }

        }


        protected void onPostExecute(String result) {
            Log.d("Paul", "Result is " + result);
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.update_exchange_rate) {
            ConnectivityManager connMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                try {
                    dataFromAsyncTask = new ReadFromWebsite().execute().get(10, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    setDialog("Update Failed");
                    Log.d("Paul", String.valueOf(e));
                } catch (ExecutionException e) {
                    setDialog("Update Failed");
                    Log.d("Paul", String.valueOf(e));
                } catch (TimeoutException e) {
                    setDialog("System Timed Out");
                    Log.d("Paul", String.valueOf(e));
                }
                Log.d("Paul", "The file name is " + file_name);
                Log.d("Paul", "Exchange rate is " + dataFromAsyncTask);
                if (Double.parseDouble(dataFromAsyncTask) != 0) {
                    exchangeRate = Double.parseDouble(dataFromAsyncTask);
                    Log.d("Paul", "The Exchange rate variable has been changed to " + String.format("%.4f", exchangeRate));
                }
                editor = file.edit();
                if (editor.putString(file_name, dataFromAsyncTask).commit()) {
                    Log.d("Paul", "written to file");
                }
            } else
                setDialog("No Network Connection");
            return true;
        }

        if (id == R.id.display_exchange_rate) {

            if (!file.contains(file_name))
                Toast.makeText(MainActivity.this, "Update Exchange Rate", Toast.LENGTH_LONG).show();
            else {
                setDialog("US$1 = JA$ " + file.getString(file_name, "0"));
            }


        }

        return super.onOptionsItemSelected(item);
    }

    public void setDialog(String message) {
        Bundle args = new Bundle();
        args.putString("message", message);
        dialog.setArguments(args);
        dialog.show(getFragmentManager(), "");
    }

    public class OkDialog extends DialogFragment {
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
