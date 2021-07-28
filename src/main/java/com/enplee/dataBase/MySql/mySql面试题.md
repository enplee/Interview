## MySql面试题

### 一. 索引(索引的实现，比较红黑树，B树，B＋树，HashMap)

+ 什么是索引，优缺点(使用索引会有哪些优缺点)

```
索引是一种用来快速查询和数据查找的数据结构，常见的索引结构有: Hash Btree B+tree
优点: 加速检索速度，索引诞生的愿意，通过索引唯一性保证数据唯一性
缺点: 查询快，增删改需要维护成本，同时占用一定的磁盘空间
```

+ 索引的分类，优缺点

```
1. Hash索引 kv索引
通过hash值来对应桶，通过拉链法等处理冲突。查询时间复杂度O(1)
缺点: 不支持顺序和范围查询
2. B/B+ 树
B树是多路平衡查找树，B+树在B树的基础上增加了叶子节点的指针，方便做范围查询。
```

+ 红黑树 B树 B+树(MySQL 为什么使用 B+ 树来作索引，对比 B 树它的优点和缺点是什么)

```
B树和B+树
1. B树所有的节点既存储数据有存储key，B+树只有叶子节点存储值，其他节点只存放key
2. B+树增加了指针指向相邻的叶子节点，方便做范围查询
3. B树的存储导致了可能检索的效率不稳定，而B+树都是从跟节点到叶子节点，效率稳定
红黑树 B+树
```

### 二. 索引类型

+ 什么叫主键索引

```
数据库表中主键使用的索引就是主键索引，一张表只能规定一个主键，不能为null，不可重复。
没有主键，会检查有没有唯一索引的字段，有认为为主键，否则穿件一个自增的主键

PRIMARY KEY索引和UNIQUE索引非常类似。事实上，PRIMARY KEY索引仅是一个具有名称PRIMARY的UNIQUE索引。这表示一个表只能包含一个PRIMARY KEY，因为一个表中不可能具有两个同名的索引.
```

+ 辅助索引(唯一索引与普通索引的区别是什么？)

```
唯一索引、普通索引、前缀索引属于二级索引
唯一索引: 唯一索引存在约束，一个表只允许一个唯一索引，不可重复，允许数据为NULL
普通索引: 一张表可以有多个普通索引，可重复，可为NULL。
```

+ 聚集索引与非聚集索引(聚簇索引和非聚簇索引有什么区别？什么情况用聚集索引)

```
1. 聚集索引
索引结构和数据一起存放的索引。InnoDb上的主键索引，叶子节点存的树真正的数据。
缺点：直接关联数据，更新代价大。
2. 非聚集索引
非聚集索引叶子节点存放的不是真是真实的数据，而是指向数据的指针或者主键。 相当于索引和数据是分离的。
非聚簇索引缺点？  非聚簇索引最大的问题是会产生回表，也就是最后还是要到表中做一次查询
一定会发生回表？  不一定，如果发生了索引覆盖，比如查询的就是key，那么不需要回表
3. 索引覆盖
就是索引中的key本身就是需要查询的内容,此时就发生了索引覆盖。
索引覆盖的应用: 实际应用中可以将热点查询字段按照联合索引的方式构建,这样就能通过索引覆盖提高查询效率。
```

+ 联合索引和最左匹配原则

```
• 联合索引就是多个字段联合建立索引，排序根据就是第一列优先级>第二列优先级>第三列优先级
• 最左匹配原则是：联合索引的三个键 只有1 12 123组合可以被查出来，尽量选1为最频繁键
```

+ 索引失效的情况**

```
1. 使用like进行模糊查询时，且%在前缀，使用or关键字，如果没有联合索引或者都有索引，就不会走索引。
2. 联合索引，查询不是1,12,123
3. 对查询字段进行运算或者使用函数，不会触发索引
4. mySql优化器认为全表扫描优于走索引，索引失效。
```

#### 代码

+ 创建索引

