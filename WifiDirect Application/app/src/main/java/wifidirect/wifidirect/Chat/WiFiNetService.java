package wifidirect.wifidirect.Chat;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Vector;

public class WiFiNetService implements IWiFiNetService {
    private static final String TAG = "MyApp: WiFiNetService: ";
    Vector<Device> DeviceList = new Vector<>();

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
        ByteBuffer buf = ByteBuffer.allocate(2048);
        buf.put(Msg.getBytes());
        buf.flip();
        device.channel.write(buf, device.channel,
                new CompletionHandler<Integer, AsynchronousSocketChannel>() {
                    @Override
                    public void completed(Integer result, AsynchronousSocketChannel channel) {
                        //after message written
                        //NOTHING TO DO
                        Log.d(TAG,"me : " + Msg);
                    }

                    @Override
                    public void failed(Throwable exc, AsynchronousSocketChannel channel) {
                        Log.d(TAG,"Fail to write the message.");
                    }
                });
    }

    @Override
    public void StartRead(final Device device) {
        final ByteBuffer buf = ByteBuffer.allocate(2048);

        device.channel.read(buf, device.channel,
                new CompletionHandler<Integer, AsynchronousSocketChannel>() {
                    @Override
                    public void completed(Integer result, AsynchronousSocketChannel channel) {
                        String msg = new String(buf.array());
                        Log.d(TAG,device.name + " : " + msg);
                        msgReceivedHandler.MsgReceived(device, msg);
                        StartRead(device);
                    }

                    @Override
                    public void failed(Throwable exc, AsynchronousSocketChannel channel) {
                        Log.d(TAG,"Fail to read message.");
                    }

                });
    }

    @Override
    public void ReceiveBroadcast(final Device device) {
        final ByteBuffer buf = ByteBuffer.allocate(2048);
        //final String[] Msg = {new String()};
        device.channel.read(buf, device.channel,
                new CompletionHandler<Integer, AsynchronousSocketChannel>() {
                    @Override
                    public void completed(Integer result, AsynchronousSocketChannel channel) {
                        String msg = new String(buf.array());
                        Log.d(TAG,device.name + " : " + msg);
                        msgReceivedHandler.MsgReceived(device, msg);
                        BroadCast(msg);
                        ReceiveBroadcast(device);
                    }

                    @Override
                    public void failed(Throwable exc, AsynchronousSocketChannel channel) {
                        Log.d(TAG,"Fail to read message.");
                    }

                });
    }

    public class MsgReceivedHandler implements IMsgReceived {
        @Override
        public void MsgReceived(Device device, String msg) {
            System.out.println(msg);
        }

    }
}


