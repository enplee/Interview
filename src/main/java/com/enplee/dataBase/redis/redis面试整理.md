## redis面试整理

### 一. 什么是redis(为什么要用redis做缓存)

<details> <summary>展开查看</summary>
<pre><code>	redis是用C编写的Nosql的K-V型内存数据库。
	redis读写速度非常快，所以广泛应用于缓存方案，支持事务、持久化、LRU驱动时间、集群方案。。</code></pre> 
</details>

+ 为什么使用缓存，为什么用redis 使用redis的考虑

<details> <summary>展开查看</summary>
<pre><code>	现在web应用的访问，大多数都是读请求，读操作的次数远超于写次数，如果直接读数据库，不仅慢而且大量链接打到数据库可能造成db崩溃。
	使用redis作为缓存，redis内存存储的特性，使得查询十分快速，同时降低数据库的压力。
	由于内存容量的限制，redis中缓存的数据一般是常用和主要的数据，还要考虑:
	1.存储的数据访问命中率如何，如果命中率很低，没必要写入缓存
	2.如果写入操作多，那么没必要使用缓存
	3.数据的大小，如果很大也没必要存在内存中。</code></pre> 
</details>

### 二. 使用redis缓存造成的问题

+ 缓存雪崩(什么是缓存雪崩，怎么解决)

<details> <summary>展开查看</summary>
<pre><code>	what:
	缓存雪崩是指redis服务宕机或者redis中的缓存在同一时间大量失效，造成访问流量全部打到了数据库中，造成数据库压力陡增，并造成雪崩一样的连锁反应，甚至造成数据库大面积宕机。
	why:
	redis服务宕机、缓存数据同时失效
	how：
	1. redis本身避免设置统一的失效时间，设置随机的失效时间。
	2. 事发前，使用redis集群，保证redis高可用，尽量不宕机。
	3. 事发中，积极采用限流策略，减少打到数据库的流量。
	4. 事发后，根据redis持久化功能，快速恢复数据。</code></pre> 
</details>

+ 缓存击穿(什么是缓存击穿，怎么解决)

<details> <summary>展开查看</summary>
<pre><code>	what:
	查询一个不存在与数据库中的数据(非法数据等)，缓存一定没有命中，请求透过了redis到了数据库中，造成数据库存在压力
	why:
	请求中包含大量不存在数据库中的数据，走了数据库
	how:
	1. 前端尽量做数据校验，抱枕数据的合法性，拦截一部分数据。
	2. 缓存不存在的key，设置较短的过期时间。
	3. 使用过滤器(布隆过滤器和布谷鸟过滤器)进行访问过滤</code></pre> 
</details>

+ 双写一致性的问题

<details> <summary>展开查看</summary>
<pre><code>	what:
	update操作，数据库和redis中的数据都涉及写操作，双写操作如何保证数据的一致性。
	how:
	Cache aside partten 先写数据库 再删缓存</code></pre> 
</details>

### 三. redis特性

+ redis为什么这么快

<details> <summary>展开查看</summary>
<pre><code>	1. 内存数据库，操作在内存上进行，比传统磁盘数据库要快(随机存取慢，顺序存取性能不错)
	2. 单线程模型，无锁竞争，没有线程的上下文切换
	3. i/o多路复用模型，非阻塞式io: 单线程高效处理网络请求
	4. 高效的数据结构，大量的优化结果。</code></pre> 
</details>

