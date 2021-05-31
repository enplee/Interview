### JUC之高级应用

### 一 并发List类CopyOnWriteArrayList

List的线程安全版本是CopyOnWriteArrayList，使用了写时复制的策略。copyOnwrite指的是：读操作所有线程共享读，一旦发生写操作，复制数据镜像进行同步修改，然后将修改数据替换原来的数据。

```java
//维护了一个Obj[]
private transient volatile Object[] array;
//get和set方法提供写时复制的支持
final Object[] getArray() {
        return array;
}
final void setArray(Object[] a) {
        array = a;
    }
```

##### CopyOnWriteArrayList::Add() ::Set() 源码

```java
    public boolean add(E e) {
        // syn 之后 复制数组 add元素 修改array引用
        synchronized (lock) {
            Object[] es = getArray();
            int len = es.length;
            es = Arrays.copyOf(es, len + 1);
            es[len] = e;
            setArray(es);
            return true;
        }
    public E set(int index, E element) {
        synchronized (lock) {
            Object[] es = getArray();
            E oldValue = elementAt(es, index);

            if (oldValue != element) {
                es = es.clone();
                es[index] = element;
            }
            // Ensure volatile write semantics even when oldvalue == element
            setArray(es);
            return oldValue;
        }
    }
```

##### CopyOnWriteArrayList::Get() ::Iterator()

```java
    public E get(int index) { // get uncheck
        return elementAt(getArray(), index);
    }
    static <E> E elementAt(Object[] a, int index) {
        return (E) a[index];
    }
// 	copyOnWrite会产生数据弱一致性问题，A线程get，B线程remove，因为B是在copyArray上修改，可能造成，B删除了A仍然能读取到的问题。

    public Iterator<E> iterator() {
        return new COWIterator<E>(getArray(), 0);
    }
// COWIterator 构造器
        COWIterator(Object[] es, int initialCursor) {
            cursor = initialCursor;
            snapshot = es;
        }
// iterator也是弱一致性，迭代器持有的是生成时候的快照，后续的修改对iterator都不可见
```

### 二 LockSupport工具类

LockSupport是个工具类，主要提供线程的挂起和唤醒。LockSupport是创建锁和其他同步器的基础。

##### LockSupport :: park() ::unpark() ::parkNanos()

```java
public class LockSupportPrint {
    static Thread t1 = null, t2 = null;
    public static void main(String[] args) {
        String s1 = "123456789";
        String s2 = "abcdefghi";
        t1= new Thread(()->{
            for(char s : s1.toCharArray()) {
                System.out.println(s);
                LockSupport.unpark(t2);
                LockSupport.park();
            }
        },"t1");
        t2 = new Thread(()-> {
            for(char s : s2.toCharArray()) {
                LockSupport.park();
                System.out.println(s);
                LockSupport.unpark(t1);
            }
        },"t2");

        t2.start();
        t1.start();
    }
}
```

##### LockSupport的park、unpark方法和wait、notify的区别：

1. park/unpark和 wait/notify方法都能实现线程的阻塞和唤醒，但是实现机制是不相同的。
2. wait、notify实现唤醒和阻塞的机制是信号，信号会消失，如果在一个线程wait之前对这个线程notify，不会起作用，线程依然会阻塞。
3. park、unpark实现阻塞和唤醒的机制是许可证，线程持有许可证将不会阻塞，所有线程默认不持有许可证，unpark会为线程分配许可证，park会剥夺许可证。如果一个线程在调用park之前存在线程对这个线程unpark(分配许可证)，那么这个线程不会阻塞。
4. 更普遍的：park、unpark可以针对特定的线程操作，而wait、notify要么是当前线程，要么是随机或全部线程。
5. 以及：wait、notify都进入同步块持有锁才能进行阻塞和唤醒操作。

### 三 AQS抽象同步队列

AQS是实现同步器的基础组件，大多数锁通过持有的AQS对象来实现同步功能。

AOS由主要由State、FIFO的双向队列和ConditionObject三个组件组成。

##### 一. 组件

FIIO双向队列的节点是Node，每个节点存放了一个线程，并维护线程在队列中的状态，对节点的操作都是原子的：

