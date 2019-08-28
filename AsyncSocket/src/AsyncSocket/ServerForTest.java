package AsyncSocket;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CompletableFuture;

public class ServerForTest {
    private static int i = 0;

    public static void main(String[] args) {
        WiFiNetService service = new WiFiNetService();
        int port = 1234;
        String hostname = "127.0.0.1";

        try {
            final AsynchronousServerSocketChannel server =
                    AsynchronousServerSocketChannel.open().bind(
                            new InetSocketAddress(hostname, port));
            System.out.println("Server listening on " + port);

            server.accept("Client connection",
                    new CompletionHandler<AsynchronousSocketChannel, Object>() {
                        public void completed(AsynchronousSocketChannel channel, Object att) {
                            i++;
                            Device device = new Device(channel, "client" + (i));
                            service.DeviceList.add(device);
                            System.out.println(device.name + " connected to server.");

                            server.accept("Client connection", this);
                            CompletableFuture.runAsync(() -> {
                                service.ReceiveRespondUppercase(device);
                            });
                        }

                        public void failed(Throwable exc, Object att) {
                            System.out.println("Failed to accept connection");
                        }
                    });
            Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
