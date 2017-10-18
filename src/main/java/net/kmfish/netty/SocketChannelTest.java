package net.kmfish.netty;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by lijun on 17/10/18.
 */
public class SocketChannelTest {


    private void connectNonBlock() throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);

        Selector selector = Selector.open();
        SelectionKey key = socketChannel.register(selector, SelectionKey.OP_CONNECT);

        // 异步模式下，connect会直接返回，稍候当isConnectable后，要调用socketChannel.finishConnect来结束连接操作。
        boolean connected = socketChannel.connect(new InetSocketAddress("localhost", 10000));
        System.out.println("connected:" + connected);

        while (selector.isOpen()) {
            int readyChannels = selector.select();
            if (0 == readyChannels) {
                continue;
            }

            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectedKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key1 = iterator.next();
                if (key1.isConnectable()) {
                    System.out.println("isConnectable ");

                    socketChannel = (SocketChannel) key1.channel();
                    if (!socketChannel.finishConnect()) {
                        continue;
                    }
                    socketChannel.register(selector, SelectionKey.OP_WRITE);
                }

                if (key1.isWritable()) {
                    System.out.println("isWritable");

                    ByteBuffer buf = ByteBuffer.allocate(48);
                    buf.put(("hello, this is client" + System.currentTimeMillis()).getBytes());
                    buf.flip();

                    while (buf.hasRemaining()) {
                        socketChannel.write(buf);
                    }

                    buf.clear();

                    socketChannel.close();
                    selector.close();
                }

                iterator.remove();
            }
        }
    }

    /**
     * 阻塞式的channel
     * @throws IOException
     */
    private void connect() throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("localhost", 9999));

        ByteBuffer buf = ByteBuffer.allocate(48);
        buf.put(("hello, this is client" + System.currentTimeMillis()).getBytes());
        buf.flip();

        while (buf.hasRemaining()) {
            socketChannel.write(buf);
        }

        buf.clear();
        socketChannel.close();
    }

    public static void main(String[] args) {
        try {
//            new SocketChannelTest().connect();
            new SocketChannelTest().connectNonBlock();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
