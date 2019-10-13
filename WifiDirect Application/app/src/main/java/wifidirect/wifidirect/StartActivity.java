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
     * ToDo
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
     * ToDo
     * @param view
     */
    public void TurnOn(View view){
        Intent intent = new Intent(this, MainActivity.class);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
            startActivity(intent);
        }
    }

    /**
     * ToDo
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        WiFiAnimation.start();
    }
}
