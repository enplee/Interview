##  Golang面试整理

#### 一. slice底层原理与数组的区别

```
1.数组是固定长度的相同类型的元素序列。
2.数组在传递的时候是值传递，将数值复制给一个新的变量或者方法参数传递的时候，都会进行拷贝。

slice本身的长度是不固定的，相当于动态数组。
slice持有了一个源数组在内存地址中的引用，多个slice可以共享一个原数组。
```

```go
type slice struct {
 array unsafe.Pointer // 指向源数组中第一个可以访问的位置
 len int // slice的长度
 cap int // array.len -> pointer的长度
}
```

+ slice的创建

```go
slice := make([]int,10) // cap = 10 len = 10
1. 底层创建了len = 10的array
2. Pointer指向了idx = 0
3. slice.len = 10 slice.cap = 10
slice := make([]int,10,20) // cap = 20 len = 10
1. 底层创建了len = 20的array
2. Pointer指向了idx = 0
3. slice.len = 10 slice.cpa = 20
```

+ slice的扩容机制

```go
1. 如果new_cap >= cap*2, 直接扩容new_cap
2. 否则如果 cap<=1024，new_cap = cap*2, 否则 new_cap = cap*1.25
```



#### 二. defer的执行顺序

```
defer语句后的语句将会被延迟执行。defer归属的函数即将返回时，defer按照逆序执行。
defer可以用来在函数退出的时候，释放资源。
+ 多个defer语句的执行顺序
多个defer行为被注册的时候，以逆序的形式后进先出。
```

+ defer和return的执行顺序

```go
func test() int {//这里返回值没有命名
    var i int
    defer func() {
        i++
        fmt.Println("defer1", i) //作为闭包引用的话，则会在defer函数执行时根据整个上下文确定当前的值。i=2
    }()
    defer func() {
        i++
        fmt.Println("defer2", i) //作为闭包引用的话，则会在defer函数执行时根据整个上下文确定当前的值。i=1
    }()
    return i
}

func test() (i int) { //返回值命名i
    defer func() {
        i++
        fmt.Println("defer1", i)
    }()
    defer func() {
        i++
        fmt.Println("defer2", i)
    }()
    return i
}
/*
defer2 1
defer1 2
return: 0

defer2 1
defer1 2
return: 2
*/
```

```
return不是原子操作，return分为两步；第一步赋值，如果有命名的返回值直接返回，如果没有命名会指定一个默认的返回值执行赋值。第二步函数执行返回机制。
defer和return的执行顺序是:
1. return 进行复制
2. defer 逆序执行
3. return执行返回
```

#### 三. Channel总结(有缓存的管道和没有缓存的管道区别是什么)

```
Go语言中最常见的设计模式是:不要通过共享内存来通讯而是通过通讯的方式共享内存。
在其他语言中,许多线程传递数据的方式是通过共享内内存,同时要控制这块线程的访问。
Go提供了一种不同的并发模式CSP(Communicating sequential processs).通讯顺序进程。
```

+ 数据结构

```go
type hchan struct {
	qcount   uint
	dataqsiz uint
	buf      unsafe.Pointer
	elemsize uint16
	closed   uint32
	elemtype *_type
	sendx    uint
	recvx    uint
	recvq    waitq
	sendq    waitq

	lock mutex
}
/* 
qcount、dataqsiz、buf、sendx、recv 构建了缓冲的循环队列
recvq、sendq、mutex用来控制数据的同步
```

+ 发送数据 chan <- i

```go
在真正发送数据的逻辑执行之前,会为chan加锁,防止多个goroutine并发的修改chan.并检查chan是否关闭。
func chansend(c *hchan, ep unsafe.Pointer, block bool, callerpc uintptr) bool {
	lock(&c.lock)

	if c.closed != 0 {
		unlock(&c.lock)
		panic(plainError("send on closed channel"))
	}
所有发送数据操作会被编译成执行chansend()方法。该方法会做如下进行:
1. 如果chan的队列中存在等待的接受者,直接将数据发送给接受者 runtime.send()
2. 如果缓冲区存在空间,将数据写入chan的缓冲区
3. 如果不存在或者缓冲区已满,等待其他Goroutine接受数据
```



```
Channel是goRoutine之间的通信桥梁。go语言通过通信来共享内存数据。
channel是goroutine安全的
channel提供了FIFO语义
channel可以使goroutine block/unblock
```

```go
ch1 := make(chan int)
ch2 := make(chan int,1)
1. 无缓冲的管道必须要求goroutine之间同时完成接受和发送动作，如果无人接受，发送方将阻塞。
2. 有缓冲的管道不要求接受和发送同时完成，在缓冲没有满的情况下，向管道发送数据是不阻塞的。只有向缓冲满了的管道发送数据以及向空的管道接受数据的goroutine才会阻塞。
```

#### 四. map的实现 [清晰的参考文章](https://studygolang.com/articles/32943)

```
hashTable实现，通过链地址法处理冲突。Array + Array的链表。
```

golang中的map依然采用的hash表的方式进行映射,但是处理hash冲突的方式不同于传统链表实现,而是使用一个长度为8的连续数组bmap.

+ hmap

```go
type hmap struct {
	count     int	
	flags     uint8
	B         uint8
	noverflow uint16
	hash0     uint32

	buckets    unsafe.Pointer
	oldbuckets unsafe.Pointer
	nevacuate  uintptr

	extra *mapextra
}
/*hmap结构
count: 描述当前元素个数
hash0: hash因子，用来随机生成hashcode
B: bmap个数的位数
buckets: 指向bmap[]
```

