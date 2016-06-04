package zebra.collectpluscc;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

public class SendNewParcel extends Activity   {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_new_parcel);

        WebView myWebView = (WebView) findViewById(R.id.webView);

        myWebView.setWebViewClient(new WebViewClient());

       myWebView.getSettings().setJavaScriptEnabled(true);


           myWebView.loadUrl("https://www.collectplus.co.uk/send-a-parcel");


    }

    public void ButtonClicked(View view) {
        switch (view.getId()) {
            case R.id.Home:
                startActivity(new Intent(this, Home.class));
                overridePendingTransition(0, 0);
                finish();
        }
    }
}