package AsyncSocket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.*;

public class Server {
    private static int port = 1234;
    private static int i = 0;
    public static void main(String[] args) throws Exception{
        try {
            final AsynchronousServerSocketChannel server =
                    AsynchronousServerSocketChannel.open().bind(
                            new InetSocketAddress("127.0.0.1", 1234));

            System.out.println("Server listening on " + port);

            ClientHandler clientHandler = new ClientHandler();

            server.accept("Client connection",
                    new CompletionHandler<AsynchronousSocketChannel, Object>() {
                        public void completed(AsynchronousSocketChannel ch, Object att) {
                            i++;
                            User user = new User(ch, "client" + (i));
                            clientHandler.Add(user);
                            System.out.println(user.name + " connected to server.");

                            // accept the next connection
                            server.accept("Client connection", this);

                            // handle this connection
                            CompletableFuture.runAsync(() -> {
                                //write an message to client side
                                while(true) {
                                    startWrite(ch);
                                    //clientHandler.writeToAllUser();
                                }
                            });

                            CompletableFuture.runAsync(() -> {
                                //start to read message
                                while(true) {
                                    //clientHandler.BroadcastRecvMsg();
                                    startRead(ch);
                                }
                            });
                        }

                        public void failed(Throwable exc, Object att) {
                            System.out.println("Failed to accept connection");
                        }
                    });
            Thread.currentThread().join();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void startRead( final AsynchronousSocketChannel sockChannel) {
        final ByteBuffer buf = ByteBuffer.allocate(2048);
        sockChannel.read( buf, sockChannel,
                new CompletionHandler<Integer, AsynchronousSocketChannel>(){
            @Override
            public void completed(Integer result, AsynchronousSocketChannel channel) {
                //print the message
                System.out.println( "Read message:" + new String( buf.array()) );
                startRead(channel);
            }

            @Override
            public void failed(Throwable exc, AsynchronousSocketChannel channel) {
                System.out.println( "fail to read message from client");
            }

        });
    }
    private static void startWrite( final AsynchronousSocketChannel sockChannel) {
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
        sockChannel.write(buf, sockChannel,
                new CompletionHandler<Integer, AsynchronousSocketChannel >() {
            @Override
            public void completed(Integer result, AsynchronousSocketChannel channel ) {
                //after message written
                //NOTHING TO DO
            }

            @Override
            public void failed(Throwable exc, AsynchronousSocketChannel channel) {
                System.out.println( "Fail to write the message to client");
            }
        });
    }
}