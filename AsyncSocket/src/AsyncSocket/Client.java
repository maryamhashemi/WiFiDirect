package AsyncSocket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;


public class Client {
    public static void main(String[] args) throws Exception {

        try {
            //create a socket channel
            AsynchronousSocketChannel client = AsynchronousSocketChannel.open();

            //try to connect to the server side
            client.connect(new InetSocketAddress("127.0.0.1", 1234), client,
                    new CompletionHandler<Void, AsynchronousSocketChannel>() {
                        @Override
                        public void completed(Void result, AsynchronousSocketChannel channel) {
                            System.out.println("connected to server.");

                            // handle this connection
                            CompletableFuture.runAsync(() -> {
                                //write an message to server side
                                while (true) {
                                    startWrite(channel);
                                }
                            });

                            CompletableFuture.runAsync(() -> {
                                //start to read message
                                while (true) {
                                    startRead(channel);
                                }
                            });
                            ;
                        }

                        @Override
                        public void failed(Throwable exc, AsynchronousSocketChannel channel) {
                            System.out.println("fail to connect to server");
                        }
                    });
            Thread.currentThread().join();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void startRead(final AsynchronousSocketChannel sockChannel) {
        final ByteBuffer buf = ByteBuffer.allocate(2048);
        sockChannel.read(buf, sockChannel,
                new CompletionHandler<Integer, AsynchronousSocketChannel>() {
                    @Override
                    public void completed(Integer result, AsynchronousSocketChannel channel) {
                        //print the message
                        System.out.println("Read message:" + new String(buf.array()));
                        startRead(channel);
                    }

                    @Override
                    public void failed(Throwable exc, AsynchronousSocketChannel channel) {
                        System.out.println("fail to read message from server");
                    }

                });
    }

    private static void startWrite(final AsynchronousSocketChannel sockChannel) {
        ByteBuffer buf = ByteBuffer.allocate(2048);
        String message = "";
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
                new CompletionHandler<Integer, AsynchronousSocketChannel>() {
                    @Override
                    public void completed(Integer result, AsynchronousSocketChannel channel) {
                        //after message written
                        //NOTHING TO DO
                    }

                    @Override
                    public void failed(Throwable exc, AsynchronousSocketChannel channel) {
                        System.out.println("Fail to write the message to server");
                    }
                });
    }
}

