package wifidirect.wifidirect.ChatAsync;

import android.util.Log;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CompletableFuture;

public class Client implements IClient {

    private static final String TAG = "MyApp: ChatAsync: Client";
    Device device;
    WiFiNetService service = new WiFiNetService();
    int port = 8888;
    String hostname = "127.0.0.1";

    /**
     * Use this method to connect to the server which listens on port 8888.
     * Pay attention that client connects the server asynchronously.
     */
    @Override
    public void Start() {
        CompletionHandler connectionHandler = new CompletionHandler<Void, AsynchronousSocketChannel>() {
            @Override
            public void completed(Void result, AsynchronousSocketChannel channel) {
                Log.d(TAG, "Connected to server.");
            }

            @Override
            public void failed(Throwable exc, AsynchronousSocketChannel channel) {
                Log.d(TAG, "Fail to connnect to server.");
                exc.printStackTrace();
            }
        };

        try {
            AsynchronousSocketChannel client = AsynchronousSocketChannel.open();

            CompletableFuture.runAsync(() -> {
                client.connect(new InetSocketAddress(hostname, port), client, connectionHandler);
            });

            device = new Device(client, "client");
            service.Receive(device);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
