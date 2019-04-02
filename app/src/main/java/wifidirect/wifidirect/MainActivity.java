package wifidirect.wifidirect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button btnonoff, btnDiscover, btnSend;
    ListView listView;
    TextView read_msg_box, ConnenctionStatus;
    EditText writeMsg;

    WifiManager wifiManager;

    WifiP2pManager manager;
    WifiP2pManager.Channel channel;

    BroadcastReceiver Receiver;
    IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Initialize();
        Listener();
    }

    // Register the Broadcast Reciever
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(Receiver,intentFilter);
    }

    // UnRegister the Broadcast Reciever
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(Receiver);
    }

    private void Listener()
    {
        btnonoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Check current Status of WiFi
//                if WiFi is on, set it off. Otherwise set it on.
                if (wifiManager.isWifiEnabled())
                {
                    wifiManager.setWifiEnabled(false);
                    btnonoff.setText("on");
                }
                else
                {
                    wifiManager.setWifiEnabled(true);
                    btnonoff.setText("off");
                }

            }
        });
    }

    private void Initialize()
    {
//        Button to enable and disable WiFi
        btnonoff = (Button)findViewById(R.id.onoff);

//        Button to discover peers
        btnDiscover =(Button)findViewById(R.id.discover);

//        Button to send message
        btnSend = (Button)findViewById(R.id.sendButton);

//        listView to Show all available peers
        listView = (ListView)findViewById(R.id.peerListView);

//        TextView to show message
        read_msg_box =(TextView)findViewById(R.id.readMsg);

//        TextView to show connection status
        ConnenctionStatus =(TextView)findViewById(R.id.connectionStatus);

//        EditText to write meassage
        writeMsg = (EditText)findViewById(R.id.writeMsg);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

//        This class provides the API for managing  Wifi peer to peer connectivity
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);

//        A channel  that connects the application to the WiFi p2p framework
        channel = manager.initialize(this,getMainLooper(),null);

        Receiver = new WifiDirectBroadcastReceiver(manager,channel,this);
        intentFilter = new IntentFilter();

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }
}
