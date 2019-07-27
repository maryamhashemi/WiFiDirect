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
    public static void main(String[] args) throws Exception{
        int port = 1234;
        try {
            final AsynchronousServerSocketChannel server =
                    AsynchronousServerSocketChannel.open().bind(
                            new InetSocketAddress("127.0.0.1", 1234));

            System.out.println("Server listening on " + port);

            server.accept("Client connection",
                    new CompletionHandler<AsynchronousSocketChannel, Object>() {
                        public void completed(AsynchronousSocketChannel ch, Object att) {
                            System.out.println("Accepted a connection");

                            // accept the next connection
                            server.accept("Client connection", this);

                            // handle this connection
                            CompletableFuture.runAsync(() -> {
                                //write an message to server side
                                while(true) {
                                    startWrite(ch);
                                }
                            });
                            CompletableFuture.runAsync(() -> {
                                //start to read message
                                while(true) {
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