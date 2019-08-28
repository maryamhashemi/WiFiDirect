package simpleNetwork;

import java.io.*;
import java.net.*;

public class Client implements IClient {
    private DataInputStream dis;
    private DataOutputStream dos;

    @Override
    public void Connect(int port) {

        InetAddress ip;
        try {
            // getting localhost ip
            ip = InetAddress.getByName("localhost");

            // establish the connection
            Socket socket = new Socket(ip, port);

            // obtaining input and out streams
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());

        } catch (IOException e) {

            e.printStackTrace();
        }
    }


    @Override
    public void Send(String msg) {
        try {
            // write on the output stream
            dos.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void Receive() {
        // readMessage thread
        Thread ReceiveMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        String msg = dis.readUTF();
                        for (ClientHandler c : Server.clients) {
                            c.dos.writeUTF(msg);
                        }
                        System.out.println(msg);

                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                }

            }
        });

        ReceiveMessage.start();
    }
} 