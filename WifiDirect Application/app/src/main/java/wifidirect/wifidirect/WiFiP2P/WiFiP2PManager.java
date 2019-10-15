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

import java.util.HashMap;
import java.util.Map;

import wifidirect.wifidirect.MainActivity;

import static android.os.Looper.getMainLooper;

public class WiFiP2PManager implements IWiFiP2PManager {
    private static final String TAG = "MyApp: WiFiP2P: WiFiP2PManager";
    private static final String SERVICE_INSTANCE = "_wifidemotest";
    private static final String SERVICE_REG_TYPE = "_presence._tcp";

    private WifiManager wifiManager;
    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel channel;

    private MainActivity mainActivity;
    public BroadcastReceiver receiver;
    public IntentFilter intentFilter;

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
    @Override
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
    @Override
    public void Discover() {

        wifiP2pManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            /**
             * Discovery started successfully and
             * the system broadcasts the WIFI_P2P_PEERS_CHANGED_ACTION intent
             * which you can listen it in a BroadcastReceiver to obtain a list of peers
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
     * Start a p2p connection to a device with the specified configuration.
     *
     * @param config representing a Wi-Fi P2p configuration of a device to which we want to connect.
     */
    @Override
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
     * Create a p2p group with the current device as the group owner.
     */
    @Override
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
     * Cancel any ongoing p2p group negotiation.
     */
    @Override
    public void forcedCancelConnect() {
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
     * listening on requested connection info.
     */
    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            if (wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
                // Running Server
                Log.d(TAG, "This Device is group owner.");
                mainActivity.StartChatActivity(wifiP2pInfo.isGroupOwner, wifiP2pInfo.groupOwnerAddress.getHostAddress());

            } else if (wifiP2pInfo.groupFormed && !wifiP2pInfo.isGroupOwner) {
                // Running Client
                Log.d(TAG, "This Device is client.");
                mainActivity.StartChatActivity(wifiP2pInfo.isGroupOwner, wifiP2pInfo.groupOwnerAddress.getHostAddress());
            }
        }
    };

    // We use all of the following functions for service discovery.
    // Using Wi-Fi Peer-to-Peer (P2P) Service Discovery allows you to discover
    // the services of nearby devices directly, without being connected to a network.
    // You can also advertise the services running on your device.
    // These capabilities help you communicate between apps,
    // even when no local network or hotspot is available.

    /**
     * If you're providing a local service, you need to register it for service discovery.
     * Once your local service is registered, the framework automatically responds
     * to service discovery requests from peers.
     * To create a local service:
     * <p>
     * 1. Create a WifiP2pServiceInfo object.
     * 2. Populate it with information about your service.
     * 3. Call addLocalService() to register the local service for service discovery.
     */
    @Override
    public void startRegistration() {
        // Create a string map containing information about your service.
        Map record = new HashMap();
        record.put("listenport", String.valueOf(8888));
        record.put("buddyname", Build.MANUFACTURER);
        record.put("available", "visible");

        // Service information.  Pass it an instance name, service type
        // _protocol._transportlayer , and the map containing
        // information other devices will want once they connect to this one.
        WifiP2pDnsSdServiceInfo serviceInfo =
                WifiP2pDnsSdServiceInfo.newInstance(SERVICE_INSTANCE, SERVICE_REG_TYPE, record);

        // Add the local service, sending the service info, network channel,
        // and listener that will be used to indicate success or failure of
        // the request.
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
     * Android uses callback methods to notify your application of available services,
     * so the first thing to do is set those up.
     * Create a WifiP2pManager.DnsSdTxtRecordListener to listen for incoming records.
     * This record can optionally be broadcast by other devices.
     * To get the service information, create a WifiP2pManager.DnsSdServiceResponseListener.
     * This receives the actual description and connection information.
     * Once both listeners are implemented,
     * add them to the WifiP2pManager using the setDnsSdResponseListeners() method.
     */
    @Override
    public void DnsSdResponseListeners() {
        WifiP2pManager.DnsSdTxtRecordListener txtListener = new WifiP2pManager.DnsSdTxtRecordListener() {
            @Override
            /* Callback includes:
             * fullDomain: full domain name: e.g "printer._ipp._tcp.local."
             * record: TXT record data as a map of key/value pairs.
             * device: The device running the advertised service.
             */
            public void onDnsSdTxtRecordAvailable(
                    String fullDomain, Map<String, String> record, WifiP2pDevice device) {
                Log.d(TAG, "DnsSdTxtRecord available -" + record.toString());
            }
        };
        WifiP2pManager.DnsSdServiceResponseListener servListener =
                new WifiP2pManager.DnsSdServiceResponseListener() {
                    @Override
                    public void onDnsSdServiceAvailable(String instanceName, String registrationType,
                                                        WifiP2pDevice resourceType) {
                        // A service has been discovered. Is this our app?
                        if (instanceName.equalsIgnoreCase(SERVICE_INSTANCE)) {
                            //ToDo: Update nearby peers list
                        }
                    }
                };
        wifiP2pManager.setDnsSdResponseListeners(channel, servListener, txtListener);
    }

    /**
     * Now create a service request and call addServiceRequest().
     * This method also takes a listener to report success or failure.
     */
    @Override
    public void AddServiceRequest() {
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
     * Finally, make the call to discoverServices().
     */
    @Override
    public void discoverService() {
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