```java
    abstract static class Node {
        volatile Node prev;       // initially attached via casTail
        volatile Node next;       // visibly nonnull when signallable
        Thread waiter;            // visibly nonnull when enqueued
        volatile int status;      // written by owner, atomic bit ops by others
		// status: cancelled 线程被取消 signal 线程需要被唤醒 condition 线程在条件队列里等待 propagate 释放时要唤醒其他资源
        // methods for atomic operations
        final boolean casPrev(Node c, Node v) {  // for cleanQueue
            return U.weakCompareAndSetReference(this, PREV, c, v);
        }
        final boolean casNext(Node c, Node v) {  // for cleanQueue
            return U.weakCompareAndSetReference(this, NEXT, c, v);
        }
        final int getAndUnsetStatus(int v) {     // for signalling
            return U.getAndBitwiseAndInt(this, STATUS, ~v);
        }
        final void setPrevRelaxed(Node p) {      // for off-queue assignment
            U.putReference(this, PREV, p);
        }
        final void setStatusRelaxed(int s) {     // for off-queue assignment
            U.putInt(this, STATUS, s);
        }
        final void clearStatus() {               // for reducing unneeded signals
            U.putIntOpaque(this, STATUS, 0);
        }
    }
	// 节点又分为 独占节点 共享节点 和 条件节点
    static final class ExclusiveNode extends Node { }
    static final class SharedNode extends Node { }
    static final class ConditionNode extends Node{ }
```

AQS中维护了一个单一的状态信息：State。在ReentrentLock中，State代表重入次数。CountDownLathc中，代表计数器当前的值。samephore中，代表了可用信号个数。不同的锁可以通过定义State代表的语义来实现不同的功能：

```java
    private volatile int state;
    protected final int getState() {
        return state;
    }
    protected final void setState(int newState) {
        state = newState;
    }
    protected final boolean compareAndSetState(int expect, int update) {// CAS的修改State的状态
        return U.compareAndSetInt(this, STATE, expect, update);
    }
```

AQS中有一个内部类：ConditionObject，实现了Condition接口。每个Condition对象维护一个条件队列，存放调用Condition::await()被阻塞的线程。

```java
    public class ConditionObject implements Condition, java.io.Serializable {
        private static final long serialVersionUID = 1173984872572414699L;
        /** First node of condition queue. */
        private transient ConditionNode firstWaiter;
        /** Last node of condition queue. */
        private transient ConditionNode lastWaiter;
        
        void signal() {}
        void await() {}
        void signalAll() {}
    }
```

##### 二 操作

**State操作**：AQS队列实现线程同步的关键是对State值进行操作。根据State是否属于一个线程，将操作State方式分为独占和共享。

独占方式：acquire()  acquireInterruptibly() release()

独占方式，State和具体线程是绑定的，一旦线程获取了资源，其他线程只能阻塞，被放入阻塞队列挂起。

共享方式：acquireShared() acquireInterruptiblyShared() releaseShared()

共享方式，State不和线程绑定，线程获取资源是与State状态有关，可以多个线程同时执行。

**注意: 在accquire() release() 中不会实现具体的State修改，而是调用tryAcquire()和tryRelease(), 这需要延迟到子类根据功能具体实现 **

```java
    public final void acquireInterruptibly(int arg)
        throws InterruptedException {
        if (Thread.interrupted() ||   
            (!tryAcquire(arg) && acquire(null, arg, false, true, false, 0L) < 0))
            throw new InterruptedException();
    }
```

##### 模板方法 : 上述的tryAcquire() 和 tryRelease() 就是应用了模板方法的设计模式。

```
规定好一些公用方法的主流程、算法而不具体实现，只是对流程进行控制，子类对具体的流程进行定义，这样保证其子类实现也按照流程和算法架构进行实现。
```

```java
    protected boolean tryAcquire(int arg) {
        throw new UnsupportedOperationException();
    }
```

为什么tryAcquire不设计为abstract方法：因为存在独占共享两套模式，如果abstract，那么子类就必须实现两套功能。

**节点操作**: 当一个线程获取锁失败的时候，这个线程会被封装成Node进行插入到AQS的阻塞队列中。

