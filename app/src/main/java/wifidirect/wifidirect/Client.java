package wifidirect.wifidirect;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client implements IClient {
    Socket socket;
    String IPAddress;
    final static int ServerPort = 8888;
    DataInputStream dis;
    DataOutputStream dos;

    public Client(InetAddress HostAddr) {
        IPAddress = HostAddr.getHostAddress();
        socket = new Socket();
    }

    @Override
    public void Connect() {

        Thread connect = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket.connect(new InetSocketAddress(IPAddress, ServerPort), 500);

                    // obtaining input and out streams
                    dis = new DataInputStream(socket.getInputStream());
                    dos = new DataOutputStream(socket.getOutputStream());
                    Recieve();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        connect.start();
    }

    @Override
    public void Send(final String msg) {

        Thread SendMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // write on the output stream
                    dos.writeUTF(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        SendMessage.start();
    }

    @Override
    public void Recieve() {
        // readMessage thread
        Thread RecieveMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    byte[] buffer = new byte[1024];
                    int bytes;

                    try {
                        bytes = dis.read(buffer);
                        if (bytes > 0) {
                            MainActivity.handler.obtainMessage(MainActivity.MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                        }
                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                }
            }
        });
        RecieveMessage.start();
    }
}
