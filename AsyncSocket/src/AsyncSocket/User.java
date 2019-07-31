package AsyncSocket;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

public class User {
    AsynchronousSocketChannel channel;
    String name;

    User(AsynchronousSocketChannel channel, String name){
        this.channel = channel;
        this.name = name;
    }
}
