package com.enplee.netIO.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.*;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;

public class nettyTest {

    @Test
    /**
     * Netty 提供了更方便使用的bytebuf来取代java JDK-NIO中原生的Bytebuffer
     */
    public void testBytebuf() {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(8, 20);
        boolean b = PooledByteBufAllocator.defaultUseCacheForAllThreads();
        System.out.println(b);
        print(buffer);
        buffer.writeBytes(new byte[]{1,3,3,3});
    }
    public void print(ByteBuf buf) {
        System.out.println(buf.isReadable());
        System.out.println(buf.readerIndex());
        System.out.println(buf.readableBytes());
        System.out.println(buf.isWritable());
        System.out.println(buf.writerIndex());
        System.out.println(buf.writableBytes());
        System.out.println(buf.maxCapacity());
        System.out.println(buf.capacity());
    }
    @Test
    public void LoopExecutor() throws IOException {
        NioEventLoopGroup loopGroup = new NioEventLoopGroup(3);
        loopGroup.execute(() -> {
            for(;;) {
                System.out.println("hello 1");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        loopGroup.execute(()->{
            for(;;) {
                System.out.println("hello 2");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        System.in.read();
    }

    @Test
    public void clientModel() throws InterruptedException {
        NioEventLoopGroup eventExecutors = new NioEventLoopGroup(1);

        NioSocketChannel client = new NioSocketChannel();  // SocketChannel -> NioScoketChannel
        eventExecutors.register(client);

        ChannelFuture connect = client.connect(new InetSocketAddress(9090));
        ChannelFuture sync = connect.sync();

        ByteBuf buf = Unpooled.copiedBuffer("hello server".getBytes());
        ChannelFuture send = client.writeAndFlush(buf);
        send.sync();

        sync.channel().closeFuture().sync();
        System.out.println("client over");

    }

    @Test
    public void NettyClient() throws InterruptedException {
        NioEventLoopGroup loopGroup = new NioEventLoopGroup(1);
        Bootstrap bootstrap = new Bootstrap();
        ChannelFuture bind = bootstrap.group(loopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(new MyInHandler());
                    }
                })
                .connect(new InetSocketAddress(9090));

        bind.sync().channel().closeFuture().sync();
    }

    @Test
    public void NettyServer() throws Exception {
        NioEventLoopGroup loopGroup = new NioEventLoopGroup(1);
        ServerBootstrap bootstrap = new ServerBootstrap();
        ChannelFuture bind = bootstrap.group(loopGroup, loopGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {

                    @Override
                    protected void initChannel(NioSocketChannel nioServerSocketChannel) throws Exception {
                        ChannelPipeline pipeline = nioServerSocketChannel.pipeline();
                        pipeline.addLast(new MyInHandler());
                    }
                })
                .bind(new InetSocketAddress(9090));
        bind.sync().channel().closeFuture().sync();
    }
}
class MyInHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf bytebuf = (ByteBuf)msg;
        CharSequence charSequence = bytebuf.getCharSequence(0, bytebuf.readableBytes(), CharsetUtil.UTF_8);
        System.out.println(charSequence);
        ctx.writeAndFlush(charSequence);
    }
}
