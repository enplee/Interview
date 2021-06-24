### JUC面试题整理

#### 1. List与线程安全

+ ArrayList是线程安全的么，为什么？

```
ArrayList不是线程安全的，ArrayList内部并没有针对多线程的情况做特殊处理。
比如: ArrayList在调用add方法的时候，首先要根据判断是否需要扩容，然后根据size进行赋值，size++；
在这个过程中: 
首先size++不是原子性的，可能导致两个线程的add操作都针对的是一个size。
在扩容的时候，如果两个线程都做了扩容，然后一个线程的add丢失。
```

```java
public boolean add(E e) {
    //确定添加元素之后，集合的大小是否足够，若不够则会进行扩容
    ensureCapacityInternal(size + 1);  // Increments modCount!!
    //插入元素
    elementData[size++] = e;
    return true;
}
```

+ 线程安全的List

```
SynchronizedList()：读写方法加锁实现同步，效率低
CopyOnWriteArrayList(): 写时复制原理，读读之间不互斥，读写之间不互斥。
选型?：
读多写少的场景适合写时CopyOnWriteArrayList。
写多的场景,还是使用SynchronizedList,写时复制在写多的场景会不断的创建新的数据，性能比较低。
```

```java
// CopyOnWriteArrayList源码
public E get(int index) { // get方法不加锁, 直接获取数组然后根据idx获取值。
    return get(getArray(), index);
}
private E get(Object[] a, int index) {
    return (E) a[index];
}
final Object[] getArray() {
    return array;
}

public boolean add(E e) { // 写方法加锁 使用ReentrantLock,copy一份数组并修改,然后同步的替换
    //获取重入锁
    final ReentrantLock lock = this.lock;
    //加锁
    lock.lock();
    try {
        //得到旧数组并获取旧数组的长度
        Object[] elements = getArray();
        int len = elements.length;
        //复制旧数组的元素到新的数组中并且大小在原基础上加1
        Object[] newElements = Arrays.copyOf(elements, len + 1);
        //把值插入到新数组中
        newElements[len] = e;
        //使用新数组替换老数组
        setArray(newElements);
        return true;
    } finally {
        //释放锁
        lock.unlock();
    }
}
```

#### 2. Map与线程安全

+ HashMap是线程安全么？为什么
+ 线程安全的Map和实现原理

#### 3. java中的并发工具

+ CountDownLatch 用来允许一个线程或者多个线程等待其他线程完成

```java
public class CountDownLatchTest { //类似join，但是可以阻塞多个线程
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch count = new CountDownLatch(3);
        ExecutorService threadPool = Executors.newFixedThreadPool(3);
        for(int i=0;i<3;i++) {
            threadPool.execute(() -> {
                System.out.println("111");
                count.countDown();
            });
        }
        count.await();
        System.out.println("finish");
        threadPool.shutdown();
    }
}
```

+ CyclicBarrier：同步屏障，所有线程到达时候屏障才会开启，否则会阻塞

```java
public class CyclicBarrierTest { //调用了await方法会阻塞，直到有足够的线程到达await。
    public static void main(String[] args) throws BrokenBarrierException, InterruptedException {
        ExecutorService ec = Executors.newFixedThreadPool(10);
        CyclicBarrier cyclicBarrier = new CyclicBarrier(4);
        for(int i=0;i<3;i++){
            ec.execute(() -> {
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
                System.out.println("111111");
            });
        }
        System.out.println("22222");
        Thread.sleep(1000);
        cyclicBarrier.await();

        ec.shutdown();
    }
}
```

+ Semphore：信号量，用来协调控制并发的数量(如果一个服务只能支持5个并发，现在有6个用户，你选择什么并发工具?)

```java
public class SemphoreTest { // 信号量可以用来做限流,搞定工作的线程数,其余的阻塞。
    public static void main(String[] args) {
        Semaphore semaphore = new Semaphore(3);
        ExecutorService ec = Executors.newFixedThreadPool(5);
        for(int i=0;i<4;i++) {
            ec.execute(() -> {
                try {
                    semaphore.acquire();
                    System.out.println(Thread.currentThread().getName());
                    Thread.sleep(1000);
                    semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        System.out.println("finish");
        ec.shutdown();
    }
}
```

#### 4. 线程池 [参考链接](https://www.cnblogs.com/jay-huaxiao/p/11454416.html)整理的很详细

+ 什么是线程池、为什么需要线程池

```
线程池通过池化的方法管理线程
优点:
重复利用线程，减少重复的创建和销毁，节省资源。
提高响应速度，任务到达的时候，不用等待线程的创建。
```

+ 如何创建线程池

