package wifidirect.wifidirect.WiFiP2P;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;

import android.util.Log;
import android.widget.Toast;

import wifidirect.wifidirect.MainActivity;

/**
 * This Class notifies important Wi-Fi p2p events.
 */
public class WiFiP2PBroadcastReceiver extends BroadcastReceiver implements IWiFiP2PBroadcastReceiver {

    private static final String TAG = "MyApp: WiFiP2P: WiFiP2PBroadcastReceiver";
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private MainActivity mainActivity;
    private WifiP2pDevice myDevice;

    public WiFiP2PBroadcastReceiver(WifiP2pManager manager,
                                    WifiP2pManager.Channel channel,
                                    MainActivity mainActivity) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.mainActivity = mainActivity;
    }

    /**
     * This method is called when the BroadcastReceiver is receiving an Intent broadcast.
     * This method is always called within the main thread of its process.
     * When it runs on the main thread, you should never perform long-running operations in it
     * (there is a timeout of 10 seconds that the system allows before
     * considering the receiver to be blocked and a candidate to be killed).
     *
     * @param context The Context in which the receiver is running.
     * @param intent  The Intent being received.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        // Check the current action of WiFi
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            CheckWifiEnable(context, intent);
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            CheckPeerlistChange();
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            CheckWiFiConnectivity(intent);
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            CheckDeviceDetail(intent);
        }

    }

    /**
     * Translates a device status code to a readable String status.
     *
     * @param deviceStatus status code of device.
     * @return A readable String device status.
     */
    private static String getP2pDeviceStatus(int deviceStatus) {
        Log.d(TAG, "Peer status :" + deviceStatus);
        switch (deviceStatus) {
            case WifiP2pDevice.AVAILABLE:
                return "Available";
            case WifiP2pDevice.INVITED:
                return "Invited";
            case WifiP2pDevice.CONNECTED:
                return "Connected";
            case WifiP2pDevice.FAILED:
                return "Failed";
            case WifiP2pDevice.UNAVAILABLE:
                return "Unavailable";
            default:
                return "Unknown";
        }
    }

    /**
     * Broadcast intent action to indicate
     * whether Wi-Fi p2p is enabled or disabled.
     *
     * @param context The Context in which the receiver is running.
     * @param intent  The Intent being received.
     */
    @Override
    public void CheckWifiEnable(Context context, Intent intent) {
        int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);

        if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
            Toast.makeText(context, "Wifi is On", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Wifi is ON");
        } else {
            Toast.makeText(context, "Wifi is OFF", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Wifi is OFF");
        }
    }

    /**
     * Broadcast intent action indicating that the available peer list has changed.
     * Call WifiP2pManager.requestPeers() to get a list of current peers
     * request available peers from the wifi p2p manager. This is an
     * asynchronous call and the calling activity is notified with a
     * callback on PeerListListener.onPeersAvailable()
     */
    @Override
    public void CheckPeerlistChange() {
        if (manager != null) {
            manager.requestPeers(channel, mainActivity.manager.peerListListener);
        }
    }

    /**
     * Broadcast intent action indicate that the state of Wi-Fi p2p connectivity has changed.
     * Respond to new connection or disconnection.
     *
     * @param intent The Intent being received.
     */
    @Override
    public void CheckWiFiConnectivity(Intent intent) {

        if (manager == null) {
            return;
        }
        NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
        if (networkInfo.isConnected()) {
            manager.requestConnectionInfo(channel, mainActivity.manager.connectionInfoListener);

        } else {
            Log.d(TAG, "Device is disconnected");
        }
    }

    /**
     * Broadcast intent action indicate that this device details have changed.
     *
     * @param intent The Intent being received.
     */
    @Override
    public void CheckDeviceDetail(Intent intent) {

        myDevice = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
        mainActivity.MyDeviceName.setText(myDevice.deviceName);
        mainActivity.MyDeviceStatus.setText(getP2pDeviceStatus(myDevice.status));
        mainActivity.MyIpAddress.setText(myDevice.deviceAddress);
    }
}
