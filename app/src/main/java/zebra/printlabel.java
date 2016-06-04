package zebra.collectpluscc;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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

import com.zebra.sdk.comm.BluetoothConnectionInsecure;
import com.zebra.sdk.comm.Connection;

public class printlabel extends Activity {
    // Tag used for logging errors
    private static final String TAG = printlabel.class.getSimpleName();

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
    Boolean email= false;
    private static String ourIntentAction = "";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collect);

        ourIntentAction = getString(R.string.intentAction);

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

        if (!email) {

            // check the intent action is for us
            if (i.getAction().contentEquals(ourIntentAction)) {
                // define a string that will hold our output
                String data = i.getStringExtra(DATA_STRING_TAG);
                OkDialog("Your label is being printed");
                Context context = getApplicationContext();

                Toast toast = Toast.makeText(context, data, Toast.LENGTH_SHORT);
                toast.show();
                Switchtoemail();


            }

        }
        else
        {
            //set as email field
        //ignorescan
        }
    }

    public void ButtonClicked(View view) {
        switch (view.getId()) {
            case R.id.button:
                if (!email)
                {
                    OkDialog("Parcel Found, your label is being printed");
                    Switchtoemail();
                    break;
                }
                else if(email)
                {
                    //set as email

                    OkDialog("An email has been sent to your account");

                    Switchtoparcel();
                    break;
                }

        }
    }

    public void Switchtoemail()
    {
        EditText et = (EditText)findViewById(R.id.editbox);
        TextView TV = (TextView)findViewById(R.id.Text);
        TV.setText("For an e-reciept please enter you email below, register on the Collect+ app with this email to see the parcel tracking");
        Button btn = (Button)findViewById(R.id.button);
        btn.setText("Enter");
        email=true;

    }

    public void Switchtoparcel()
    {
        EditText et = (EditText)findViewById(R.id.editbox);
        TextView TV = (TextView)findViewById(R.id.Text);
        TV.setText("Please scan you barcode or enter your parcel ID below");
        Button btn = (Button)findViewById(R.id.button);
        btn.setText("Enter Parcel ID");
        email=false;

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
                sendZplOverBluetooth(theBtMacAddress, GenerateLabel());

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

    private void sendZplOverBluetooth(final String theBtMacAddress,final String label) {

        new Thread(new Runnable() {
            public void run() {
                try {
                    // Instantiate insecure connection for given Bluetooth&reg; MAC Address.
                    Connection thePrinterConn = new BluetoothConnectionInsecure(theBtMacAddress);

                    // Initialize
                    Looper.prepare();

                    // Open the connection - physical connection is established here.
                    thePrinterConn.open();

                    // This example prints "This is a ZPL test." near the top of the label.

                    // Send the data to printer as a byte array.
               //     thePrinterConn.write(label.getBytes());

                    // Make sure the data got to the printer before closing the connection
                    Thread.sleep(500);

                    // Close the insecure connection to release resources.
                    thePrinterConn.close();

                    Looper.myLooper().quit();
                } catch (Exception e) {
                    // Handle communications error here.
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public String GenerateLabel()
    {
        return "^XA~TA000~JSN^LT0^MNW^MTD^PON^PMN^LH0,0^JMA^PR5,5~SD15^JUS^LRN^CI0^MMT^PW456^LL0807^LS0^FO0,576^GFA,01920,01920,00060," +
                ":Z64:eJxjYBgFo2AUjIKhAeT/kwmGqN5RMLwBAJkb350=:EC8A^FO0,448^GFA,01920,01920,00060,:Z64:eJxjYBhZ4D+5YIjqHQWjYBSMglEwOAEAEY7jHQ==:718A^FO160,736^GFA,02688,02688,00028,:Z64:eJztkzGO1DAYhZ/XICOaaAtEgwiipMhsSRXPESiIKOcCHGHZmG6PMeVqTuHcgCNkO4qRbDoX0fz8tjMh7GimBilPv0aW3nx6ec4fYNE/pjXwbDqtZ8aV0QGgHtv3KA5xkgSuAdXRAZKC8BotGU1HqDA8zqFwe+ErOGdXzibnefSUJbIlHZgTRL0mn6MyF7TVhoSv5WHl6yIk7xVK5rpe+RaaOeVVuJFD8l5kzkh/h/K7r1WP8A7Za3IeZLhF0Xld2Ghk70PmIMKb6NV8vEMcrtekPAsxxJpel8lrE3c/ctgDsvNVCVRpEpfzcDtygE7D9Xaay0aOPfnoa/bq0RNN9BLXNF8eE1ceuYeJa4ky9/aUS95f3O6U+5M39sPw7fr1k+ec+v38GjtUfDzpJ/g+Y7/ZnV3tRk6GOt9nuuvDLK8zileF35FWvRhuxJD7Za4v+tLo+P6CDB9lmPIE+aG1pW35vUvaeL3xUx57RKaIu1TnXeqnPEG/iKBoiPvJG6fJjv1eGuF6t4d0Ie7nypmVw8jxz+ZHywmtjVwxxMl5rG28l/RPX8++EtGw1soePT3/gh5Y5gKHM1zMw3nu8znunjXjqifcpwt52wt56zPcokWLFv0/+g3U5xdd:2FB4" +
                "^FO0,238^GB453,0,3^FS^FO146,506^GB310,0,4^FS^FO137,730^GB316,0,4^FS^FO323,598^GB0,133,3^FS^FO143,244^GB0,354,4^FS^FT420,655^A0I,56,57^FH\\^FD20^FS^FT419,626^A0I,25,24^FH\\^FDREAD^FS" +
                "^FT317,611^A0I,23,24^FH\\^FDWokingham^FS" +
                "^FT317,671^A0I,23,24^FH\\^FD26 Blackberry Gardens ^FS^" +
                "FT317,643^A0I,23,24^FH\\^FDWinnersh^FS^" +
                "FT318,700^A0I,23,24^FH\\^FDTim Stoddard^FS^" +
                "FT447,362^A0I,23,19^FH\\^FDShopping online?^FS" +
                "^FT447,334^A0I,23,19^FH\\^FDClick & Collect your online purchases^FS" +
                "^FT447,306^A0I,23,19^FH\\^FDto your local Collect+ store^FS" +
                "^FT447,278^A0I,23,19^FH\\^FDwww.collectplus.co.uk/our-services^FS" +
                "^FT110,321^A0I,62,64^FH\\^FD72^FS" +
                "^FT108,274^A0I,25,24^FH\\^FD72 HR^FS" +
                "^FT115,506^A0I,54,55^FH\\^FD02H^FS" +
                " FT87,780^A0I,28,28^FH\\^FDLight^FS" +
                "^FT87,746^A0I,28,28^FH\\^FD<2Kg^FS" +
                "^BY2,3,161^FT440,53^BCI,,Y,N" +
                "^FD>:8CBBG1279258A020^FS" +
                "^FO252,454^GB0,142,4^FS" +
                "^FO392,451^GB0,57,4^FS" +
                "^FO321,453^GB0,58,4^FS" +
                "^FT422,541^A0I,28,28^FH\\^FDRG41 5RN^FS" +
                "^FT381,476^A0I,20,19^FH\\^FDOf: 1^FS" +
                "^FT450,477^A0I,20,19^FH\\^FDBox: 1^FS" +
                "^FT312,488^A0I,14,14^FH\\^FDWeight:^FS" +
                "^FT309,466^A0I,17,16^FH\\^FD<2Kg^FS" +
                "^FT247,484^A0I,20,16^FH\\^FDCreation Date:^FS" +
                "^FT244,465^A0I,14,14^FH\\^FD31-May-2016^FS" +
                "^LRY^FO0,731^GB137,0,76^FS^LRN" +
                "^PQ1,0,1,Y^XZ";
    }


    public String GenerateReciept() {
        return "^XA~TA000~JSN^LT0^MNW^MTD^PON^PMN^LH0,0^JMA^PR5,5~SD15^JUS^LRN^CI0^MMT^PW456^LL0807^LS0^FO96,704^GFA,03456,03456,00036,:Z64:" +
                "eJztlE+K2zAUh59iOg6hWO2ia8/ah5B9hJQ2616hJ7AEsxlyioHu5hLVUdQbaNGFKa7U92Rb0uRPM6tCwQ8CL8mXz08/SQFYa621/k3JDqC0sec/T4HCln4A+O6AawGFtyBd9vU7eA/ALPcjMO8V1xJqP2Krc0lLnso52DinK/QI7J0zidgTQx50eG/JI70HT89e6n7yBMa1lubx2OcMWzwgTW35gB7mQWpbjsmj5nmAm1YVyIhihNpolhb2Zva8xXAksBE9aOBGgYzM7mny0Asbh55yCGG2kfkwe5AZ7gB68lhALGO2amEYLaTHfCoDG+yFWphD9DBaCHm4Dnwdg27iPECMwHlmhkfm8BETs2xh5OwZc8/ugZjoaScPXPXgPvnJQ3ximm/JMzEXPJ/OPPU0T2K2D94tnt45R/lMnip59l3bwek8L9eV5TOIOR8zZXgx53beL4N7l+/XTsV9HyZGnDHJU85nI+w7vk/nZ5lnC9zWis6YQAmdMXfmqaC2tQ7nEBdV/9B4qmM+YZ7mF1fCVOZuwHyYV722m/HEIz3eHV3+/hLuF8ak87szzRMYVYY7KOY7aC956L5r8rQnDObTAH7CyR3+EwRQj23uAfKU9DvpgTzUy7QsaJ6bprnHnClWTJg80OPJMIk5UO1ZfDh5sBhktX3EUkVk6P/ntIKnO/O8qOYZC4qMueKBv3t2x+Px8RWezzc8NM/TKzxfb3goH5Ux1/LpbnjWWmut/77+ACbqh/U=:99B1" +
                "^FT405,670^A0I,28,28^FH\\^FDparcels made easy  51425^FS" +
                "^FT404,631^A0I,28,28^FH\\^FDCAMBRIDGE SUPERSTORE^FS" +
                "^FT405,594^A0I,28,28^FH\\^FD24 HOUR EXPRESS^FS" +
                "^FT431,524^A0I,28,28^FH\\^FDSN 003045882^FS" +
                "^FT164,524^A0I,28,28^FH\\^FDSPR 34628^FS" +
                "^FT429,446^A0I,28,28^FH\\^FDThank you for sending^FS" +
                "^FT429,412^A0I,28,28^FH\\^FDyour parcel(s) with^FS" +
                "^FT429,378^A0I,28,28^FH\\^FDCollect+ at 12:38^FS" +
                "^FT429,344^A0I,28,28^FH\\^FDon 31/05/16^FS" +
                "^FT431,290^A0I,17,31^FH\\^FDPLEASE KEEP RECEIPT^FS" +
                "^FT431,269^A0I,17,31^FH\\^FDFOR PROOF OF TRANSACTION^FS" +
                "^FT433,199^A0I,34,33^FH\\^FDCUSTOMER PARCEL RECEIPT^FS" +
                "^FT428,149^A0I,22,28^FH\\^FDParcel = 8AMW900000018092^FS" +
                "^FT428,111^A0I,28,28^FH\\^FDTracking  NHATCCB^FS" +
                "^FT427,55^A0I,23,24^FH\\^FDwww.collectplus.co.uk^FS" +
                "^FT427,30^A0I,20,21^FH\\^FDCUSTOMER COPY^FS" +
                "^PQ1,0,1,Y^XZ";
    }



}