```java
// 可以通过ThreadPoolExecutor的构造方法来创建
    public ThreadPoolExecutor(int corePoolSize,
                              int maximumPoolSize,
                              long keepAliveTime,
                              TimeUnit unit,
                              BlockingQueue<Runnable> workQueue,
                              ThreadFactory threadFactory,
                              RejectedExecutionHandler handler)
// corePoolSize 核心线程数最大值
// maximumPoolSize 线程池线程数最大值
// keepAliveTime 非核心线程空闲存活时间
// unit 存活时间单位
// workQueue 存放到达任务的工作队列
// threadQueue 设置线程工厂，可以定制线程的创建，方便排查问题
// handler 拒绝策略: 抛出异常、丢弃任务、丢弃最老任务、交给调用线程池的线程处理。
```

+ 线程池的执行流程

```
1. 任务提交, 如果当前核心线程数小于最大核心线程数, 创建一个核心线程去执行任务。
2. 如果核心线程数已经达到最大值，将任务放到阻塞队列中，等待核心线程去消费
3. 如果任务队列满了，创建一个非核心线程去执行任务
4. 如果非核心线程数也到达了上限，执行定义的拒绝策略处理。
```

+ 线程池的任务队列

```java
// ArrayBlockingQueue
ArrayBlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<Runnable>(3);
// 通过数组实现有界队列，通过全局方法加锁实现同步机制，同时维护两个condition来存放take、put方法阻塞的线程。
```

```java
// LinkedBlockingQueue
LinkedBlockingDeque<Runnable> linkedBlockingDeque = new LinkedBlockingDeque<>();
// 内部通过单链表的形式实现队列,入队出队分别使用ReentrantLock加以控制,并维护两个condition来存放被阻塞的线程。
public LinkedBlockingDeque() { // 默认的容量是Int的最大值
    this(Integer.MAX_VALUE);
}
// offer/poll方法不会阻塞 take/put方法会阻塞
```

```java
// DelayBlockingQueue
无界延迟阻塞队列,队列中的每个元素都有一个过期时间，只有过期的元素才可以出队。基于PriorityQueue实现。
队列中的元素需要实现:
public interface Delayed extend Conparable<Delayed>(){} 
```

```java
// SynchronousQueue
// 容量为0的阻塞队列，不存储元素。插入操作一直阻塞到take操作发生。
```

#### 常见的线程池(提供了几种线程池的固定实现)

+ FixedThreadPool

```java
public static ExecutorService newFixedThreadPool(int nThreads, ThreadFactory threadFactory) {
        return new ThreadPoolExecutor(nThreads, nThreads,
                                      0L, TimeUnit.MILLISECONDS,
                                      new LinkedBlockingQueue<Runnable>(),
                                      threadFactory);
    }
// 无非核心线程
// LinkeBlockingQueue作为工作队列

//问题: 使用无界队列的线程池会导致内存的飙升？
可能会,如果核心线程执行任务时间比较长,导致任务堆积,使得队列占用内存偏高，最后oom。
//适合场景: cpu密集型场景,执行周期长,但是任务来的慢
```

+ CachedThreadPool

```java
public static ExecutorService newCachedThreadPool(ThreadFactory threadFactory) {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                                      60L, TimeUnit.SECONDS,
                                      new SynchronousQueue<Runnable>(),
                                      threadFactory);
    }
// 无核心线程
// 非核心线程上限MaxValue,空闲时间60s
// 阻塞队列是SynchronousQueue
缺点:提交任务速度大于处理速度的时候,每次提交一个任务都会创建一个线程,导致创建过多的线程。
优点:没有核心线程,长时间空闲的线程池不持有线程资源。
```

+ SingleThreadExecutor

```java
  public static ExecutorService newSingleThreadExecutor(ThreadFactory threadFactory) {
        return new FinalizableDelegatedExecutorService
            (new ThreadPoolExecutor(1, 1,
                                    0L, TimeUnit.MILLISECONDS,
                                    new LinkedBlockingQueue<Runnable>(),
                                    threadFactory));
    }
// 单线程
// 无线阻塞队列
适用于:串行化执行
```

+ ScheduledThreadPool

```java
public ScheduledThreadPoolExecutor(int corePoolSize) {
        super(corePoolSize, Integer.MAX_VALUE, 0, NANOSECONDS,
              new DelayedWorkQueue());
    }
// 用于周期性执行任务
```

+ 线程池的状态装换

```
Running ---shutDown() -----> shutDown() ----》tidying ---terminated() ---> terminated
		-----stop()--------> Stop()
shutdown()方法需要等待所有任务:线程上的任务和队列中的任务执行完毕
stop()方法等待任务执行完毕
```