```java
    final void enqueue(Node node) {
        if (node != null) {
            for (;;) {
                Node t = tail;
                node.setPrevRelaxed(t);        // avoid unnecessary fence
                if (t == null)                 // initialize
                    tryInitializeHead();
                else if (casTail(t, node)) {
                    t.next = node;
                    if (t.status < 0)          // wake up to clean link
                        LockSupport.unpark(node.waiter);
                    break;
                }
            }
        }
    }
```

**条件变量支持** signal/await -> notify/wait

基于AQS实现同步队列：NoReentrantLock

```java
public class NoReentrantLock implements Lock {

    private final Sync sync = new Sync();

    @Override
    public void lock() {
        sync.acquire(1);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        return sync.tryAcquire(1);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireNanos(1,unit.toNanos(time));
    }

    @Override
    public void unlock() {
        sync.release(0);
    }

    @Override
    public Condition newCondition() {
        return sync.newCondition();
    }

    private static class Sync extends AbstractQueuedSynchronizer {
        @Override
        protected boolean tryAcquire(int accquire) {
            assert accquire == 1;
            if(compareAndSetState(0,1)){
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        @Override
        protected boolean tryRelease(int release) {
            assert release == 0;
            if(getState()==0){
                throw new IllegalMonitorStateException();
            }
            setState(0);
            return true;
        }

        @Override
        protected boolean isHeldExclusively() {
            return getState() == 1;
        }

        Condition newCondition() {
            return new ConditionObject();
        }
    }
}
```

### 四 ReentrantLock

ReentrantLock是可重入的独占锁，默认是非公平锁，通过出入构造参数实现非公平锁。State==0表示锁空闲，State>=1表示锁被占用甚至重入。

##### ReentrantLock类结构

ReentrantLock实现了Lock接口，同时通过内部持有Sync来实现锁机制，Sync继承自AQS，同时又fairSync和NoFiarSync两个具体实现子类，实现公平锁和非公平锁的tryAcquire()等方法的定制。

```java
public class ReentrantLock implements Lock, java.io.Serializable {
    private static final long serialVersionUID = 7373984872572414699L;
    /** Synchronizer providing all implementation mechanics */
    private final Sync sync;

    abstract static class Sync extends AbstractQueuedSynchronizer {
       
    }
    static final class NonfairSync extends Sync {
        
    }

    /**
     * Sync object for fair locks
     */
    static final class FairSync extends Sync {
        
    }
}
```

**非公平锁** : 每个线程都可以CAS式的修改State的状态。

```java
        final boolean initialTryLock() {
            Thread current = Thread.currentThread();
            int c = getState();
            if (c == 0) {
                if (!hasQueuedThreads() && compareAndSetState(0, 1)) {
                    setExclusiveOwnerThread(current);
                    return true;
                }
            } else if (getExclusiveOwnerThread() == current) {
                if (++c < 0) // overflow
                    throw new Error("Maximum lock count exceeded");
                setState(c);
                return true;
            }
            return false;
        }
```

**公平锁**： 要先判读当前线程是不是在阻塞队列中，如果在可以抢锁，否则阻塞，之后进入阻塞队列。

```java
        final boolean initialTryLock() {
            Thread current = Thread.currentThread();
            if (compareAndSetState(0, 1)) { // first attempt is unguarded
                setExclusiveOwnerThread(current);
                return true;
            } else if (getExclusiveOwnerThread() == current) {
                int c = getState() + 1;
                if (c < 0) // overflow
                    throw new Error("Maximum lock count exceeded");
                setState(c);
                return true;
            } else
                return false;
        }
```

### 五 ReentrantReadWriteLock 读写锁

ReentrantLock是独占锁，但是并非所用应用场景都需要完全独占的，如果读多写少的场景，那么可共享的读写锁性能更高。

读锁是共享锁，只要当前没有线程持有写锁，那么就可以过去到读锁。

写锁是独占锁，只有当没有任何线程加读锁和写锁的时候，才可以加写锁，一旦加写锁，其他线程得不到任何锁。

##### 如何在Lock的State中维护读和写两种状态：使用State的高16位维护ReadState，低16位维护WriteState

