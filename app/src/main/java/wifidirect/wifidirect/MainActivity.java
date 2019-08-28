package wifidirect.wifidirect;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class MainActivity extends AppCompatActivity {

    // Use this button to turn on or off the WiFi
    Button btnonoff;

    // Use this button to discover nearby peers
    Button btnDiscover;

    // Use this button to send messages
    Button btnSend;

    // Use this button to change private/group mode
    ToggleButton btnPrivateGroup;

    // Use this ListView to show the neerby peers
    ListView listView;

    // Use this textview to show the recieved message
    static TextView read_msg_box;

    // Use this textview to show connection status
    TextView ConnectionStatus;

    // Use this EditText to write message
    EditText writeMsg;

    ProgressBar discoveryProgressbar;
    SwipeRefreshLayout swipeRefreshLayout;

    boolean isGroup = false;

    WifiManager wifiManager;
    WifiP2pManager manager;
    WifiP2pManager.Channel channel;
    BroadcastReceiver Receiver;
    IntentFilter intentFilter;
    List<WifiP2pDevice> peers = new ArrayList<>();

    // Use this array to show Device name in ListView
    String[] DeviceNameArray;

    // Use this array to connect to device
    WifiP2pDevice[] deviceArray;

    static final int MESSAGE_READ = 1;

    Server server = null;
    Client client = null;

    static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the neseccary variables
        Initialize();

        // Location permission is necessary to use Wifi Direct
        // for android 6 and later
        checkLocationPermission();

        Listener();
    }

    // Register the Broadcast Reciever
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(Receiver, intentFilter);
    }

    // UnRegister the Broadcast Reciever
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(Receiver);
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != getPackageManager().PERMISSION_GRANTED) {
            // the permission aren’t granted and  use requestPermissions to ask the user to grant.
            // The response from the user is captured in the onRequestPermissionsResult callback.
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == getPackageManager().PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this,
                            "permission was granted",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this,
                            "permission denied",
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void Listener() {
        // Check current Status of WiFi
        //if WiFi is on, set it off. Otherwise set it on.
        btnonoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wifiManager.isWifiEnabled()) {
                    wifiManager.setWifiEnabled(false);
                    btnonoff.setText(getString(R.string.WiFi_on_Text));
                } else {
                    wifiManager.setWifiEnabled(true);
                    btnonoff.setText(getString(R.string.WiFi_Off_Text));
                }

            }
        });

        // Discover nearby available peers
        btnDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discoveryProgressbar.setVisibility(View.VISIBLE);
                discoveryProgressbar.setProgress(20);
                discoveryProgressbar.setMax(50);
                manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        // Discovery Started successfully and
                        // the system broadcasts the WIFI_P2P_PEERS_CHANGED_ACTION intent
                        // which you can listen for in a broadcast receiver to obtain a list of peers
                        ConnectionStatus.setText(getString(R.string.Discovery_Success));
                    }

                    @Override
                    public void onFailure(int reason) {
                        //Discovery not started
                        ConnectionStatus.setText(getString(R.string.Discovery_Fail));
                    }
                });
            }
        });

        // Change the mode of application
        // there is two mode:
        // 1. Private mode: Send and recieve messages between two people
        // 2. Group mode: Send and recieve messages between more than two peoples
        btnPrivateGroup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(getApplicationContext(), "Group mode", Toast.LENGTH_SHORT).show();
                    btnPrivateGroup.setTextOn("Group mode");
                    isGroup = true;

                    manager.createGroup(channel, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(getApplicationContext(), "Group is created successfully", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onFailure(int reason) {
                            Toast.makeText(getApplicationContext(), "Create group is failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Privatie mode", Toast.LENGTH_SHORT).show();
                    btnPrivateGroup.setTextOff("Private mode");
                    isGroup = false;
                }
            }
        });

        // Connect to peer
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, final View view, int i, long id) {

                //if(!isGroup)
                //{
                final WifiP2pDevice device = deviceArray[i];
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;

                manager.connect(channel, config, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getApplicationContext(), "Connected to " + device.deviceName, Toast.LENGTH_SHORT).show();

                        ((TextView) view).setText(device.deviceName + "\n connected");
                    }

                    @Override
                    public void onFailure(int reason) {
                        Toast.makeText(getApplicationContext(), " Not Connected to " + device.deviceName, Toast.LENGTH_SHORT).show();
                    }
                });
                //}
