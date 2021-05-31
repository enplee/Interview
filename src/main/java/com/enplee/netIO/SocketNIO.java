package com.enplee.netIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;

public class SocketNIO {
    public static void main(String[] args) throws Exception {
        List<SocketChannel> clients = new LinkedList<>();

        ServerSocketChannel server = ServerSocketChannel.open();  // 创建服务管道
        server.bind(new InetSocketAddress(9090)); // 绑定socket端口
        server.configureBlocking(false); // set非阻塞


        while (true) {
            SocketChannel client = server.accept();  // 非阻塞式监听socket

            if(client == null) { // -1 -> null
                System.out.println("无连接");
            }else {
                client.configureBlocking(false); // 将连接添加到clients容器 并设置非阻塞读
                clients.add(client);
            }

            ByteBuffer buffer = ByteBuffer.allocateDirect(4096);  // ByteBuffer进行数据读取

            for(SocketChannel c : clients) {
                int num = c.read(buffer);
                if(num>0) {
                    buffer.flip();
                    byte[] req = new byte[buffer.limit()];
                    buffer.get(req);

                    String s = new String(req);
                    System.out.println(s);
                    buffer.clear();
                }
            }

        }


    }
}
