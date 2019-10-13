package wifidirect.wifidirect.ChatMultiThread;

public interface IClient {
    void Connect();
    void Send(String msg);
    void Receive();
}