//                else
//                {
//                    manager.createGroup(channel, new WifiP2pManager.ActionListener() {
//                        @Override
//                        public void onSuccess() {
//                            Toast.makeText(getApplicationContext(),"Group is created successfully",Toast.LENGTH_SHORT);
//                        }
//
//                        @Override
//                        public void onFailure(int reason) {
//                            Toast.makeText(getApplicationContext(),"Create Group is failed",Toast.LENGTH_SHORT);
//                        }
//                    });
//
//                }
            }
        });

        // Send Message
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = (String) btnSend.getText();

                if (server != null) {
                    CompletableFuture.runAsync(() -> {
                        server.service.BroadCast(msg);
                    });
                }
                if (client != null) {

                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                discoveryProgressbar.setVisibility(View.VISIBLE);
                discoveryProgressbar.setProgress(20);
                discoveryProgressbar.setMax(50);
                manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        // Discovery Started successfully and
                        // the system broadcasts the WIFI_P2P_PEERS_CHANGED_ACTION intent
                        // which you can listen for in a broadcast receiver to obtain a list of peers
                        ConnectionStatus.setText(getString(R.string.Discovery_Success));
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(int reason) {
                        //Discovery not started
                        ConnectionStatus.setText(getString(R.string.Discovery_Fail));
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }

    private void Initialize() {
        // Button to enable and disable WiFi
        btnonoff = findViewById(R.id.onoff);

        // Button to discover peers
        btnDiscover = findViewById(R.id.discover);

        // Button to send message
        btnSend = findViewById(R.id.sendButton);

        // Button to change private mode and group mode
        btnPrivateGroup = findViewById(R.id.privateGroupbtn);

        // listView to Show all available peers
        listView = findViewById(R.id.peerListView);

        // TextView to show message
        read_msg_box = findViewById(R.id.readMsg);

        // TextView to show connection status
        ConnectionStatus = findViewById(R.id.connectionStatus);

        // EditText to write meassage
        writeMsg = findViewById(R.id.writeMsg);

        discoveryProgressbar = findViewById(R.id.discoveryProgress);
        discoveryProgressbar.setVisibility(View.GONE);

        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setRefreshing(false);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        // This class provides the API for managing  Wifi peer to peer connectivity
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);

        // A channel  that connects the application to the WiFi p2p framework
        channel = manager.initialize(this, getMainLooper(), null);

        Receiver = new WifiDirectBroadcastReceiver(manager, channel, this);

        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        Toast.makeText(getApplicationContext(), "startRegistration", Toast.LENGTH_SHORT).show();
        startRegistration();
    }

    //  Fetch the list of peers.
    WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            // Check Wether the available peers change or not
            if (!peerList.getDeviceList().equals(peers)) {
                peers.clear();
                peers.addAll(peerList.getDeviceList());

                DeviceNameArray = new String[peerList.getDeviceList().size()];
                deviceArray = new WifiP2pDevice[peerList.getDeviceList().size()];

                int index = 0;

                for (WifiP2pDevice device : peerList.getDeviceList()) {
                    DeviceNameArray[index] = "maryam" + device.deviceName;
                    deviceArray[index] = device;
                    index++;
                }

                ArrayAdapter<String> adapter =
                        new ArrayAdapter<>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, DeviceNameArray);
                listView.setAdapter(adapter);
                discoveryProgressbar.setVisibility(View.GONE);
            }

            if (peers.size() == 0) {
                Toast.makeText(getApplicationContext(), "No Device Found", Toast.LENGTH_SHORT).show();
                discoveryProgressbar.setVisibility(View.GONE);
            }
        }
    };

    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            //final InetAddress groupOwnerAddress = wifiP2pInfo.groupOwnerAddress;

            if (wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
                ConnectionStatus.setText(getString(R.string.Host));
                server = new Server();
                CompletableFuture.runAsync(() -> {
                        server.Start();
                });

            } else if (wifiP2pInfo.groupFormed) {
                ConnectionStatus.setText(getString(R.string.client));
                client = new Client();
                CompletableFuture.runAsync(() -> {
                       client.Start();
                });

            }
        }
    };

    static Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_READ:
                    byte[] readBuff = (byte[]) msg.obj;
                    String tempMSg = new String(readBuff, 0, msg.arg1);
                    read_msg_box.setText(tempMSg);
                    break;
            }
            return true;
        }
    });

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**********************************/

    public static final String SERVICE_INSTANCE = "_wifidemotest";
    public static final String SERVICE_REG_TYPE = "_presence._tcp";

    private void startRegistration() {
        Map record = new HashMap();
        record.put("listenport", String.valueOf(1234));
        record.put("buddyname", Build.MANUFACTURER);
        record.put("available", "visible");

        WifiP2pDnsSdServiceInfo serviceInfo =
                WifiP2pDnsSdServiceInfo.newInstance(SERVICE_INSTANCE, SERVICE_REG_TYPE, record);

        manager.addLocalService(channel, serviceInfo, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "addLocalService successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int arg0) {
                Toast.makeText(getApplicationContext(), "addLocalService failed", Toast.LENGTH_SHORT).show();
            }
        });
        discoverService();
    }

    private void discoverService() {

        manager.setDnsSdResponseListeners(channel,
                new WifiP2pManager.DnsSdServiceResponseListener() {
                    @Override
                    public void onDnsSdServiceAvailable(String instanceName,
                                                        String registrationType, WifiP2pDevice srcDevice) {
                        // A service has been discovered. Is this our app?
                        if (instanceName.equalsIgnoreCase(SERVICE_INSTANCE)) {
                            // update the UI and add the item the discovered
                            // device.
                            DeviceNameArray = new String[1];
                            DeviceNameArray[0] = "NSD : " + srcDevice.deviceName;
                            ArrayAdapter<String> adapter =
                                    new ArrayAdapter<>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, DeviceNameArray);
                            listView.setAdapter(adapter);
                            }
                        }
                    }
                , new WifiP2pManager.DnsSdTxtRecordListener() {

                    @Override
                    public void onDnsSdTxtRecordAvailable(
                            String fullDomainName, Map<String, String> record,
                            WifiP2pDevice device) {
                    }
                });

        WifiP2pDnsSdServiceRequest serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        manager.addServiceRequest(channel, serviceRequest,
                new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        ConnectionStatus.setText("addServiceRequest successfully");

                    }
                    @Override
                    public void onFailure(int arg0) {
                        ConnectionStatus.setText("addServiceRequest failed");
                    }
                });

        manager.discoverServices(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                ConnectionStatus.setText("discoverServices successfully");
            }
            @Override
            public void onFailure(int arg0) {
                ConnectionStatus.setText("discoverServices failed");
            }
        });
    }
}
