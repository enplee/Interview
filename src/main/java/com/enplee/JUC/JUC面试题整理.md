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

