package wifidirect.wifidirect;

import android.content.Intent;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

import wifidirect.wifidirect.WiFiP2P.WiFiP2PManager;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MyApp: MainActivity: ";

    public Button btnonoff;
    Button btnDiscover;
    Button btnSend;
    ToggleButton btnPrivateGroup;
    ListView listView;
    static TextView readMsgBox;
    EditText writeMsg;
    ProgressBar discoveryProgressbar;
    SwipeRefreshLayout swipeRefreshLayout;
    public TextView MyDeviceName;
    public TextView MyDeviceStatus;
    public TextView MyIpAddress;
    String[] DeviceNameArray;
    WifiP2pDevice[] deviceArray;
    boolean isGroup = false;
    public WiFiP2PManager manager;
    List<WifiP2pDevice> peers = new ArrayList<>();
    static final int MESSAGE_READ = 1;
    LocationPermission locationPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Initialize();
        Listener();
    }

    /**
     * TODO: JavaDoc
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * Register the Broadcast Receiver
     */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(manager.receiver, manager.intentFilter);
    }

    /**
     * UnRegister the Broadcast Receiver
     */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(manager.receiver);
    }

    /**
     * initialize the necessary variables
     */
    private void Initialize() {
        btnonoff = findViewById(R.id.onoff);
        btnDiscover = findViewById(R.id.discover);
        btnSend = findViewById(R.id.sendButton);
        btnPrivateGroup = findViewById(R.id.privateGroupbtn);
        listView = findViewById(R.id.peerListView);
        readMsgBox = findViewById(R.id.readMsg);
        writeMsg = findViewById(R.id.writeMsg);
        MyDeviceName = findViewById(R.id.MyDeviceName);
        MyDeviceStatus = findViewById(R.id.MyDeviceStatus);
        MyIpAddress = findViewById(R.id.MyIpAddress);

        discoveryProgressbar = findViewById(R.id.discoveryProgress);
        discoveryProgressbar.setVisibility(View.GONE);

        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setRefreshing(false);

        manager = new WiFiP2PManager(this);

        locationPermission = new LocationPermission(this);
        locationPermission.checkLocationPermission();
    }

    private void Listener() {
        /**
         * Change the mode of application
         * there is two mode:
         * 1. Private mode: Send and recieve messages between two people
         * 2. Group mode: Send and recieve messages between more than two peoples
         */
        btnPrivateGroup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.d(TAG, "This device is in group mode");
                    isGroup = true;
                    //manager.CreateGroup();
                } else {
                    Log.d(TAG, "This device is in private mode");
                    isGroup = false;
                }
            }
        });

        /**
         * When tap a device which is listed in listview, connect to it
         */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, final View view, int i, long id) {
                final WifiP2pDevice device = deviceArray[i];
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                manager.Connect(config);
            }
        });

        // Send Message
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = (String) btnSend.getText();
                //ToDo: Send message
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ShowDiscoveryProgressBar();
                manager.Discover();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    /**
     * TODO: JavaDoc
     * @param view
     */
    public void TurnOnWiFi(View view) {
        if (manager.TurnOnWiFi())
            btnonoff.setText(R.string.wifi_off_text);
        else
            btnonoff.setText(R.string.wifi_on_text);
    }

    /**
     * TODO: JavaDoc
     * @param view
     */
    public void Discover(View view) {
        ShowDiscoveryProgressBar();
        manager.Discover();
    }

    /**
     * TODO: JavaDoc
     */
    private void ShowDiscoveryProgressBar() {
        discoveryProgressbar.setVisibility(View.VISIBLE);
        discoveryProgressbar.setProgress(20);
        discoveryProgressbar.setMax(50);
    }

    /**
     * TODO : JavaDoc
     */
    private void HideDiscoveryProgressBar()
    {
        discoveryProgressbar.setVisibility(View.GONE);
    }

    /**
     * TODO: JavaDoc
     * @param peerList
     */
    public void UpdatePeerList(WifiP2pDeviceList peerList) {
        if (!peerList.getDeviceList().equals(peers)) {
            peers.clear();
            peers.addAll(peerList.getDeviceList());

            DeviceNameArray = new String[peerList.getDeviceList().size()];
            deviceArray = new WifiP2pDevice[peerList.getDeviceList().size()];

            int index = 0;

            for (WifiP2pDevice device : peerList.getDeviceList()) {
                DeviceNameArray[index] = device.deviceName;
                deviceArray[index] = device;
                index++;
            }

            ArrayAdapter<String> adapter =
                    new ArrayAdapter<>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, DeviceNameArray);
            listView.setAdapter(adapter);
            HideDiscoveryProgressBar();
        }

        if (peers.size() == 0) {
            Toast.makeText(getApplicationContext(), "No Device Found", Toast.LENGTH_SHORT).show();
            HideDiscoveryProgressBar();
        }
    }

    /**
     * TODO : JavaDoc
     */
    static Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_READ:
                    byte[] readBuff = (byte[]) msg.obj;
                    String tempMSg = new String(readBuff, 0, msg.arg1);
                    readMsgBox.setText(tempMSg);
                    break;
            }
            return true;
        }
    });
}