+ bmap

```go
type bmap struct {
    topbits  [8]uint8
    keys     [8]keytype
    values   [8]valuetype
    pad      uintptr
    overflow uintptr
}
/*bmap 桶结构
topbits[]: 记录key-hash的高八位,用来快速比较key
keys/values: 长度8的key/val对应的数组
overflow: 指向溢出处理的桶
```

+ 读map[key]: map[key]方法会在编译器生成中间代码 mapaccess()方法执行。

```go
func mapaccess1(t *maptype, h *hmap, key unsafe.Pointer) unsafe.Pointer {
	alg := t.key.alg
	hash := alg.hash(key, uintptr(h.hash0))  // 1. 获取hashCode
	m := bucketMask(h.B)	// 2. 求出当前的掩码
	b := (*bmap)(add(h.buckets, (hash&m)*uintptr(t.bucketsize))) // 3. 计算在buckets上的映射,获得对应bmap的指针
	top := tophash(hash) // 求高8位值进行快速比较
    
    // 进行遍历
bucketloop:
	for ; b != nil; b = b.overflow(t) { // for 遍历当前桶和溢出桶 b b.overflow
		for i := uintptr(0); i < bucketCnt; i++ {
			if b.tophash[i] != top {	// 如果高8位不同,跳过,如果是nil,跳出大循环
				if b.tophash[i] == emptyRest {
					break bucketloop
				}
				continue
			}
			k := add(unsafe.Pointer(b), dataOffset+i*uintptr(t.keysize))// 高8位相同,计算出keys上的地址
			if alg.equal(key, k) { // 如果key相同,计算对应的vals地址并返回
				v := add(unsafe.Pointer(b), dataOffset+bucketCnt*uintptr(t.keysize)+i*uintptr(t.valuesize))
				return v
			}
		}
	}
	return unsafe.Pointer(&zeroVal[0])
}
```

+ 写map[key] = val: 中间代码执行mapassign()方法。

```go
func mapassign(t *maptype, h *hmap, key unsafe.Pointer) unsafe.Pointer 
/* 
1. 首先通过key获取对应的hashcode和bmap
2. 遍历bmap上的topHash和key,如果找到相同的key,返回对应的地址
3. 如果key不在bmap中,map会为新的key/val规划内存地址
4. 如果key不在,同时bmap满了,map会调用newoverflow创建溢出桶或者在已经创建好的桶中进行追加。
```

+ 扩容机制,随着元素的增加,碰撞会逐渐增加,性能会恶化。

```go
// mapassign函数在以下两种情况出发扩容:
// 1. 装载因子 > 6.5
// 2. 溢出桶过多
//在扩容期间访问哈希表时会使用旧桶，向哈希表写入数据时会触发旧桶元素的分流。除了这种正常的扩容之外，为了解决大量写入、删除造成的内存泄漏问题，哈希引入了 sameSizeGrow 这一机制，在出现较多溢出桶时会整理哈希的内存减少空间的占用。
```

+ Maps 是线程安全的吗？怎么解决它的并发安全问题？

```
原生的map肯定不是并发安全的，想要解决并发安全那就加锁sync.Map
```

```go
var counter = struct{
    sync.RWMutex
    m map[string]int
}{m: make(map[string]int)}
```

#### 五. 进程、线程、协程的区别和联系 [Golang协程详解和应用](https://zhuanlan.zhihu.com/p/74047342)关于进程线程和Go协程总结

#### [关于进程线程和Go协程总结](https://blog.csdn.net/weixin_40051278/article/details/99286534)

+ 进程

```
进程就是程序的动态运行过程，进程是操作系统资源分配的基本单位。
进程在运行过程中，需要分配内存以及CPU的时间片。在线程之前，进程也是CPU调度的基本单位。
但是: 进程在调度上的成本非常高，上下文切换开销大。于是，考虑将CPU调度和资源分配隔离开来。
```

+ 线程

```
线程是比进程更轻量的调度执行单位，也是如今CPU调度的基本单位。
线程的引入将CPU调度和资源分配隔离开来，各个线程既可以共享进程资源，又可以独立的调度。
线程只需要分配少量资源，比如程序计数器、寄存器和栈。切换开销很小。
从一个应用程序的角度，线程的创建是在进程内部的，而进程内部的线程需要操作系统线程的支持。常见的有1:N和1:1模型。1:1模型的实现，每一个进程内部的线程都需要操作系统的内核线程来支持。所以，线程的创建、调度、阻塞都交给操作系统来实现，这样的好处是显然是很方便的，但是线程的操作会造成频繁的方法调用和内核切换，同时1:1的模型并不能充分发挥CPU的能力。
```

+ 协程

```
协程可以叫做轻量级线程，又可以叫用户级线程。他是基于1:N线程模型来实现的。协程是应用系统自己来实现调度处理。如果调度机制实现得当，大部分操作是不需要进入内核态的。可以充分发挥支持线程的能力。
同时，线程的栈大小固定的而协程栈大小是可以动态调整的。
携程虽然有上述的优势，但是调度机制完全由应用系统自己实现，比较困难和复杂。
```

#### 六. Golang 垃圾回收机制

```
Golang GC采用的三色标记+读写混合屏障
```

#### 七. Golang中的指针使用

#### 八. Golang中的String