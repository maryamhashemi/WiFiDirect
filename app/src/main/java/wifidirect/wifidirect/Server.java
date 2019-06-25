package wifidirect.wifidirect;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server implements IServer {

    //Vector to store active clients
    static Vector<ClientHandler> clients = new Vector<>();

    Socket socket;
    ServerSocket serverSocket;

    // Counter for clients
    int i = 0;

    @Override
    public void Accept() {
        Thread accept = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    serverSocket = new ServerSocket(8888);
                    // running infinite loop for getting
                    // client request
                    while (true) {
                        // Accept the incoming request
                        socket = serverSocket.accept();

                        // obtain input and output streams
                        DataInputStream dis = new DataInputStream(socket.getInputStream());
                        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());


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

    @Override
    public void Send(final String msg) {
        Thread SendMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (ClientHandler c : clients) {
                        if (c.isloggedin) {
                            c.dos.writeUTF(msg);
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
