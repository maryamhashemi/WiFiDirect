package AsyncSocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CompletableFuture;

public class MainClient {

    public static void main(String[] args) throws Exception {
        try {
            WiFiNetService service = new WiFiNetService();
            int port = 1234;
            String hostname = "127.0.0.1";
            AsynchronousSocketChannel client = AsynchronousSocketChannel.open();

            var connectionHandler = new CompletionHandler<Void, AsynchronousSocketChannel>() {
                @Override
                public void completed(Void result, AsynchronousSocketChannel channel) {
                    System.out.println("connected to server.");
                }

                @Override
                public void failed(Throwable exc, AsynchronousSocketChannel channel) {
                    System.out.println("fail to connect to server");
                }
            };

            client.connect(new InetSocketAddress(hostname, port), client,
                   connectionHandler);

            Thread.sleep(1000);
            Device device = new Device(client, "client");
            service.Receive(device);

            while (true) {
                String msg = getMsg();
                service.Send(device, msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static String getMsg() {
        BufferedReader consoleReader = new BufferedReader(
                new InputStreamReader(System.in));
        String message = "";
        try {
            message = consoleReader.readLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;
    }


}
