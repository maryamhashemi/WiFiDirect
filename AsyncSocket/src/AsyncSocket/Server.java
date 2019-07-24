package AsyncSocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;

public class Server {
    public static void main(String[] args) throws Exception {
        AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open();
        String host = "localhost";
        int port = 8989;
        InetSocketAddress Addr = new InetSocketAddress(host, port);
        server.bind(Addr);
        System.out.format("Server is listening at %s%n", Addr);
        Attachments attach = new Attachments();
        attach.server = server;
        server.accept(attach, new ConnectionHandler());
        Thread.currentThread().join();
    }
}

class Attachments {
    AsynchronousServerSocketChannel server;
    AsynchronousSocketChannel client;
    ByteBuffer buffer;
    SocketAddress clientAddr;
    boolean isRead;
}

class ConnectionHandler implements
        CompletionHandler<AsynchronousSocketChannel, Attachments> {
    @Override
    public void completed(AsynchronousSocketChannel client, Attachments attach) {
        try {
            SocketAddress clientAddr = client.getRemoteAddress();
            System.out.format("Accepted a  connection from  %s%n", clientAddr);
            attach.server.accept(attach, this);
            sReadWriteHandler rwHandler = new sReadWriteHandler();
            Attachments newAttach = new Attachments();
            newAttach.server = attach.server;
            newAttach.client = client;
            newAttach.buffer = ByteBuffer.allocate(2048);
            newAttach.isRead = true;
            newAttach.clientAddr = clientAddr;

            while(true) {
                //client.read(newAttach.buffer, newAttach, rwHandler);
                String msg = null;
                try {
                    msg = getTextFromUser();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Charset cs = Charset.forName("UTF-8");
                byte[] data = msg.getBytes(cs);
                newAttach.buffer.clear();
                newAttach.buffer.put(data);
                newAttach.buffer.flip();
                newAttach.isRead = false;
                client.write(newAttach.buffer, newAttach, rwHandler);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void failed(Throwable e, Attachments attach) {
        System.out.println("Failed to accept a  connection.");
        e.printStackTrace();
    }

    private String getTextFromUser() throws Exception{
        System.out.print(" Please enter a  message:");
        BufferedReader consoleReader = new BufferedReader(
                new InputStreamReader(System.in));
        String msg = consoleReader.readLine();
        return msg;
    }
}

class sReadWriteHandler implements CompletionHandler<Integer, Attachments> {
    @Override
    public void completed(Integer result, Attachments attach){
        if (result == -1) {
            try {
                attach.client.close();
                System.out.format("Stopped  listening to the   client %s%n",
                        attach.clientAddr);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return;
        }

        if (attach.isRead) {
            attach.buffer.flip();
            int limits = attach.buffer.limit();
            byte bytes[] = new byte[limits];
            attach.buffer.get(bytes, 0, limits);
            Charset cs = Charset.forName("UTF-8");
            String msg = new String(bytes, cs);
            System.out.format("Client at  %s  says: %s%n", attach.clientAddr, msg);

        } else {
            attach.buffer.clear();
            attach.isRead = true;
        }
    }

    @Override
    public void failed(Throwable e, Attachments attach) {
        e.printStackTrace();
    }
}