```java
        static final int SHARED_SHIFT   = 16;
        static final int SHARED_UNIT    = (1 << SHARED_SHIFT);
        static final int MAX_COUNT      = (1 << SHARED_SHIFT) - 1;
        static final int EXCLUSIVE_MASK = (1 << SHARED_SHIFT) - 1;

        /** Returns the number of shared holds represented in count. */
        static int sharedCount(int c)    { return c >>> SHARED_SHIFT; }
        /** Returns the number of exclusive holds represented in count. */
        static int exclusiveCount(int c) { return c & EXCLUSIVE_MASK; }
```

### 六 ConcurrentHashMap ***[参考]{https://mp.weixin.qq.com/s?__biz=MzAwNDA2OTM1Ng==&mid=2453141985&idx=2&sn=875412f607c18fa6ba6aa0b0862b7fe6&scene=21#wechat_redirect}

#### 1. 多线程下的HashMap

虽然HashMap1.8版本中，该头插法为尾插法解决的多线程情况下死循环的问题，但是HashMap因为没有同步机制，是不能保证线程安全的。

在多线程对统一桶进行put操作是，正常应该是拉链法处理hash冲突，但是如果两个线程都读到了桶为null，那么都会作为头结点造成节点的覆盖。

#### 2. HashTable的缺点

```java
    public synchronized V get(Object key) {
        Entry<?,?> tab[] = table;
        int hash = key.hashCode();
        int index = (hash & 0x7FFFFFFF) % tab.length;
        for (Entry<?,?> e = tab[index] ; e != null ; e = e.next) {
            if ((e.hash == hash) && e.key.equals(key)) {
                return (V)e.value;
            }
        }
        return null;
    }
```

```java
    public synchronized V put(K key, V value) {
        if (value == null) {
            throw new NullPointerException();
        }
        Entry<?,?> tab[] = table;
        int hash = key.hashCode();
        int index = (hash & 0x7FFFFFFF) % tab.length;
        @SuppressWarnings("unchecked")
        Entry<K,V> entry = (Entry<K,V>)tab[index];
        for(; entry != null ; entry = entry.next) {
            if ((entry.hash == hash) && entry.key.equals(key)) {
                V old = entry.value;
                entry.value = value;
                return old;
            }
        }
        addEntry(hash, key, value, index);
        return null;
    }
```

所有的方法都要Synchronized关键字修饰的锁，开销大，性能差。读取操作之间也不需要同步呀。

### 3. ConcurrentHashMap 1.7 

![segement](F:\java\Interview\src\main\java\com\enplee\JUC\image\segement.webp)

使用分段锁思想，将桶分成n个Segment，每个Segment可以看成是一个小的HashMap。对一个Segment加锁的时候，不会影响到对其他Segment的读写。

**filed**

```java
static final int DEFAULT_CONCURRENCY_LEVEL = 16; // 并发级别，就是分段数量
final Segment<K,V>[] segments; // 分段数组，类型是Segment是ConcurrentHashMap的一个静态内部类，类似于HashMap

static final class Segment<K,V> extends ReentrantLock implements Serializable { 
 
 //这是在 scanAndLockForPut 方法中用到的一个参数，用于计算最大重试次数
 //获取当前可用的处理器的数量，若大于1，则返回64，否则返回1。
 static final int MAX_SCAN_RETRIES =
  Runtime.getRuntime().availableProcessors() > 1 ? 64 : 1;

 //用于表示每个Segment中的 table，是一个用HashEntry组成的数组。
 transient volatile HashEntry<K,V>[] table;

 //当前Segment扩容的阈值，同HashMap计算方法一样也是容量乘以加载因子
 //需要知道的是，每个Segment都是单独处理扩容的，互相之间不会产生影响
 transient int threshold;

}
static final class HashEntry<K,V> { // 分段内部维护的类似HashMap的Node节点
 //每个key通过哈希运算后的结果，用的是 Wang/Jenkins hash 的变种算法，此处不细讲，感兴趣的可自行查阅相关资料
 final int hash;
 final K key;
 //value和next都用 volatile 修饰，用于保证内存可见性和禁止指令重排序
 volatile V value;
 //指向下一个节点
 volatile HashEntry<K,V> next;
}
```

