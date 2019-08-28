package wifidirect.wifidirect;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CompletableFuture;

public class Client implements IClient {

    @Override
    public void Start() {
        WiFiNetService service = new WiFiNetService();
        int port = 8888;
        String hostname = "127.0.0.1";

        try {
            AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
            client.connect(new InetSocketAddress(hostname, port), client,
                    new CompletionHandler<Void, AsynchronousSocketChannel>() {
                        @Override
                        public void completed(Void result, AsynchronousSocketChannel channel) {
                            System.out.println("connected to server.");

                            final Device device = new Device(channel, "client");
                            CompletableFuture.runAsync(() -> {
                                while (true) {
                                    String msg = getMSg();
                                    service.Send(device, msg);
                                }
                            });

                            CompletableFuture.runAsync(() -> {
                                while (true) {
                                    service.Receive(device);
                                }
                            });
                        }

                        @Override
                        public void failed(Throwable exc, AsynchronousSocketChannel channel) {
                            System.out.println("fail to connect to server");
                            exc.printStackTrace();
                        }
                    });
            Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getMSg() {
        //ToDo: get message from edit text
        return "";
    }
}
