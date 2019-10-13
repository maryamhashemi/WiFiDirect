package wifidirect.wifidirect.ChatAsync;

import java.nio.channels.AsynchronousSocketChannel;

/**
 * In this Class, We consider a asynchronous socket channel for each device
 * in order to connect to other devices and interchange messages.
 * Also, we assign a name to devices to make them recognizable.
 */
public class Device {
    AsynchronousSocketChannel channel;
    String name;

    Device(AsynchronousSocketChannel channel, String name)
    {
        this.channel = channel;
        this.name = name;
    }
}
