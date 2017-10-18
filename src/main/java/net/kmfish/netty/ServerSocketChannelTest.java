package net.kmfish.netty;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by lijun on 17/10/18.
 */
public class ServerSocketChannelTest {


    private void startNonBlock() throws IOException {

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);

        Selector selector = Selector.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(10000));
        // 注册ServerChannle的accept事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        ByteBuffer buf = ByteBuffer.allocate(48);

        while (selector.isOpen()) {
            int readyChannels = selector.select();
            if (0 == readyChannels) {
                continue;
            }

            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectedKeys.iterator();
            while (iterator.hasNext()) {

                SelectionKey key = iterator.next();

                if (key.isAcceptable()) {
                    System.out.println("isAcceptable");
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);
                    // 注册client channel的read事件
                    socketChannel.register(selector, SelectionKey.OP_READ);
                }

                if (key.isReadable()) {
                    System.out.println("isReadable");

                    SocketChannel client = (SocketChannel) key.channel();

                    int bytesRead = client.read(buf);
                    System.out.println("bytesRead " + bytesRead);
                    while (bytesRead != -1) {
                        buf.flip();

                        System.out.println("receive:");
                        while (buf.hasRemaining()) {
                            System.out.print((char) buf.get());
                        }
                        System.out.println();

                        buf.clear();
                        bytesRead = client.read(buf);
                    }

                    client.close();
                    buf.clear();
                }
                iterator.remove();
            }

        }

    }

    /**
     * 阻塞式的channel
     * @throws IOException
     */
    private void start() throws IOException {

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(9999));

        while (true) {
            SocketChannel socketChannel = serverSocketChannel.accept();

            System.out.println("accept a socketChannel:" + socketChannel);

            ByteBuffer buf = ByteBuffer.allocate(48);

            int bytesRead = socketChannel.read(buf);
            System.out.println("bytesRead " + bytesRead);
            while (bytesRead != -1) {
                buf.flip();

                System.out.println("receive:");
                while (buf.hasRemaining()) {
                    System.out.print((char)buf.get());
                }

                buf.clear();
                bytesRead = socketChannel.read(buf);
            }

            buf.clear();
            socketChannel.close();
        }
    }


    public static void main(String[] args) {
        try {
            new ServerSocketChannelTest().startNonBlock();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
