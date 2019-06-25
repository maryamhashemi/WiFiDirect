package wifidirect.wifidirect;

public interface IClient {

    void Connect();
    void Send(String msg);
    void Recieve();
}
