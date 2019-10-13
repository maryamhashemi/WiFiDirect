package wifidirect.wifidirect.ChatMultiThread;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import wifidirect.wifidirect.ChatActivity;

public class Server implements IServer {
    public static final String TAG = "MyApp: ChatMultiThread: Server";
    static Vector<ClientHandler> clients = new Vector<>();
    Socket socket;
    ServerSocket serverSocket;
    int i = 0;
    int port = 8888;

    /**
     * Use this method to setup server. As a Result, Server listens on port 8888 to accept client.
     * Pay attention that we need to use a specific thread to accept connection from clients.
     * We must handle IoException because of accept system call.
     */
    @Override
    public void Accept() {
        Thread accept = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    serverSocket = new ServerSocket(port);
                    // running infinite loop for getting client request
                    while (true) {
                        // Accept the incoming request
                        socket = serverSocket.accept();

                        // obtain input and output streams
                        DataInputStream dis = new DataInputStream(socket.getInputStream());
                        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

                        Log.d(TAG, "server accepted connection.");
                        // Create a new handler object for handling this request.
                        final ClientHandler client = new ClientHandler(socket, "client " + i, dis, dos);

                        // Create a new Thread with this object.
                        Thread t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                client.Broadcast();
                            }
                        });

                        // add this client to active clients list
                        clients.add(client);

                        // start the thread.
                        t.start();

                        // increment i for new client.
                        // i is used for naming only, and can be replaced
                        // by any naming scheme
                        i++;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        accept.start();
    }

    /**
     * Use this method to send {@param Msg} to all clients which are connected to server.
     * We write a message in data output stream which makes connection between
     * client and server. So we must handle the IoException.
     * Pay attention that we need to use a specific thread to send message.
     *
     * @param Msg is a message to be sent.
     */
    @Override
    public void Send(final String Msg) {
        Thread SendMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (ClientHandler c : clients) {
                        if (c.isloggedin) {
                            c.dos.writeUTF(Msg);
                            Log.d(TAG, "Server send to " + c.name + " " + Msg);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        SendMessage.start();
    }
}

