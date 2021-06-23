### Netty面试题

#### 1. BIO/NIO/AIO的区别

+ 什么是BIO

```
Blocking I/O 是一种阻塞 + 同步的通信模式。
```

+ 什么是NIO

```
New IO 或者 Non Blocking IO 
Java NIO三大组件:
Channel：
Buffer:
Selector：
```

+ 什么是AIO

```
异步io
```

+ Unix的IO模型 **https://blog.csdn.net/matthew_zhang/article/details/71328697**

```
1. 异步io: 用户发起请求后，不必等待数据，操作系统负责io操作，将io数据->内核->用户空间的缓冲区之后通知应用程序。整个数据拷贝过程是完全由os来执行，用户程序不参与任何操作，等待os发起通知。
2. 同步io: 用户发起io请求后，要将数据从内核空间拷贝到用户程序的缓冲区。
3. 阻塞/非阻塞: 用户发起io请求后，会立即返回信息标志，表示请求条件是否满足。是非阻塞的。
```

```
Unix的五种io模型
Blocking I/O
No-Blocking I/O
I/O Multiplexing
Sigle-Driven I/O
Asynchronous I/O  --> 异步io
```

![io](io.png)

```
Netty中，在5.0版本引入了AIO，但是因为重大Bug撤回了该版本，目前广泛使用的4.0版本仍然使用的同步非阻塞IO。
```

#### 2. 什么是Netty

```
Netty是一个网络程序编程框架，Netty简化和流程化了网络应用开发，通过使用netty可以快速开发出一个网络应用。
```

+ 为什么选择netty

```
1.使用简单: API简单，开发友好
2.功能强大、性能高。
3.成熟稳定
4.社区活跃
5.应用案例丰富。在GRPC、Doobo等商业应用上获得了成功。
```

+ Netty都优化了哪些步骤

```
1. Netty为我们提供了成熟的线程模型。
2. 为我们实现了很多方式的编码器解码器等
3. 对于IO操作，Netty已经封装好，我们在编码时候无需关心。
```

#### 3. Reactor模型

```
Reactor模型的中心思想是将要处理的I/O请求注册到一个中心的多路复用器上，并将主线程阻塞在多路复用器上，当有对应的io事件到达时，多路复用器将io事件通过事件分发器分发到对应的时间处理器上进行对应的IO事件处理。
+ 事件分发器
+ 多路复用器
+ 事件处理器
+ 事件: io事件
```

+ Reactor线程模型

```
1. 单Reactor单线程模型：acceptor和handler处理都在单个线程中，当handler阻塞时候，其他handler无法执行，同时也会大致acceptor无法处理io请求。
2. 单Reactor多线程模型: 启用专门的线程来处理io事件，将io事件的处理分发到线程池中负责。
3. 多Reactor多线程模型: 如果有大量的io事件，增加处理io事件的线程数，-> 多Reactor多线程模型。
```

+ Netty中Reactor的实现

```java
// 1:1
EventLoopGroup boss = new NioEventLoopGroup(1);
ServerBootstrap bs = new ServerBootstrap();
bs.group(boss);
// 1:N n = 核心数*2
EventLoopGroup boss = new NioEventLoopGroup(1);
EventLoopGroup worker = new NioEventLoopGroup();
bs.group(boss,worker);
// N:N 
EventLoopGroup boss = new NioEventLoopGroup();
EventLoopGroup worker = new NioEventLoopGroup();
bs.goup(boss,boss);
```

+ 业务线程池

```
原因: 如果在Reactor模型线程中进行业务处理，那么这个线程可能会被阻塞，造成对io读写速率下降，影响性能。
解决: 加入业务处理线程池，Reactor模型的中的线程只负责io事件的处理和io数据的读写，将之后的业务逻辑抛出到另外的线程池中解决。
```

#### 4. Netty的核心组件

+ Bootstrap & ServerBootstrap

```
这两个分别是客户端和服务端的启动器。通过使用Bootstrap,编码人员更加方便的组装和配置Netty应用程序。
```

