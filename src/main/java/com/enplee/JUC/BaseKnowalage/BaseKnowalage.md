### JUC之线程基础

#### 一 线程的创建与运行

java中有三种线程创建方式，分别是实现Runnable接口run方法，继承Thread类重写run方法，使用FutureTask方式。

#### extends Thread

```java
public class ThreadCreat {
    public static class CreatedThread extends Thread {
        @Override
        public void run() {
            System.out.println(this.getName());
        }
    }

    public static void main(String[] args) {
        CreatedThread createdThread = new CreatedThread();
        createdThread.start();
    }
}
```

#### implements Runnable

```java
public class ThreadCreat {
    public static class CreatedRunnable implements Runnable {

        @Override
        public void run() {
            System.out.println("Runnable Thread");
        }
    }
    public static void main(String[] args) {
        CreatedRunnable createdRunnable = new CreatedRunnable();
        new Thread(createdRunnable).start();
    }
}
```

#### Future Task

```java
public class FeatureTaskCreate {
    public static class CallerTask implements Callable<String> {

        @Override
        public String call() throws Exception {
            return "return calling results";
        }
    }

    public static void main(String[] args) {
        FutureTask<String> futureTask = new FutureTask<>(new CallerTask());
        new Thread(futureTask).start();
        try {
            String res = futureTask.get();
            System.out.println(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

#### 二 线程通知与等待

#### Object :: wait() notify() nitifyAll()

```java
public class Syn_wait_notify {
	/**
	* 实现线程交替打印
	*/
    public static void main(String[] args) {
        Object syn = new Object();
        Thread thread1 = new Thread(()->{
            synchronized(syn){
                for(int i=0;i<26;i++){
                    System.out.println(i);
                    try {
                        syn.notify();
                        syn.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                syn.notify();
            }
        });
        Thread thread2 = new Thread(()->{
           synchronized (syn) {
               for(int i=0;i<26;i++){
                   System.out.println((char)(i+'a'));
                   try {
                       syn.notify();
                       syn.wait();
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
               }
               syn.notify();
           }
        });
        thread1.start();
        thread2.start();
    }
}
```

#### Thread:: join() sleep() yeild()

##### join() 当前线程会等待调用joind的线程执行结束。yeild()会建议当前thread交出时间片，进入ready转态

```java
public class thread_join {

    public static void main(String[] args) throws InterruptedException {
        Thread thread1 = new Thread(()->{
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        thread1.start();
        System.out.println("thread1 start");

        thread1.join();
        System.out.println("thread1 finish");
    }
}
```

#### 三 线程上下文

#### 线程死锁

##### 死锁的条件：

	+ 互斥条件
	+ 请求与保持
	+ 不可剥夺条件
	+ 环路等待

##### 避免死锁：推荐资源申请有序进行

#### ThreadLocal 

+ 一个ThreadLocal对象可以对应多个线程，为每个线程保存一个线程私有的数据
+ 在线程中调用ThreadLocal::get()/set(), 会操作当前线程中的值
+ 原理是：线程本地维护了ThreadLocalMap，key是ThreadLocalHashcode-val是线程本地变量。每个ThreadLocal对象都对应着一个唯一的ThreadLocalHashcode值，进行get/set时，首先获取当前线程的Map，并用ThreadLocal对应的键去取值。所以说，ThreadLocal只是一个工具壳。

```java
public class Thread_threadLocals {
    public static ThreadLocal<String> threadLocal = new ThreadLocal<>();
    public static void main(String[] args) {

        threadLocal.set("main_val");
        Thread t1 = new Thread(()->{
           threadLocal.set("t1 val");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            print();
        });

        Thread t2 = new Thread(()->{
            threadLocal.set("t2.val");
            print();
        });
        t1.start();
        t2.start();
        System.out.println(threadLocal.get());
    }
    public static void print(){
        System.out.println("current thread:"+Thread.currentThread().getName()+" :"+threadLocal.get());
        threadLocal.remove();
    }
}
```





