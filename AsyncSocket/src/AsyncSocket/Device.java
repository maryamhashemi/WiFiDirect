package AsyncSocket;

import java.nio.channels.AsynchronousSocketChannel;

 class Device {
    AsynchronousSocketChannel channel;
    String name;

    Device(AsynchronousSocketChannel channel, String name)
    {
        this.channel = channel;
        this.name = name;
    }
}
