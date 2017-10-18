package net.kmfish.netty;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by lijun on 17/10/18.
 */
public class FileChannelTest {

    public static void main(String[] args) {
        try {
            readFile("./text.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void readFile(String path) throws IOException {

        RandomAccessFile file = new RandomAccessFile(path, "rw");
        FileChannel channel = file.getChannel();
        System.out.println("file size:" + channel.size());

        ByteBuffer buf = ByteBuffer.allocate(48);

        int bytesRead = channel.read(buf);
        while (bytesRead != -1) {

            System.out.println("Read " + bytesRead);
            buf.flip();

//            while (buf.hasRemaining()) {
//                System.out.println(buf.get());
//            }

            System.out.println(new String(buf.array(), "UTF-8")); // 处理字符时要考虑字符集的问题

            buf.clear();
            bytesRead = channel.read(buf);
        }

        channel.close();
        file.close();
    }

}
