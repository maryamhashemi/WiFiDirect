package simpleNetwork;

public interface IClient {

    void Connect(int port);

    void Send(String msg);

    void Receive();

}
