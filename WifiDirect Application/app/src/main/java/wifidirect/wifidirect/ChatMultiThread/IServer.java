package wifidirect.wifidirect.ChatMultiThread;

public interface IServer {
    void Accept();
    void Send(String msg);
}
