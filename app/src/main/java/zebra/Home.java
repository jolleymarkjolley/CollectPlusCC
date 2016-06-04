package zebra.collectpluscc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Home extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }
    public void ButtonClicked(View view) {
        switch (view.getId()) {
            case R.id.Print:
                startActivity(new Intent(this, printlabel.class));
                overridePendingTransition(0, 0);
                finish();
                break;
            case R.id.Collect:
                startActivity(new Intent(this, Collect.class));
                overridePendingTransition(0, 0);
                finish();
                break;
            case R.id.SendNewParcel:
                startActivity(new Intent(this, SendNewParcel.class));
                overridePendingTransition(0, 0);
                finish();
                break;
            default:
        }
    }

//ytt
}
