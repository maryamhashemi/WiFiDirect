package AsyncSocket;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Vector;

public class WiFiNetService implements IWiFiNetService{

    Vector<Device> DeviceList = new Vector<Device>();

    @Override
    public void Send(Device device, String Msg){
        StartWrite(device.channel, Msg);
    }

    @Override
    public void MsgRecievedDirect(Device device){
        StartRead(device.channel);
    }

    @Override
    public void MsgRecievedBC(){
        for(Device device: DeviceList)
        {
            String Msg = StartRead(device.channel);
            BroadCast(Msg);
        }
    }

    @Override
    public void BroadCast(String Msg){
        for(Device device: DeviceList)
        {
            StartWrite(device.channel, Msg);
        }
    }

    @Override
    public void HandleBroadCast(){
        //TODO
    }

    @Override
    public void StartWrite(AsynchronousSocketChannel channel, String Msg){
        ByteBuffer buf = ByteBuffer.allocate(2048);
        buf.put(Msg.getBytes());
        buf.flip();
        channel.write(buf, channel,
                new CompletionHandler<Integer, AsynchronousSocketChannel >() {
                    @Override
                    public void completed(Integer result, AsynchronousSocketChannel channel ) {
                        //after message written
                        //NOTHING TO DO
                    }

                    @Override
                    public void failed(Throwable exc, AsynchronousSocketChannel channel) {
                        System.out.println( "Fail to write the message.");
                    }
                });
    }

    @Override
    public String StartRead(AsynchronousSocketChannel channel)
    {
        ByteBuffer buf = ByteBuffer.allocate(2048);
        final String[] Msg = {new String()};
        channel.read( buf, channel,
                new CompletionHandler<Integer, AsynchronousSocketChannel>(){
                    @Override
                    public void completed(Integer result, AsynchronousSocketChannel channel) {
                        //TODO : show message in console or in UI
                        Msg[0] = new String( buf.array());
                    }

                    @Override
                    public void failed(Throwable exc, AsynchronousSocketChannel channel) {
                        System.out.println( "fail to read message.");
                    }

                });
        return Msg[0];
    }

}
