package wifidirect.wifidirect.ChatMultiThread;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import wifidirect.wifidirect.ChatActivity;

public class Client implements IClient {
    public static final String TAG = "MyApp: ChatMultiThread: Client";
    private Socket socket;
    private String IPAddress;
    private final static int ServerPort = 8888;
    private DataInputStream dis;
    private DataOutputStream dos;

    public Client(InetAddress HostAddr) {
        IPAddress = HostAddr.getHostAddress();
        socket = new Socket();
    }

    public Client(String HostAddr) {
        IPAddress = HostAddr;
        socket = new Socket();
    }

    /**
     * Use this method to connect to the server which listens on port 8888.
     * If the client connect to server successfully,
     * client will be ready to receive messages.
     * Pay attention that we need to use a specific thread to connect to server.
     */
    @Override
    public void Connect() {
        Thread connect = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket.connect(new InetSocketAddress(IPAddress,ServerPort), 5000);
                    dis = new DataInputStream(socket.getInputStream());
                    dos = new DataOutputStream(socket.getOutputStream());
                    Log.d(TAG,"Client connected to Server on port" + ServerPort);
                    Receive();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        connect.start();
    }

    /**
     * Use this method to send {@param Msg} to server.
     * We write a message in data output stream which makes connection between
     * client and server. So we must handle the IoException.
     * Pay attention that we need to use a specific thread to send message.
     * @param Msg is a message to be sent.
     */
    @Override
    public void Send(final String Msg) {
        Thread SendMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // write on the output stream
                    dos.writeUTF(Msg);
                    Log.d(TAG,"Client send: " + Msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        SendMessage.start();
    }

    /**
     * Use this method to receive message from Server.
     * We read a message from data input stream which makes connection between
     * client and server. So we must handle the IoException.
     * Pay attention that we need to use a specific thread to receive message.
     */
    @Override
    public void Receive() {
        Thread ReceiveMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    byte[] buffer = new byte[2048];
                    int bytes;

                    try {
                        bytes = dis.read(buffer);
                        if (bytes > 0) {
                            Log.d(TAG,"Client received message.");
                            // ToDo : seperate ui works from here.
                            ChatActivity.handler.obtainMessage(ChatActivity.MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                        }
                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                }
            }
        });
        ReceiveMessage.start();
    }
}

