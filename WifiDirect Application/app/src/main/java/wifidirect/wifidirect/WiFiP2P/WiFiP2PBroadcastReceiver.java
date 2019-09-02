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
 * A BroadcastReceiver that notifies of important Wi-Fi p2p events.
 */
public class WiFiP2PBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "MyApp: WiFiP2PBroadcastReceiver: ";
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private MainActivity mainActivity;
    private  WifiP2pDevice myDevice;

    public WiFiP2PBroadcastReceiver(WifiP2pManager manager,
                                       WifiP2pManager.Channel channel,
                                       MainActivity mainActivity)
    {
        super();
        this.manager = manager;
        this.channel = channel;
        this.mainActivity = mainActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        // Check the current action of WiFi
        String action = intent.getAction();
        if(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action))
        {
            //Broadcast intent action to indicate whether Wi-Fi p2p is enabled or disabled.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE,-1);

            if(state == WifiP2pManager.WIFI_P2P_STATE_ENABLED)
            {
                Toast.makeText(context,"Wifi is On",Toast.LENGTH_SHORT).show();
                Log.d(TAG,"Wifi is ON");
            }
            else
            {
                Toast.makeText(context,"Wifi is OFF",Toast.LENGTH_SHORT).show();
                Log.d(TAG,"Wifi is OFF");
            }
        }
        else if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action))
        {
            // Broadcast intent action indicating that the available peer list has changed.
            // Call WifiP2pManager.requestPeers() to get a list of current peers
            // request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()
            if(manager != null)
            {
                manager.requestPeers(channel,mainActivity.manager.peerListListener);
            }
        }
        else if(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action))
        {
            // Broadcast intent action indicating that the state of Wi-Fi p2p connectivity has changed.
            // Respond to new connection or disconnection
            if(manager == null)
            {
                return;
            }
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if(networkInfo.isConnected())
            {
                manager.requestConnectionInfo(channel, mainActivity.manager.connectionInfoListener);

            }
            else
            {
                Log.d(TAG, "Device is disconnected");
            }
        }
        else if(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // ToDo:Respond to this Device's Wifi state changing
            //Broadcast intent action indicating that this device details have changed.
            myDevice = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
            mainActivity.MyDeviceName.setText(myDevice.deviceName);
            mainActivity.MyDeviceStatus.setText(getP2pDeviceStatus(myDevice.status));
            mainActivity.MyIpAddress.setText(myDevice.deviceAddress);
        }

    }

    /**
     * Translates a device status code to a readable String status
     * @param deviceStatus
     * @return A readable String device status
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
}
