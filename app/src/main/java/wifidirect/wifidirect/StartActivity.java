package wifidirect.wifidirect;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class StartActivity extends AppCompatActivity {

    AnimationDrawable WiFiAnimation;
    Button btnTurnOn;
    WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        ImageView imageView = (ImageView) findViewById(R.id.WiFiImage);
        imageView.setBackgroundResource(R.drawable.animated_wifi);
        WiFiAnimation = (AnimationDrawable) imageView.getBackground();

        start();
    }

    private void start(){
        btnTurnOn = findViewById(R.id.TurnOn);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }


    public void TurnOn(View view){
        Intent intent = new Intent(this, MainActivity.class);
        if (wifiManager.isWifiEnabled()) {
            startActivity(intent);
        } else {
            wifiManager.setWifiEnabled(true);
            startActivity(intent);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        WiFiAnimation.start();
    }
}
