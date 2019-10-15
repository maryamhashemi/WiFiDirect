package wifidirect.wifidirect;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class StartActivity extends AppCompatActivity {

    private static final String TAG = "MyApp: StartActivity: ";
    AnimationDrawable WiFiAnimation;
    Button btnTurnOn;
    WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        ImageView imageView = findViewById(R.id.WiFiImage);
        imageView.setBackgroundResource(R.drawable.animated_wifi);
        WiFiAnimation = (AnimationDrawable) imageView.getBackground();

        start();
    }

    /**
     * Initialize view objects and check weather wifi is enabled or not.
     * if wifi is enabled, the main activity starts.
     */
    private void start(){
        btnTurnOn = findViewById(R.id.TurnOn);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        Intent intent = new Intent(this, MainActivity.class);
        if (wifiManager.isWifiEnabled()) {
            startActivity(intent);
        }
    }


    /**
     * Use this method when btnTurnOn clicks.
     * @param view refers to view that was clicked.
     */
    public void TurnOn(View view){
        Intent intent = new Intent(this, MainActivity.class);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
            startActivity(intent);
        }
    }

    /**
     * Called when the current Window of the activity gains or loses focus.
     * @param hasFocus Whether the window of this activity has focus.
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        WiFiAnimation.start();
    }
}
