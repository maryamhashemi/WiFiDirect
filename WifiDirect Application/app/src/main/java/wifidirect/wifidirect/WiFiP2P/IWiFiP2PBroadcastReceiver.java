package wifidirect.wifidirect.WiFiP2P;

import android.content.Context;
import android.content.Intent;

public interface IWiFiP2PBroadcastReceiver {
    void CheckWifiEnable(Context context, Intent intent);

    void CheckPeerListChange();

    void CheckWiFiConnectivity(Intent intent);

    void CheckDeviceDetail(Intent intent);
}
