package wifidirect.wifidirect;

public interface IServer {
    void Accept();
    void Send(String msg);
}