+ 为什么redis是单线程 [参考链接](https://draveness.me/whys-the-design-redis-single-thread/)

<details> <summary>展开查看</summary>
<pre><code>	1. redis绝多数操作的性能瓶颈不是CPU，redis性能瓶颈最可能是机器内存容量或者网络带宽。
	2. redis采用单线程+多路复用器完全可以并发的处理客户端的请求
	3. 增加多线程机制反而增加了开发和维护成本。
	后续引入了多线程，也是在局部删除大键值对操作，降低redis主线程阻塞的时间，提高执行效率。</code></pre> 
</details>

+ 为什么要为缓存数据设置过期时间

<details> <summary>展开查看</summary>
<pre><code>	1. 有助于缓解内存的消耗。
	2. 有些消息本身就有时效性，比如验证码，正好可以通过缓存过期来实现。</code></pre> 
</details>

+ redis如何判断数据过期？对于过期数据如何删除？

<details> <summary>展开查看</summary>
<pre><code>	redis中维护了一个过期字典，字典的key指向redis中的key，值是一个longlong类型的unix时间戳。
	删除策略:
 	1. 惰性删除: 只有在查询key的时候，才会进行过期时间比较，然后删除。
 	2. 定期删除: 基于统计意义，每段时间抽取一定的key，知道抽取key中的过期key小于一定的比例停止抽取，否则一直抽取删除操作。</code></pre> 
</details>

+ redis内存淘汰策略

<details> <summary>展开查看</summary>
<pre><code>	通过过期数据删除策略还不够，redis内存可能会发生溢出。
	1. no-eviction 禁止驱逐数据
	2. volatile-ttl 从设置了过期时间的数据中淘汰将要过期的
	3. volatile/allkeys -lru 使用lru策略 从设置了过期时间、全部key中淘汰
	4. volatile/allkeys -random 随机淘汰
	5. valatile/allkeys -lfu 使用lfu使用频率淘汰</code></pre> 
</details>

### 四. redis数据结构

+ String

```
Redis的字符串如果保存的对象是整数类型，那么就用int存储。如果不能用整数表示，就用SDS来表示，SDS通过记录长度，和预分配空间，可以高效计算长度，进行append操作。
```

```
String类型是redis中最常见的类型。内部实现通过SDS(Simple Dynamic String)实现。
底层实现:SDS 动态字符串类似List
与C原生的字符串相比:
 + 增加len字段表示String长度
 + 实现内存空间预分配和惰性释放
 + 自动进行扩容
 + 存储无限制，二进制安全，c原生只能存Ascii, \0就认为结束了。

常用命令:
set/get
strlen/exists
incr/decr # 支持计数器操作
expire/setex
```

+ List

```
list是链表，c语言中没有实现链表，redis的list实现为双向链表linkedList。
底层实现: zipList+linkedList -> quictList
 + 早期版本是zipList+LinkedList，数据量小的时候使用zipList，之后自动转换成linkedList。
 + linkedList的空间成本较高，替换成了quicklist: 由zipList组成的双向队列。
 
常用命令:
lpush/lpop
rpush/rpop
lrange/llen # lrange遍历list llen长度

应用:
 + 消息队列 blpop brpop 阻塞式
 + 文章列表或者数据分页 lrange支持范围查询
```

+ Hash

```
hash类似于HashMap, string类型的key和value的映射表。
底层实现: 数组+链表

常用命令:
hset/hget
hkeys/hvals
hgetall/hmset
hexists

应用: 
 + 可以用来存储对象，直接通过file修改对象的值。
```

+ Hash的底层实现和扩容与rehash

```
Hash的底层实现是字典，通过桶+拉链法实现。
正常状态下，字典维护一个Hash表，当周期函数检测到数据量超过负载因子就会触发扩容，此时会创建一张更大容量的新表，然后将当前hash设定为扩容状态，然后渐进式reHash。
```

```
Rehash过程:渐进式reHash。
生成扩容的新表后，不会立即将所有的元素迁移到新表。而是伴随着每次增删改查操作和周期函数操作将数据迁移。
在一段时间后，表0上的数据被全部迁移到了表1上，交换表0和1的指针，同时状态设为操作完成。
如果状态在reHash期间，发生读写:
1. 增加优先在1增加
2. 查首先在0上查，然后去1上查
3. 先查后改
4. 删除先查后改
```

+ set

```
set是无序集合，自动去重。

常用命令:
sadd/spop
smembers/sismember
scard/sinterstore/sunion

应用:
+ 去重 
+ 交集 并集 差集 共同关注、共同粉丝等
```

+ Sorted Set

```
排序set，去重根据score排序。

实现方式: 跳表
https://blog.csdn.net/qq_24950043/article/details/118305731

常用命令:
zadd/zcard
zscore/zrange
zrevrange/zrem

应用场景: 需要对数据进行去重排序的场景
实时排行信息: 直播间礼物排行榜，在线用户列表，弹幕消息等
```

#### 高级结构

+ Bitmap

```
bitMap, 连续的二进制数组，类似于操作系统里位示图。用一个位来表示状态。

常用命令: setbit/getbit bitcount/bittop

应用场景:
1. 保存状态，登录状态、签到状态、用户行为分析
```

+ BloomFilter

```
布隆过滤器，用来帮助判断元素是否存在

命令：
bf.add/bf.exists

使用场景：
1. 抖音推送去重，拦截已经推荐过得。
```

+ pub/sub

```
1-N的消息队列
```

### 五. 持久化

+ RDB

```
RDB是通过快照的方式实现持久化，在某个时间节点保存当前内存中数据的副本。是redis默认的持久化方式。bgSave()、save(),save会阻塞redis主进程已经被弃用。

底层原理: 调用内核调用fork(),创建出一个子进程，子进程和主进程共享相同的物理内存(都是虚拟内存做一下映射就好)，使用CopyOnWrite，主进程修改了内存，内核会复制一份新的物理内存给子进程。
```

+ AOF

```
AOF是另外一种持久化方式，通过追加写操作的方式。RDB保存的数据有时效性，不一定是最新的数据，会丢失一定时间间隔的修改。AOF会记录redis的每一条操作。
AOF重写机制:
为了缓解AOF不断累积的指令，AOF提供的重写机制，进行指令的压缩。
AOF持久化选项:
appendfsync always # 一旦数据发生修改
appendfsync everysec #每秒进行
appendfsync no #交给系统
```

+ 混合持久化

```
RDB不准确，AOF准确但是量大恢复慢，所以使用混合持久化。 RDB镜像+之后的AOF操作。
重启之后，先通过RDB恢复，然后运行AOF中的所有操作，实现快速恢复。
```

### 六. 集群

[参考链接](https://mp.weixin.qq.com/s?__biz=Mzg5MzU2NDgyNw==&mid=2247487143&idx=1&sn=b095c730e2180d7461c6c0aaa55f495f&source=41#wechat_redirect)

[整理面试比较全面的文章](https://mp.weixin.qq.com/s?__biz=Mzg5ODU2ODczMQ==&mid=2247485345&idx=1&sn=256268ef3ea7237998d38da60a0fc3cc&chksm=c061c0d5f71649c3a803d2517f1b3673676b598421aaa16421b7b3aefed0b142c4b199f1bde4&scene=132#wechat_redirect)

+ 主从复制+读写分离的好处

```
将以太redis节点的数据复制到其他节点。主节点负责读写,从节点承担读,同时在主节点挂掉后,从节点可以替代主节点。
1. 数据冗余,实现了数据的热备份
2. 故障恢复,主节点出现问题,从节点可以承担主节点的工作。服务的冗余。
3. 负责均衡,主从复制的基础上配合读写分离,主节点承担读写工作,从节点分担读请求,提高redis可用性。
```

+ 具体实现

```shell
127.0.0.1:6380> SLAVEOF ip+port
1. 在配置文件中配置 slaveof ip port
2. 启动命令中配置参数 --salveof ip port
3. 使用客户端的命令 slaveof ip port
```

+ 主从同步实现原理

```
Master <--- 1. 接受slaveof命令,向master发起TCP连接建立请求 --- Slave
Master ---             2. 双方建立连接			  	   --> Slave
Master <--- 3. slave发送Ping命令,验证对方是redis实例      ---- slave
Master --- 	4. master恢复Pong命令表示同意				  ---> Salve
Master <--- 5. slave 发送Sync同步命令,请求数据同步         ----> salve

1.Master 将client添加进slave列表,并执行bgSave指令启动子线程保存RDB文件

Master ----   6. 发送RDB文件数据							----> slave
```

#### redis Sentinel哨兵

+ 哨兵节点

```
哨兵节点: 特殊的redis节点,负责自动化的故障恢复功能：
1. 监控: 哨兵节点会通过心跳包不断检查主节点和从节点的状态
2. 故障转移: 发现主节点不能正常工作时,进行自动故障转移,选择从节点升级为主节点,其他从节点跟随新主节点
3. 配置提供者: redis客户端初始化时,通过哨兵系统获得redis服务的主节点。
4. 通知: 哨兵将故障转移结果通知给客户端。
```

+ 快速故障恢复

```
1. 定时任务: 每个哨兵维护了三个定时任务: 定时获取最新的主从结构/获取其他哨兵节点的信息/ping命令检测所有节点的状态。
2. 主观下线: 定时心跳检测时,如果一个节点心跳超时,当前哨兵节点就会对其主观下线。如果该节点是主节点,出发sentinel is-master-down-by-addr命令询问状态。
3. 客观下线: 如果判断主节点下线的数量达到了配置的数值,就会对主节点进行客观的下线。
----------------- 故障转移 --------------
4. 选举哨兵领导: 主节点被判断客观下线之后,哨兵节点会进行协商,通过Raft算法,先到先得的方式选举出领导者。
5. 故障转移: 通过选举出的领导者进行故障的转移；
	+ 选择主节点
	+ 更新主从关系
	+ 将下线的主节点设置为从节点。
```

+ 主节点的选择依据:

```
1. 过滤掉不健康的节点,比如: 主观下线的,心跳包恢复间隔大于一定时长的等。
2. 选择优先级高的节点,如果优先级选不出,看复制偏移量,最后选id最大的。
```

+ 哨兵应用的注意事项和局限性

```
1. 哨兵的数量应该>1,且分布在不同的物理机上。
2. 哨兵节点应该是奇数,方便投票和决策。
3. redis对客户端的通知和配置提供需要客户端的实现。

局限性:
1. 无法对从节点进行故障转移
2. 无法进行写操作进行负载均衡
```

#### redis cluster集群

```

```

#### redis实现分布式锁

+ 原子操作提供互斥能力

```
SETNX: SET IF NOT EXISTS 
DEL lock: 释放锁
问题: 如果持有锁的服务还没有释放锁就挂掉了,产生了死锁。
```

+ 避免死锁

```
expire: 设置过期时间,一个服务长时间不释放锁仍然会过期
SET lock 1 EX 10 NX: redis底层保证了指令对于set和ex操作的原子性
问题: 
	1. 过期时间难以预估,太短不够,太长资源浪费。
	2. 这个锁所有服务都可以通过DEL进行释放。
```

+ 锁释放权限

```
将lock添加标识,释放锁要先检查标识,比如线程id等。
SET lock uuid ex 10 nx.
所有的服务在释放锁的时候,都要先check当前锁是不是自己持有,然后决定释放。
问题: 保证check和释放操作的原子性
定义lua脚本,交给redis保证锁释放操作的原子性。
```

+ 锁过期时间评估

```
优先分配一个较短的过期时间,开启一个守护线程,定时检测锁的失效时间,如果锁快过期了,但是资源操作还没完成,对锁进行续期.

Java的线程分为两种：User Thread(用户线程)、DaemonThread(守护线程)。

只要当前JVM实例中尚存任何一个非守护线程没有结束，守护线程就全部工作；只有当最后一个非守护线程结束是，守护线程随着JVM一同结束工作，Daemon作用是为其他线程提供便利服务，守护线程最典型的应用就是GC(垃圾回收器)，他就是一个很称职的守护者。
```

+ redis复制之后的安全

```
在主从集群中,主从切换之后会造成锁失效的问题: 答案redLock
```



