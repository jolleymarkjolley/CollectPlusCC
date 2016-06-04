package zebra.collectpluscc;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Collect extends Activity {
    // Tag used for logging errors
    private static final String TAG = Collect.class.getSimpleName();

    // Let's define some intent strings
    // This intent string contains the source of the data as a string
    private static final String SOURCE_TAG = "com.motorolasolutions.emdk.datawedge.source";
    // This intent string contains the barcode symbology as a string
    private static final String LABEL_TYPE_TAG = "com.motorolasolutions.emdk.datawedge.label_type";
    // This intent string contains the barcode data as a byte array list
    private static final String DECODE_DATA_TAG = "com.motorolasolutions.emdk.datawedge.decode_data";

    // This intent string contains the captured data as a string
    // (in the case of MSR this data string contains a concatenation of the track data)
    private static final String DATA_STRING_TAG = "com.motorolasolutions.emdk.datawedge.data_string";

    String theBtMacAddress = "AC:3F:A4:00:0F:C5";
    Boolean Scanned= false;
    private static String ourIntentAction = "";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collect);

        ourIntentAction = getString(R.string.intentAction1);

        // Let's set the cursor at the end of any text in the editable text field
        EditText et = (EditText)findViewById(R.id.editbox);
        Editable txt = et.getText();
        et.setSelection(txt.length());

        // in case we have been launched by the DataWedge intent plug-in
        // using the StartActivity method let's handle the intent
  //      Intent i = getIntent();
//       handleDecodeData(i);
    }


    // We need to handle any incoming intents, so let override the onNewIntent method
    @Override
    public void onNewIntent(Intent i) {
        handleDecodeData(i);
    }

    // This function is responsible for getting the data from the intent
    // formatting it and adding it to the end of the edit box
    private void handleDecodeData(Intent i) {



            // check the intent action is for us
            if (i.getAction().contentEquals(ourIntentAction)) {
                // define a string that will hold our output
                String data = i.getStringExtra(DATA_STRING_TAG);
                OkDialog("Your label is being printed");
                Context context = getApplicationContext();

                NotifyStore("DropOff");


            }


    }

    public void ButtonClicked(View view) {
        switch (view.getId()) {
            case R.id.button:

                    OkDialog("Parcel Found, your label is being printed");
                    break;

        }
    }



    public void OkDialog(final String Message)
    {
        final Context ctx = this;
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                final Dialog dialog = new Dialog(ctx);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dlg_ok);
                TextView dlgOkBtn = (TextView) dialog.findViewById(R.id.ok);
                TextView dlgAlert = (TextView) dialog.findViewById(R.id.alert);
                dlgAlert.setText(Message);

                // if button is clicked, close the custom dialog
                dlgOkBtn.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        dialog.dismiss();
                    }
                });
                dialog.show();

            }
        });
    }

    public void NotifyStore(final String Mode)
    {
        new AsyncTask<Void, Void, Void>() {

            private InputStream inputStream = null;

            @Override
            protected Void doInBackground(Void... params)
            {
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    ArrayList<NameValuePair> getParams = new ArrayList<NameValuePair>();


                    String paramString = URLEncodedUtils.format(getParams, "utf-8");
                    String url = "http://broker.zebra-ses.com:81" + "?" + paramString;
                    HttpGet httpGet = new HttpGet(url);
                    httpGet.setHeader("Accept", "text/plain");
                    httpGet.setHeader("Content-type", "application/json");
                    httpGet.setHeader("Device-Serial", Build.ID);
                    httpGet.setHeader("Device-Type",Build.MODEL);
                    HttpResponse httpResponse = httpClient.execute(httpGet);
                    HttpEntity httpEntity = httpResponse.getEntity();
                    inputStream = httpEntity.getContent();
                    BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
                    StringBuilder sBuilder = new StringBuilder();

                    String line = null;
                    while ((line = bReader.readLine()) != null) sBuilder.append(line + "\n");
                    inputStream.close();
                    PostNotify(sBuilder.toString());
                } catch (IOException e) {
                    return null;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result)
            {
                super.onPostExecute(result);
            }

            @Override
            protected void onPreExecute()
            {
                super.onPreExecute();
            }

        }.execute();
    }

    public void PostNotify(final String Message)
    {
        final Context ctx = this;
        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                final Dialog dialog = new Dialog(ctx);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dlg_ok);
                TextView dlgOkBtn = (TextView) dialog.findViewById(R.id.ok);
                TextView dlgAlert = (TextView) dialog.findViewById(R.id.alert);
                dlgAlert.setText(Message);

                // if button is clicked, close the custom dialog
                dlgAlert.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        dialog.dismiss();
                        //sendZplOverBluetooth(theBtMacAddress, GenerateReciept());


                      }
        });


            }
        });
    }
}
