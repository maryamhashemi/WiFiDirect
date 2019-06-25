package wifidirect.wifidirect;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

// ClientHandler class
public class ClientHandler implements IClientHandler {
    private String name;
    DataInputStream dis;
    DataOutputStream dos;
    Socket s;
    boolean isloggedin;

    // constructor
    public ClientHandler(Socket s, String name,
                         DataInputStream dis, DataOutputStream dos) {
        this.dis = dis;
        this.dos = dos;
        this.name = name;
        this.s = s;
        this.isloggedin = true;
    }

    @Override
    public void Broadcast() {
        while (true) {
            try {
                // A server recieved  a message from one of the clients
                byte[] buffer = new byte[1024];
                int bytes;
                String msg = null;

                bytes = dis.read(buffer);
                if (bytes > 0) {
                    MainActivity.handler.obtainMessage(MainActivity.MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                    msg = new String(buffer,0,bytes);
                }

                //TODO: disconnected client must be handled

                // Broadcast message to all logged in clients
                for (ClientHandler c : Server.clients) {
                    if (c.name.equals(this.name) && c.isloggedin) {
                        //c.dos.writeUTF(msg);
                    } else if (c.isloggedin) {
                        c.dos.writeUTF(msg);
                    }

                }
            } catch (IOException e) {

                e.printStackTrace();
            }
        }
    }
}

