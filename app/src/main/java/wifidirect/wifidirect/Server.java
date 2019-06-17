package wifidirect.wifidirect;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server extends Thread {

    //Vector to store active clients
    static Vector<ClientHandler> Clients = new Vector<>();

    Socket socket;
    ServerSocket serverSocket;

    @Override
    public void run() {
        try {
            // server is listening on port 8888
            serverSocket = new ServerSocket(8888);

            // running infinite loop for getting
            // client request
            while (true) {

                // Accept the incoming request
                socket = serverSocket.accept();

                // obtain input and output streams
                InputStream is = socket.getInputStream();
                OutputStream os = socket.getOutputStream();

                // Create a new handler object for handling this request.
                ClientHandler client = new ClientHandler(socket,is,os);

                // Create a new Thread with this object.
                Thread t = new Thread(client);

                // add this client to active clients list
                Clients.add(client);

                // start the thread.
                t.start();

                MainActivity.sendReceive = new SendReceive(socket);
                MainActivity.sendReceive.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


class ClientHandler implements Runnable
{
    private String name;
    final InputStream is;
    final OutputStream os;
    Socket s;

    // constructor
    public ClientHandler(Socket s, InputStream is, OutputStream os) {
        this.s = s;
        this.is = is;
        this.os = os;
    }

    @Override
    public void run() {

        byte[] buffer = new byte[1024];
        int bytes;

        while (true)
        {
            try
            {
                // receive
                bytes = is.read(buffer);

                if (bytes > 0) {
                    // recieve message also by server itself
                    MainActivity.handler.obtainMessage(MainActivity.MESSAGE_READ, bytes, -1, buffer).sendToTarget();

                    // broadcast for all Clients
                    for (ClientHandler clientHandler : Server.Clients) {
                        if (clientHandler.s != null) {
                            clientHandler.os.write(buffer);
                        }
                    }
                }
            } catch (IOException e) {

                e.printStackTrace();
            }

        }
    }
}
