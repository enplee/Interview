### zookeeper面试准备

#### 一 什么是zookeeper

​		处理分布式协调，基于Znode的文件存储和监控机制。 

	##### Znode：Zookeeper整体类似树形文件系统，节点为Znode，每个Znode被路径唯一表示。

+ 持久节点：创建的节点会一直存在，只有主动删除。
+ 临时节点：伴随着cli的session，cli连接断开，临时节点删除。
+ 序列节点(概念)  持久序列节点/临时序列节点

##### Watcher： client可以通过监听器监听zookeeper上的数据变化，一旦变化会同时client，client响应式回调。

+ 监听Znode节点数据的变化
+ 监听Znode节点子节点的增减

Zookeeper通过提供Znode的操作和Watcher实现以下功能。

##### zookeeper功能

+ 统一配置管理：将多系统相同的配置记录在Znode节点上，所有系统监听这个节点数据，及时响应。
+ 统一命名服务：将资源统一命名一个Znode，Znode维护资源，比如为Znode命名一个服务，服务下维护提供服务的IP。
+ 数据的发布订阅：利用Watcher机制，客户端监听Znode节点，数据发布在节点上，client可以动态收到节点的变化推送。
+ 分布式同步(分布式锁)

##### zookeeper shell cli使用

```shell
create [-s] [-e] /node 直接create永久节点 -e 临时节点 -s 顺序节点
get /node
set /node
ls /path   
```

#### 二 zookeeper如何保证高可用性

​		从leader挂掉的不可用状态如何恢复到重新选出新leader的可用状态时间越短越好。

##### 集群角色：

+ leader：提供读写服务，负责通票的发起和决议，更新系统状态
+ follwer：提供读服务，写服务转发给leader。选举中参与投票可以被选举。
+ observer：follower功能，除了投票的功能。

#### 三 为什么Znode只支持少量的数据存储(1M)

#### 四 Zookeeper的特性

+ 顺序一致性
+ 原子性
+ 视图一致性
+ 可靠性
+ 及时性

#### 五 Zookeeper如何解决分布式一致性

+ CAP定理

```
C: Consistency	一致性
A: Availability	可用性
P: Partition tolerance 分区容错

P: 区间通信出错可能会发生，无法避免，只能被迫接受。造成了无法同时实现C&A。想一致，就要同步锁定，锁定就失去了可用性。
C: 指的是分区之间的数据保持一致。
A: 表示一旦发生请求，会马上获得回复。

一致性和可用性之间的权衡:
网页资源这种对一致性要求不高，但是可用性要求高
设计财务等数字对一致性要求高，只能牺牲可用性。
```

+ paxos理论

```
角色:
提案者: 发起提案，并附带一个全局递增的提案号。将提案发送给所有的表决者。
表决者: 负责投票，本地会记录一个当前最大提案号MaxN，只会接受大于这个提案号的提案，并更新本地提案号。
接受提案的表决者 > n/2+1 提案通过

死循环问题: 都发提案，交替递增，永远无法通过。 ——————> zookeeper leader 只能一个人提案
```

+ 两阶段式提交
+ ZAB Zookeeper原子广播协议

```
原子: 要么成功 要么失败 无中间状态
广播: 分布式多节点全部知晓 
队列: FIFO 顺序的发送消息 由TCP协议保证了 follower接受广播的有序性。
```

```
1. clit发送写请求到任意节点，节点将写请求转发给leader
2. leader将写请求写入FIFO队列进行广播，所有节点进行投票，超过半数投票通过。
3. leader将正式写写入队列进行广播，所有节点进行修改。同时更新事务ID:Zxid。
```

```
崩溃恢复之后的数据一致性:

```



#### 六 Zookeeper如何解决高可用性(leader 选举)

背景：每个节点维护了Zxid和Myid，Zxid代表了事务id，Zxid越大表示节点的信息越新。Myid是节点固定的id。

+ 初始化选举leader

```
初始化选举比较简单，因为没有数据和事务，Zxid都是0.
选举出来的leader是按照启动顺序，前n/2+1个节点中，Myid最大的。
```

+ leader崩溃，重新选举

```
leader使用中崩溃，由于已经存储了信息，那么选举就要选择最新数据的节点。
1. 3888端口实现了两两之间互相通信
2. 任何人发起投票，都会触发准leader发起自己投票
3. 推选逻辑：Zxid最大，Myid相同
```

#### 七 Zookeeper如何实现分布式锁

```
1. 争抢锁，只有一个人获得锁
	根据Znode节点的唯一性，所有client统一创建相同Path，Znode。保证只有一个节点抢到锁。可以做doubleCheck，先查询再创建。
2. 获得锁使用临时节点 session
	防止抢到锁的线程突然宕机，无法主动释放锁，创建节点使用基于session机制的临时节点。持有锁的client断开，节点删除，锁释放。
3. 持有锁的人，释放锁:
	只要client的session断开，临时节点被删除，锁得以释放。
4. 锁被释放，删除，其他cli如何知晓。
	方案一: 轮询+心跳的方式 缺点: 时延+压力
	方案二: watch 临时节点的变化 优点: 及时回调 缺点: 回调所有clit，所有clit同时抢锁
	方案三: watch + 顺序节点: 
		所有clit按照顺序创建临时顺序节点，节点id最小的获得锁，其余的排队，监听比前一顺序节点。
		优点: 响应机制快速 队列中每回只有一个clit被callback。
5. 可重入锁能设计么？ 节点添加线程信息就行了
```




