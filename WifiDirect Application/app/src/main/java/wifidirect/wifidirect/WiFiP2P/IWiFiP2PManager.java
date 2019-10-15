package wifidirect.wifidirect.WiFiP2P;

import android.net.wifi.p2p.WifiP2pConfig;

public interface IWiFiP2PManager {
    boolean TurnOnWiFi();

    void Discover();

    void CreateGroup();

    void Connect(WifiP2pConfig config);

    void forcedCancelConnect();

    void startRegistration();

    void DnsSdResponseListeners();

    void AddServiceRequest();

    void discoverService();
}
