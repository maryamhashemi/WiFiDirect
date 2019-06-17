package wifidirect.wifidirect;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client extends Thread {
    Socket socket;
    String IPAddress;
    final static int ServerPort = 8888;
    InputStream is;
    OutputStream os;

    public Client(InetAddress HostAddr) {
        IPAddress = HostAddr.getHostAddress();
        socket = new Socket();
    }

    @Override
    public void run() {
        try {
            socket.connect(new InetSocketAddress(IPAddress, ServerPort),500);
            MainActivity.sendReceive = new SendReceive(socket);
            MainActivity.sendReceive.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
