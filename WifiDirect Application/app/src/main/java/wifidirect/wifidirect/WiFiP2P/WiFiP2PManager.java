package wifidirect.wifidirect.WiFiP2P;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.*;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import wifidirect.wifidirect.Chat.Client;
import wifidirect.wifidirect.Chat.Server;
import wifidirect.wifidirect.MainActivity;

import static android.os.Looper.getMainLooper;

public class WiFiP2PManager {
    private static final String TAG = "MyApp: WiFiP2PManager: ";
    private static final String SERVICE_INSTANCE = "_wifidemotest";
    private static final String SERVICE_REG_TYPE = "_presence._tcp";

    private WifiManager wifiManager;
    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel channel;

    private MainActivity mainActivity;
    public BroadcastReceiver receiver;
    public IntentFilter intentFilter;

    /**
     * @param mainActivity
     */
    public WiFiP2PManager(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        Context context = mainActivity.getApplicationContext();

        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        // This class provides the API for managing  WifiP2p connectivity
        wifiP2pManager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);

        // A channel  that connects the application to the WiFi p2p framework
        channel = wifiP2pManager.initialize(context, getMainLooper(), null);

        receiver = new WiFiP2PBroadcastReceiver(wifiP2pManager, channel, mainActivity);

        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    /**
     * Check current Status of WiFi
     *
     * @return if WiFi is on, return false. Otherwise return true.
     */
    public boolean TurnOnWiFi() {
        if (wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
            return false;
        } else {
            wifiManager.setWifiEnabled(true);
            return true;
        }
    }

    /**
     * Initiate peer discovery.
     */
    public void Discover() {

        wifiP2pManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            /**
             * Discovery Started successfully and
             * the system broadcasts the WIFI_P2P_PEERS_CHANGED_ACTION intent
             * which you can listen it in a broadcast receiver to obtain a list of peers
             */
            @Override
            public void onSuccess() {
                Log.d(TAG, "Discovery started successfully");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "Discovery failed.resason :" + reason);
            }
        });
    }

    /**
     * Create a p2p group with the current device as the group owner.
     */
    public void CreateGroup() {
        wifiP2pManager.createGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Group is created successfully");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "Create group is failed");
            }
        });
    }

    /**
     * Start a p2p connection to a device with the specified configuration.
     *
     * @param config
     */
    public void Connect(WifiP2pConfig config) {
        wifiP2pManager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Connected " + config.deviceAddress);
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "Not Connected to " + config.deviceAddress);
            }
        });
    }

    /**
     * Cancel any ongoing p2p group negotiation
     */
    private void forcedCancelConnect() {
        wifiP2pManager.cancelConnect(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "CancelConnect success");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "forcedCancelConnect failed, reason: " + reason);
            }
        });
    }

    /**
     * Fetch the list of peers.
     */
    WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            mainActivity.UpdatePeerList(peerList);
        }
    };

    /**
     *
     */
    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            if (wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
                //ToDo: Running Server
                Log.d(TAG, "This Device is group owner.");
                Server server = new Server();
                server.Start();

            } else if (wifiP2pInfo.groupFormed) {
                //ToDo: Running Client
                Log.d(TAG, "This Device is client.");
                Client client =  new Client();
                //client.Start();
            }
        }
    };


    /**
     *
     */
    private void startRegistration() {
        Map record = new HashMap();
        record.put("listenport", String.valueOf(1234));
        record.put("buddyname", Build.MANUFACTURER);
        record.put("available", "visible");

        WifiP2pDnsSdServiceInfo serviceInfo = WifiP2pDnsSdServiceInfo.newInstance(SERVICE_INSTANCE, SERVICE_REG_TYPE, record);

        wifiP2pManager.addLocalService(channel, serviceInfo, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "addLocalService successfully");
            }

            @Override
            public void onFailure(int arg0) {
                Log.d(TAG, "addLocalService failed");
            }
        });
    }

    /**
     *
     */
    private void DnsSdResponseListeners() {
        wifiP2pManager.setDnsSdResponseListeners(channel, new WifiP2pManager.DnsSdServiceResponseListener() {
                    @Override
                    public void onDnsSdServiceAvailable(String instanceName,
                                                        String registrationType, WifiP2pDevice srcDevice) {
                        // A service has been discovered. Is this our app?
                        if (instanceName.equalsIgnoreCase(SERVICE_INSTANCE)) {
                            //ToDo: Update nearby peers list
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
    }

    /**
     *
     */
    private void AddServiceRequest() {
        WifiP2pDnsSdServiceRequest serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        wifiP2pManager.addServiceRequest(channel, serviceRequest,
                new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "addServiceRequest successfully");
                    }

                    @Override
                    public void onFailure(int arg0) {
                        Log.d(TAG, "addServiceRequest failed");
                    }
                });
    }

    /**
     *
     */
    private void discoverService() {
        startRegistration();
        DnsSdResponseListeners();
        AddServiceRequest();
        wifiP2pManager.discoverServices(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "discoverServices successfully");
            }

            @Override
            public void onFailure(int arg0) {
                Log.d(TAG, "discoverServices failed");
            }
        });
    }
}