```java
// ServerBootstrap
ServerBootstrap bs = new ServerBootstrap();
EventLoopGroup boss = new NioEventLoopGroup():
EventLoopGroup worker = new NioEventLoopGroup():

b.group(boss,worker)
 .channel(NioServerSocketChannel.class)
 .option(ChannelOption.SO_BACKLOG,100)
 .handler()
 .childHanlder();
ChannelFuture f = b.bind(PORT).sync();
// Booostrap
Bootstrap bs = new Bootstrap();
EventLoopGroup group = new NioEventLoopGroup();
b.group(group)
 .channel(NioSocketChannel.class)
 .option(ChannelOption.TCP_NODELAY,true)
 .handler();
ChannelFuture f = b.connect(HOST,PORT).sync();
f.channel().closeFuture().sync();
```

+ Channel & NioSocketChannel & NioServerSocketChannel

```
Chnnel是对网络操作的抽象，包括了基本的IO操作:bind、connect、read、write。
```

```java
// Netty实际使用中，很少接触对Channel的直接读写，都是通过对ByteBuf的操作，剩下有Netty完成。
public class RpcNettyServerHanlder extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceOf ByteBuf){
            .....
        }
    }
}
```

+ ByteBuf

```
字节缓冲区，对标的是NIO中的ByteBuffer，提供了更舒服的API和更强大的功能。
```

+ EventLoop & EventLoopGroup

```
EventLoop: 用来出里连接生命周期中发生的事件，简单来说就是负责监听网络事件和进行IO事件的处理。
Channel是Netty网络操作的抽象，EventLoop负责处理注册到其上的Channel的IO事件操作。
EventLoop:Thread = 1:1
EventLoop:Channel = 1:N 同一个Channel只能绑定同一个EventLoop
```

+ ChannelHanlder

```
CannelHanlder是注册到pipeline上，处理Channel上的事件。
1. ChannelInboundHanlder 处理入站事件  Decoder
2. ChannerOutboundHanlder 处理出站事件 Encoder
```

+ ChannelPipeline

```
ChannelHanlder会注册在ChannelPipeLine上进行链式调用，数据在管道中流动。
```

#### 5. Netty处理拆包和粘包 [参考博客](https://enplee.github.io/2021/06/07/%E4%BB%8E%E9%9B%B6%E5%AE%9E%E7%8E%B0RPC%E4%B9%8B%E4%BC%A0%E8%BE%93%E5%8D%8F%E8%AE%AE%E4%B8%8E%E6%8B%86%E5%8C%85%E7%B2%98%E5%8C%85/)

+ 什么是拆包和粘包，怎么发生的
+ 如何解决，Netty怎么实现

#### 6. 心跳机制和长连接  [参考博客](https://enplee.github.io/2021/06/07/%E4%BB%8E%E9%9B%B6%E5%AE%9E%E7%8E%B0RPC%E4%B9%8B%E8%AE%BE%E8%AE%A1%E5%BF%83%E8%B7%B3%E6%9C%BA%E5%88%B6/#more)

+ TCP的长连接和短连接
+ 心跳机制、TCP和Netty的心跳机制对比
+ Netty心跳机制实现

#### 7.Netty的零拷贝机制

+ 什么是零拷贝

```
零拷贝指的是不用将buffer从一处内存copy到另外一处的内存，减少一次内存拷贝时间，可以减少CPU的开销。
在OS层面来说，零拷贝可以指buffer数据不用在用户态和内核态之间来回copy数据。
比如linux中的mmap系统调用.https://zhuanlan.zhihu.com/p/69555454,常规read()write()系统调用，需要将data先copy到内核的缓冲区然后在copy到用户态，mmap通过将磁盘文件直接在用户空间进行映射，减少了一次内存copy，提高了性能。
```

+ Netty如何实现零拷贝

```
Netty所实现的零拷贝指的并不是OS级别的零拷贝，而是完全在用户态java层面的优化，偏向于优化数据操作。
```

```
1. CompositeBytebuf可以将多个Bytebuf合并为逻辑上的一个Bytebuf。减少了ByteBuf之间的copy。
2. wrap()操作可以将bytep[]、Bytebuffer等对象包装成ByteBuf对象，避免拷贝。
3. ByteBuf支持slice操作，可以多个ByteBuf共享一个存储buffer，避免了内存copy
```

```java
byte[] bytes = ...
    
ByteBuf bf = Unpooled.buffer();
bf.writeBytes(bytes); // 传统方式产生了copy
ByteBuf zeroCopy = Unpooled.wrappedBuffer(bytes);//减少copyUnpooled提供很多重载包装方法
```

#### 8. 聊一聊序列化协议

+ 什么是序列化、反序列化
+ 序列化协议的选型
+ 常见的序列化方案



