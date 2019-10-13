package wifidirect.wifidirect.ChatAsync;

import android.util.Log;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CompletableFuture;

public class Server implements IServer {

    private static final String TAG = "MyApp: ChatAsync: Server";
    public static int i = 0;
    WiFiNetService service = new WiFiNetService();
    int port = 8888;
    String hostname = "127.0.0.1";

    /**
     * Use this method to set up Server. As a Result, Server listens on port 8888 to accept client.
     * Pay attention that server works asynchronously.
     */
    @Override
    public void Start() {
        try {
            final AsynchronousServerSocketChannel server =
                    AsynchronousServerSocketChannel.open().bind(
                            new InetSocketAddress(hostname, port));

            CompletableFuture.runAsync(() -> {
                server.accept("Client connection",
                        new CompletionHandler<AsynchronousSocketChannel, Object>() {
                            @Override
                            public void completed(AsynchronousSocketChannel channel, Object att) {
                                Log.d(TAG, "Server listening on" + port);
                                i++;
                                Device device = new Device(channel, "client" + (i));
                                service.ConnectedDeviceList.add(device);
                                Log.d(TAG, device.name + " connected to server.");

                                server.accept("Client connection", this);

                            }

                            @Override
                            public void failed(Throwable exc, Object att) {
                                Log.d(TAG, "Failed to accept connection");
                            }
                        });
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
