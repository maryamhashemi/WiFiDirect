package AsyncSocket;

import java.nio.channels.AsynchronousSocketChannel;

public interface IWiFiNetService {
    public void Send(Device device, String Msg);
    public void MsgRecievedDirect(Device device);
    public void MsgRecievedBC();
    public void BroadCast(String Msg);
    public void HandleBroadCast();
    public void StartWrite(AsynchronousSocketChannel channel, String Msg);
    public String StartRead(AsynchronousSocketChannel channel);
}
