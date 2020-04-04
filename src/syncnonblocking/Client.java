package syncnonblocking;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Client {
    private static final int BUF_SIZE = 1024 * 2;
    public static void main(String[] args) {
        ByteBuffer buf = ByteBuffer.allocate(BUF_SIZE);
        try (SocketChannel clientChannel = SocketChannel.open();
             Selector selector = Selector.open();) {
            clientChannel.connect(new InetSocketAddress("127.0.0.1", 13579));
            clientChannel.configureBlocking(false);
            ConsoleThread thread = new ConsoleThread(clientChannel);;
            clientChannel.register(selector, SelectionKey.OP_READ, thread);
            thread.start();
            boolean flag = true;
            // 客户端没有必要使用Selector，我是为了练习而使用的。或者P2P通信时可以这样写
            while(flag && selector.select() > 0) { // 不要先select()再flag，因为select会阻塞flag的判断
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();
                while(iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (key.isReadable()) {
                        SocketChannel readChannel = (SocketChannel) key.channel();
                        buf.clear();
                        readChannel.read(buf);
                        buf.flip();
                        byte[] tmp = new byte[buf.limit()];
                        buf.get(tmp);
                        buf.clear();
                        String msg = new String(tmp);
                        if (msg.equals("exit")) {
                            readChannel.write(ByteBuffer.wrap("exit".getBytes()));
                            ((ConsoleThread)key.attachment()).interrupt();
                            key.cancel();
                            flag = false;
                        } else {
                            System.out.println(msg);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
