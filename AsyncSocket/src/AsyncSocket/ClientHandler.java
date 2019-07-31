package AsyncSocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Vector;
import java.util.concurrent.CompletableFuture;

public class ClientHandler {
    Vector<User> clients = new Vector<>();

    public void Add(User user) {
        clients.add(user);
    }

    public void BroadcastRecvMsg() {
        for (User u : clients) {
            String msg = read(u);
            write(msg,u);
        }
    }

    public String read(User user) {
        ByteBuffer buf = ByteBuffer.allocate(2048);
        String msg = "";
        user.channel.read(buf, user.channel, new CompletionHandler<Integer, AsynchronousSocketChannel>() {
            @Override
            public void completed(Integer result, AsynchronousSocketChannel channel) {
                //print the message
                System.out.println(user.name + " : " + new String(buf.array()));
                String msg = new String(buf.array());
            }

            @Override
            public void failed(Throwable exc, AsynchronousSocketChannel channel) {
                System.out.println("fail to read message from " + user.name);
            }

        });
        return msg;
    }

    public void write(String msg, User user) {
        ByteBuffer buf = ByteBuffer.allocate(2048);
        buf.put(msg.getBytes());
        buf.flip();
        for (User ou : clients) {
            if (!user.name.equals(ou.name)) {
                ou.channel.write(buf, ou.channel, new CompletionHandler<Integer, AsynchronousSocketChannel>() {
                    @Override
                    public void completed(Integer result, AsynchronousSocketChannel channel) {
                    }

                    @Override
                    public void failed(Throwable exc, AsynchronousSocketChannel channel) {
                        System.out.println("Fail to write the message to " + ou.name);
                    }
                });
            }
        }
    }

    public void writeToAllUser(){
        ByteBuffer buf = ByteBuffer.allocate(2048);
        String message="";
        BufferedReader consoleReader = new BufferedReader(
                new InputStreamReader(System.in));
        try {
            message = consoleReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        buf.put(message.getBytes());
        buf.flip();
        System.out.println("message: " + message);
        for(User user : clients)
        {
            user.channel.write(buf, user.channel,
            new CompletionHandler<Integer, AsynchronousSocketChannel>() {
                @Override
                public void completed(Integer result, AsynchronousSocketChannel channel) {
                    //after message written
                    //NOTHING TO DO
                    System.out.println("write to " + user.name);
                }

                @Override
                public void failed(Throwable exc, AsynchronousSocketChannel channel) {
                    System.out.println("Fail to write the message to " + user.name);
                }
            });
        }
    }
}
