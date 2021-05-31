package com.enplee.dataStruc;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ArrayblockQueue{

    Lock lock = new ReentrantLock();
    Condition notFull = lock.newCondition();
    Condition notEmpty = lock.newCondition();

    Object[] queue = null;
    int size,putPr,takePr;

    public Object take(){
        lock.lock();
        Object res = null;
        try{
            while (size == 0){
                notEmpty.await();
            }
            res = queue[takePr];
            if(++takePr == queue.length) takePr = 0;
            size--;
            notFull.signalAll();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
        return res;
    }

    public void put(Object o){
        lock.lock();
        try{
            while (size == queue.length){
                notFull.await();
            }
            queue[putPr] = o;
            if(++putPr == queue.length) putPr = 0;
            size++;
            notEmpty.signalAll();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

}
