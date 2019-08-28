package AsyncSocket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Vector;


public class WiFiNetService implements IWiFiNetService {

    Vector<Device> DeviceList = new Vector<Device>();

    @Override
    public void Send(Device device, String Msg) {
        StartWrite(device, Msg);
    }

    @Override
    public void Receive(Device device) {
        StartRead(device);
    }

    private IMsgReceived msgReceivedHandler = new MsgReceivedHandler();
    //public IMsgReceived bcMsgReceivedHandler = new BCMsgReceivedHandler();

    @Override
    public void BroadCast(String Msg) {
        for (Device device : DeviceList) {
            StartWrite(device, Msg);
        }
    }

    @Override
    public void StartWrite(Device device, String Msg) {
        ByteBuffer buf = ByteBuffer.allocate(204800);
        buf.put(Msg.getBytes());
        buf.flip();
        device.channel.write(buf, device.channel,
                new CompletionHandler<Integer, AsynchronousSocketChannel>() {
                    @Override
                    public void completed(Integer result, AsynchronousSocketChannel channel) {
                        //after message written
                        //NOTHING TO DO
                        System.out.println("me : " + Msg);
                    }

                    @Override
                    public void failed(Throwable exc, AsynchronousSocketChannel channel) {
                        System.out.println("Fail to write the message.");
                    }
                });
    }

    @Override
    public void StartRead(Device device) {
        ByteBuffer buf = ByteBuffer.allocate(2048);
        //final String[] Msg = {new String()};
        device.channel.read(buf, device.channel,
                new CompletionHandler<Integer, AsynchronousSocketChannel>() {
                    @Override
                    public void completed(Integer result, AsynchronousSocketChannel channel) {
                        String msg = new String(buf.array());
                        msgReceivedHandler.MsgReceived(device, msg);
                        BroadCast(msg);
                        StartRead(device);
                    }

                    @Override
                    public void failed(Throwable exc, AsynchronousSocketChannel channel) {
                        System.out.println("fail to read message.");
                    }

                });
    }

    @Override
    public void ReceiveBroadcast(Device device) {
        ByteBuffer buf = ByteBuffer.allocate(2048);
        //final String[] Msg = {new String()};
        device.channel.read(buf, device.channel,
                new CompletionHandler<Integer, AsynchronousSocketChannel>() {
                    @Override
                    public void completed(Integer result, AsynchronousSocketChannel channel) {
                        String msg = new String(buf.array());
                        msgReceivedHandler.MsgReceived(device, msg);
                        StartRead(device);
                    }

                    @Override
                    public void failed(Throwable exc, AsynchronousSocketChannel channel) {
                        System.out.println("fail to read message.");
                    }

                });
    }

    public class MsgReceivedHandler implements IMsgReceived {
        @Override
        public void MsgReceived(Device device, String msg) {
            System.out.println(msg);
        }

    }

    public void ReceiveRespondUppercase(Device device) {
        ByteBuffer buf = ByteBuffer.allocate(2048);
        device.channel.read(buf, device.channel,
                new CompletionHandler<Integer, AsynchronousSocketChannel>() {
                    @Override
                    public void completed(Integer result, AsynchronousSocketChannel channel) {
                        String msg = new String(buf.array());
                        msgReceivedHandler.MsgReceived(device, device.name + " : " + msg);
                        Send(device, msg.toUpperCase());
                        ReceiveRespondUppercase(device);
                    }

                    @Override
                    public void failed(Throwable exc, AsynchronousSocketChannel channel) {
                        System.out.println("fail to ReceiveRespondUppercase.");
                    }

                });
    }

    public String receive(Device device) {
        ByteBuffer buf = ByteBuffer.allocate(2048);
        final String[] Msg = {new String()};
        device.channel.read(buf, device.channel,
                new CompletionHandler<Integer, AsynchronousSocketChannel>() {
                    @Override
                    public void completed(Integer result, AsynchronousSocketChannel channel) {
                        Msg[0] = new String(buf.array());
                        msgReceivedHandler.MsgReceived(device, device.name +" : "+Msg[0]);
                        receive(device);
                    }

                    @Override
                    public void failed(Throwable exc, AsynchronousSocketChannel channel) {
                        System.out.println("fail to read message.");
                    }

                });

        return Msg[0];
    }
}


