package AsyncSocket;

import org.junit.Assert;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CompletableFuture;

public class WiFiNetServiceTest {

    @Test
    public void test1() {
        WiFiNetService service = new WiFiNetService();
        int port = 1234;
        String hostname = "127.0.0.1";
        final String[] respond = {new String()};

        try {

            AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
            client.connect(new InetSocketAddress(hostname, port), client,
                    new CompletionHandler<Void, AsynchronousSocketChannel>() {
                        @Override
                        public void completed(Void result, AsynchronousSocketChannel channel) {
                            System.out.println("connected to server.");
                            Device device = new Device(client, "client 1");

                            CompletableFuture.runAsync(() -> {
                                while(true)
                                service.Send(device, "hello");
                            });

                            CompletableFuture.runAsync(() -> {
                                service.Receive(device);
                            });


                        }

                        @Override
                        public void failed(Throwable exc, AsynchronousSocketChannel channel) {
                            System.out.println("fail to connect to server");
                        }
                    });
            Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}