**put方法**

先计算出key的hash，通过hash定位Segment的数组下标，再通过hash定位HashEntry数组下标，进行合适位置的插入。

```java
public V put(K key, V value) {
 Segment<K,V> s;
 //不支持value为空
 if (value == null)
  throw new NullPointerException();
 //通过 Wang/Jenkins 算法的一个变种算法，计算出当前key对应的hash值
 int hash = hash(key);
 //上边我们计算出的 segmentShift为28，因此hash值右移28位，说明此时用的是hash的高4位，
 //然后把它和掩码15进行与运算，得到的值一定是一个 0000 ~ 1111 范围内的值，即 0~15 。
 int j = (hash >>> segmentShift) & segmentMask;
 //这里是用Unsafe类的原子操作找到Segment数组中j下标的 Segment 对象
 if ((s = (Segment<K,V>)UNSAFE.getObject          // nonvolatile; recheck
   (segments, (j << SSHIFT) + SBASE)) == null) //  in ensureSegment
  //初始化j下标的Segment
  s = ensureSegment(j);
 //在此Segment中添加元素
 return s.put(key, hash, value, false);
}
```

真正插入是S.put()。首先tryLock非阻塞式的尝试加锁，成功直接开始插入，不成功：调用scanAndLockPu,  自旋方式一致尝试获取锁，如果自旋过多，阻塞式获取锁。

```java
final V put(K key, int hash, V value, boolean onlyIfAbsent) {
 //这里通过tryLock尝试加锁，如果加锁成功，返回null，否则执行 scanAndLockForPut方法
 //这里说明一下，tryLock 和 lock 是 ReentrantLock 中的方法，
 //区别是 tryLock 不会阻塞，抢锁成功就返回true，失败就立马返回false，
 //而 lock 方法是，抢锁成功则返回，失败则会进入同步队列，阻塞等待获取锁。
 HashEntry<K,V> node = tryLock() ? null :
  scanAndLockForPut(key, hash, value);
 V oldValue;
 try {
	/*
	* 获取到了锁，进行插入
	*/
   }
  }
 } finally {
  //需要注意ReentrantLock必须手动解锁
  unlock();
 }
 //返回旧值
 return oldValue;
}
```

**get方法**

get方法就简单多了，直接定位到Segment然后定位到HashEntry。

```java
public V get(Object key) {
 Segment<K,V> s; // manually integrate access methods to reduce overhead
 HashEntry<K,V>[] tab;
 //计算hash值
 int h = hash(key);
 //同样的先定位到 key 所在的Segment ，然后从主内存中取出最新的节点
 long u = (((h >>> segmentShift) & segmentMask) << SSHIFT) + SBASE;
 if ((s = (Segment<K,V>)UNSAFE.getObjectVolatile(segments, u)) != null &&
  (tab = s.table) != null) {
  //若Segment不为空，且链表也不为空，则遍历查找节点
  for (HashEntry<K,V> e = (HashEntry<K,V>) UNSAFE.getObjectVolatile
     (tab, ((long)(((tab.length - 1) & h)) << TSHIFT) + TBASE);
    e != null; e = e.next) {
   K k;
   //找到则返回它的 value 值，否则返回 null
   if ((k = e.key) == key || (e.hash == h && key.equals(k)))
    return e.value;
  }
 }
 return null;
}
```

**size方法**

分段锁机制，size的统计没有普通HashMap那样简单。首先乐观的方式进行统计，如果前后两次统计结果相同，返回。否则继续统计，知道超过次数，强制加锁。

