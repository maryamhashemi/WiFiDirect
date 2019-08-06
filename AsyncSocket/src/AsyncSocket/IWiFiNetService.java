package AsyncSocket;

import java.nio.channels.AsynchronousSocketChannel;

public interface IWiFiNetService {
    public void Send(Device device, String Msg);
    public void Recieve(Device device);
    public void BroadCast(String Msg);
    public void StartWrite(Device device, String Msg);
    public void StartRead(Device device);
    public void RecieveBroadcast(Device device);

}