```sql
// 1. alter table xxx add index xxx
ALTER TABLE table ADD INDEX (col)
ALTER TABLE table ADD INDEX (col1,col2,col3) # 组合索引
ALTER TABLE table ADD UNIQUE (col)
ALTER TABLE table ADD PRIMIARY KEY (col)

// 2. create index
CREATE INDEX index_name ON table_name (col)
CREATE UNIQUE INDEX index_name ON table_name (col)
```

+ 查找和删除索引

```mysql
SHOW INDEX FROM tablename;
ALTER TABLE table DROP INDEX/UNIQUE/PRIMARY KEY
```

### 三.存储引擎

+ MySql存储引擎的分类,区别 https://blog.csdn.net/suifeng629/article/details/106310027

```
1. MyISam
原始的默认引擎，不支持事务和行级锁，崩溃后无法安全恢复。
2. InnoDB
现在MySql的默认存储引擎，支持Redolog恢复，行级锁，事务，MVCC并发版本控制。
```

+ mvcc并发版本控制原理

```
快照读+递增的事务ID
https://www.cnblogs.com/myseries/p/10930910.html
```

+ 一条SQL语句执行过程

```
|连接器 缓存?  	  		 |
|分析器 优化器 执行器	  |  ---------> {存储引擎} ----------> 物理存储
|server 层_______________|
```

```
1. 连接器: 负责数据库登录，权限、安全验证
2. 数据缓存，已经取消
3. 分析器: 进行词法分析和语法分析
4. 优化器: 选择最优的执行方案
5. 执行器: 执行方案，返回从存储引擎中取得数据。
```

+ log( WAL知道吗？哪个log用了这个？那顺便介绍下三大log)：   [参考文章](https://mp.weixin.qq.com/s?__biz=MzAwNDA2OTM1Ng==&mid=2453141708&idx=1&sn=679b1e2755da2cf20904928242d32411&scene=21#wechat_redirect) [参考文章](https://www.cnblogs.com/ZhuChangwu/p/14096575.html)

```
Binlog: 逻辑日志，在server层追加式的记录所有的写操作，并以二进制的形式保存在磁盘中，用于主从复制。属于Server层。
redolog：属于InnoDB存储引擎的，保证事务的安全。事务未提交前，记录事务的修改。保证数据即使crash也能顺利写到磁盘。
undolog: 保证事务的原子性，记录事务发生之前的版本，记录了反向的操作，用来实现回滚。

WAL: write ahead log 日志现行技术 先写日志再落盘 保证数据完整写入磁盘

binlog和redolog的区别？
1. binlog写入的是每次数据库的操作，也就是sql语句，属于逻辑操作日志。redolog写入的是具体页的修改，属于是存储的物理修改
2. binlog记录的是全量的修改，追加写不会覆盖; redolog只是为了记录某个事务的物理修改，是可覆盖的。
3. binlog通常用来做主从同步的数据恢复，redolog用来保证事务持久性。
4. binlog是server层的，redolog只属于InnoDB存储引擎。
```

+ 了解MySql的log的两阶段提交么？

```
1. 写入 redoLog prepare
2. 写入 binlog
3. 写入 redolog commit

1 先写redolog 后写binlog: 
	binlog没写入就crash, 那么master可以通过redolog恢复，binlog没有，造成主从不一致
2 先写binlog 后写redolog:
	造成slaver比master新
3 二阶段写:
	如果redoLog已经commit，那么bin一定写好了，直接恢复
	如果redoLog是prepare阶段，就看binlog是否完整
	完整，提交
	不完整，事务回滚。
```

+ 一条sql执行很慢，怎么查？分析可能的原因？

```
偶尔很慢:
	数据库在磁盘同步
	表存在锁
一直很慢:
	没有索引
	有索引但是没命中: 运算和函数
	mysql采样出现偏差，优化器不走索引
```



+ explain指令

```
当构建了一个sql语句,我们可以通过explain指令来分析这个sql语句,explain语句可以获得sql语句的执行计划,可以查看有没有走索引的问题。
```

+ count(id)、count(*)、count(1) 统计行数

```
统计行数可以同count(主键)的方式来统计,走主键索引.
count(字段)不会统计到字段为null的情况。
count(*)和count(1)在5.6之后的mySql会做优化,走优化器认为最快的语句。
```

