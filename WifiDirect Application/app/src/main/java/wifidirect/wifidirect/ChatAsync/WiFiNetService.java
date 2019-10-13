package wifidirect.wifidirect.ChatAsync;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Vector;

/**
 *  This is general class to do 4 main communication work
 *  even the device palys a client role or server role.
 *  these are:
 *  1. Send messages to particular device.
 *  2. Receive messages from particular device.
 *  3. Broadcast messages to devices which are connected to this device.
 *  4. Receive messages from one of the connected device and broadcast them to other devices.
 */

public class WiFiNetService implements IWiFiNetService {
    private static final String TAG = "MyApp: ChatAsync: WiFiNetService";
    Vector<Device> ConnectedDeviceList = new Vector<>();
    private IMsgReceived msgReceivedHandler = new MsgReceivedHandler();

    /**
     * Use this method to send {@param Msg} to {@param device}.
     * @param device is a devices for which the message is to be sent.
     * @param Msg is a message to be sent.
     */
    @Override
    public void Send(Device device, String Msg) {
        StartWrite(device, Msg);
    }

    /**
     * Use this method to receive the messages from {@param device}.
     * @param device Is a devices from which the message is to be received.
     */
    @Override
    public void Receive(Device device) {
        StartRead(device);
    }

    /**
     * Use this method to broadcast {@param Msg} to all the connected devices
     * @param Msg is a message to be broadcasted.
     */
    @Override
    public void BroadCast(String Msg) {
        for (Device device : ConnectedDeviceList) {
            StartWrite(device, Msg);
        }
    }

    /**
     * Use this method to put {@param Msg} in buffer. Then write the content of buffer
     * in channel of {@param device}.
     * @param device is a device to write the {@param Msg} in its channel.
     * @param Msg is a message to be sent.
     */
    @Override
    public void StartWrite(Device device, String Msg) {
        ByteBuffer buf = ByteBuffer.allocate(2048);
        buf.put(Msg.getBytes());
        buf.flip();
        device.channel.write(buf, device.channel,
                new CompletionHandler<Integer, AsynchronousSocketChannel>() {
                    @Override
                    public void completed(Integer result, AsynchronousSocketChannel channel) {
                        Log.d(TAG, "me : " + Msg);
                    }

                    @Override
                    public void failed(Throwable exc, AsynchronousSocketChannel channel) {
                        Log.d(TAG, "Fail to write the message.");
                    }
                });
    }

    /**
     * Use this method to read the message from buffer and channel of {@param device}
     * @param device is a device from which is to be read.
     */
    @Override
    public void StartRead(final Device device) {
        final ByteBuffer buf = ByteBuffer.allocate(2048);
        device.channel.read(buf, device.channel,
                new CompletionHandler<Integer, AsynchronousSocketChannel>() {
                    @Override
                    public void completed(Integer result, AsynchronousSocketChannel channel) {
                        String msg = new String(buf.array());
                        Log.d(TAG, device.name + " : " + msg);
                        msgReceivedHandler.MsgReceived(device, msg);
                        StartRead(device);
                    }

                    @Override
                    public void failed(Throwable exc, AsynchronousSocketChannel channel) {
                        Log.d(TAG, "Fail to read message.");
                    }
                });
    }

    /**
     * Use this method to read a message from channel of {@param device} and
     *  then broadcast the message to all connected device.
     * @param device is a device from which is to be read.
     */
    @Override
    public void ReceiveBroadcast(final Device device) {
        final ByteBuffer buf = ByteBuffer.allocate(2048);
        device.channel.read(buf, device.channel,
                new CompletionHandler<Integer, AsynchronousSocketChannel>() {
                    @Override
                    public void completed(Integer result, AsynchronousSocketChannel channel) {
                        String msg = new String(buf.array());
                        Log.d(TAG, device.name + " : " + msg);
                        msgReceivedHandler.MsgReceived(device, msg);
                        BroadCast(msg);
                        ReceiveBroadcast(device);
                    }

                    @Override
                    public void failed(Throwable exc, AsynchronousSocketChannel channel) {
                        Log.d(TAG, "Fail to read message.");
                    }
                });
    }

    public class MsgReceivedHandler implements IMsgReceived {
        @Override
        public void MsgReceived(Device device, String msg) {
            //TODO
        }
    }
}


