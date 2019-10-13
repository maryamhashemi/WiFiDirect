package wifidirect.wifidirect.ChatMultiThread;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import wifidirect.wifidirect.ChatActivity;

/**
 * In this class, we aim to handle the clients that are conncted to server.
 * For example broadcast message and etc.
 */
public class ClientHandler implements IClientHandler {
    public static final String TAG = "MyApp: ChatMultiThread: ClientHandler";
    String name;
    DataInputStream dis;
    DataOutputStream dos;
    Socket s;
    boolean isloggedin;

    public ClientHandler(Socket s, String name,
                         DataInputStream dis, DataOutputStream dos) {
        this.dis = dis;
        this.dos = dos;
        this.name = name;
        this.s = s;
        this.isloggedin = true;
    }

    /**
     * Use this method to broadcast the message which the server is received
     * from one of its clients. As we read from data input data stream and
     * write to data output stream, we must handle IoException.
     */
    @Override
    public void Broadcast() {
        while (true) {
            try {
                byte[] buffer = new byte[2048];
                int bytes;
                String msg = null;

                bytes = dis.read(buffer);
                if (bytes > 0) {
                    //ToDo: seperate ui works from here
                    ChatActivity.handler.obtainMessage(ChatActivity.MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                    msg = new String(buffer, 0, bytes);
                    Log.d(TAG, "server received: " + msg);
                }

                //TODO: disconnected client must be handled

                // Broadcast message to all logged in clients
                for (ClientHandler c : Server.clients) {
                    if (!c.name.equals(this.name) && c.isloggedin) {
                        c.dos.writeUTF(msg);
                        Log.d(TAG, "Server broadcast to " + c.name + " " + msg);
                    }
                }
            } catch (IOException e) {

                e.printStackTrace();
            }
        }
    }
}


