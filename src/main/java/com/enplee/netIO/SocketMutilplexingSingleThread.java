package com.enplee.netIO;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class SocketMutilplexingSingleThread {
    private SocketChannel server = null;
    private Selector selector = null;
    private int port = 9090;

    private void initServer() {
        try {
            server = SocketChannel.open();
            selector = Selector.open();  // create selector epoll: epoll_create() select/poll create fds[]
            server.register(selector, SelectionKey.OP_ACCEPT); // epoll: epoll_ctl(seletor,ADD,seletor,ACCEPT)  select/poll copy fd -> fds[]
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() throws IOException {
        initServer();
        while (true) {
            while (selector.select()>0) {
                Set<SelectionKey> keys = selector.selectedKeys(); // epoll: epoll_wait() select/poll: select()/poll()
                Iterator<SelectionKey> iterator = keys.iterator();
                while (iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (key.isAcceptable()) {

                    }else if (key.isReadable()) {

                    }else if (key.isWritable()) {

                    }
                }
            }
        }
    }

    private void acceptHandler(SelectionKey key) throws IOException {
        ServerSocketChannel ssc = (ServerSocketChannel)key.channel();
        SocketChannel client = ssc.accept();
        client.configureBlocking(false);
        ByteBuffer buff = ByteBuffer.allocate(1024);
        client.register(selector,SelectionKey.OP_READ,buff);
    }

    private void readHandler(SelectionKey key) {
        SocketChannel client = (SocketChannel)key.channel();
         ByteBuffer buff = (ByteBuffer) key.attachment();
         buff.clear();
         int read = 0;
         try {
            while (true) {
                read =  client.read(buff);
                if (read > 0 ) {
                    client.register(key.selector(),SelectionKey.OP_WRITE,buff);
                } else if (read == 0 ) {
                    break;
                } else {
                    client.close();
                    break;
                }
            }

         }catch (IOException e) {
             e.printStackTrace();
         }
    }
}
