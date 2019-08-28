package wifidirect.wifidirect;

public interface IWiFiNetService {
    void Send(Device device, String Msg);
    void Receive(Device device);
    void BroadCast(String Msg);
    void StartWrite(Device device, String Msg);
    void StartRead(Device device);
    void ReceiveBroadcast(Device device);
}