```java
public int size() {
 // Try a few times to get accurate count. On failure due to
 // continuous async changes in table, resort to locking.
 //segment数组
 final Segment<K,V>[] segments = this.segments;
 //统计所有Segment中元素的总个数
 int size;
 //如果size大小超过32位，则标记为溢出为true
 boolean overflow; 
 //统计每个Segment中的 modcount 之和
 long sum;         
 //上次记录的 sum 值
 long last = 0L;   
 //重试次数，初始化为 -1
 int retries = -1; 
 try {
  for (;;) {
   //如果超过重试次数，则不再重试，而是把所有Segment都加锁，再统计 size
   if (retries++ == RETRIES_BEFORE_LOCK) {
    for (int j = 0; j < segments.length; ++j)
     //强制加锁
     ensureSegment(j).lock(); // force creation
   }
   sum = 0L;
   size = 0;
   overflow = false;
   //遍历所有Segment
   for (int j = 0; j < segments.length; ++j) {
    Segment<K,V> seg = segmentAt(segments, j);
    //若当前遍历到的Segment不为空，则统计它的 modCount 和 count 元素个数
    if (seg != null) {
     //累加当前Segment的结构修改次数，如put，remove等操作都会影响modCount
     sum += seg.modCount;
     int c = seg.count;
     //若当前Segment的元素个数 c 小于0 或者 size 加上 c 的结果小于0，则认为溢出
     //因为若超过了 int 最大值，就会返回负数
     if (c < 0 || (size += c) < 0)
      overflow = true;
    }
   }
   //当此次尝试，统计的 sum 值和上次统计的值相同，则说明这段时间内，
   //并没有任何一个 Segment 的结构发生改变，就可以返回最后的统计结果
   if (sum == last)
    break;
   //不相等，则说明有 Segment 结构发生了改变，则记录最新的结构变化次数之和 sum，
   //并赋值给 last，用于下次重试的比较。
   last = sum;
  }
 } finally {
  //如果超过了指定重试次数，则说明表中的所有Segment都被加锁了，因此需要把它们都解锁
  if (retries > RETRIES_BEFORE_LOCK) {
   for (int j = 0; j < segments.length; ++j)
    segmentAt(segments, j).unlock();
  }
 }
 //若结果溢出，则返回 int 最大值，否则正常返回 size 值 
 return overflow ? Integer.MAX_VALUE : size;
}
```

### 4. ConcurrentHashMap 1.8版本

1.8版本取消了Segment的概念，而是采用锁粒度更低的对每个桶头结点加锁。相当于结构设计和HashMap相同，但是多了一些并发处理。

**put方法**

```java
 for (Node<K,V>[] tab = table;;) {
  Node<K,V> f; int n, i, fh;
  //如果表为空，则说明还未初始化。
  if (tab == null || (n = tab.length) == 0)
   //初始化表，只有一个线程可以初始化成功。
   tab = initTable();
  //若表已经初始化，则找到当前 key 所在的桶，并且判断是否为空
  else if ((f = tabAt(tab, i = (n - 1) & hash)) == null) {
   //若当前桶为空，则通过 CAS 原子操作，把新节点插入到此位置，
   //这保证了只有一个线程可以 CAS 成功，其它线程都会失败。
   if (casTabAt(tab, i, null,
       new Node<K,V>(hash, key, value, null)))
    break;                   // no lock when adding to empty bin
  }
     synchronized (f) {
    //recheck 一下，保证当前桶的第一个节点无变化，后边很多这样类似的操作，不再赘述
    if (tabAt(tab, i) == f) {
     //如果hash值大于等于0，说明是正常的链表结构
     if (fh >= 0) {
      binCount = 1;
      //从头结点开始遍历，每遍历一次，binCount计数加1
      for (Node<K,V> e = f;; ++binCount) {
       K ek;
       //如果找到了和当前 key 相同的节点，则用新值替换旧值
       if (e.hash == hash &&
        ((ek = e.key) == key ||
         (ek != null && key.equals(ek)))) {
        oldVal = e.val;
        if (!onlyIfAbsent)
         e.val = value;
        break;
       }
       Node<K,V> pred = e;
       //若遍历到了尾结点，则把新节点尾插进去
       if ((e = e.next) == null) {
        pred.next = new Node<K,V>(hash, key,
                value, null);
        break;
       }
      }
```

思路是：

1. 先判断Table是不是null，为null则初始化Table
2. 定位桶的位置，判断桶是不是空，如果是空，则CAS方式将当前节点作为头结点，保证只有一个线程可以创建头结点。
3. 否则，synchronized对头结点进行加锁，然后进行同步的插入。