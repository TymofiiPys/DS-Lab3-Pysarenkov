package org.example;
import java.util.concurrent.Semaphore;

public class L3T1 {

    int n = 5;
    int N = 5;
    class FeedPooh{
        int buffer;
        Semaphore filler = new Semaphore(1);
        boolean valueSet = false;
        boolean full = false;
        synchronized void get() {
            while(!full){
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            buffer = 0;
            System.out.println("Ведмідь з'їв увесь мед із горщика");
            full = false;
            notifyAll();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        synchronized void put() {
            while(full){
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                filler.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            buffer++;
            filler.release();
            System.out.println(Thread.currentThread().getName() + " поповнила горщик, заповнення: " + buffer + "/" + N);
            if(buffer == N){
                full = true;
                notify();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class Producer implements Runnable{
        FeedPooh f;
        Producer (FeedPooh f, int number){
            this.f = f;
            new Thread(this, "Бджола №" + number).start();
        }

        @Override
        public void run() {
            while(true){
                f.put();
            }
        }
    }

    class Consumer implements Runnable{
        FeedPooh f;
        Consumer (FeedPooh f){
            this.f = f;
            new Thread(this, "Ведмідь").start();
        }

        @Override
        public void run() {
            while(true){
                f.get();
            }
        }
    }

    void createBees(){
        FeedPooh f = new FeedPooh();
        for (int i = 0; i < n; i++) {
            new Producer(f, (i+1));
        }
        new Consumer(f);
    }

    public static void main(String[] args) {
        L3T1 t = new L3T1();
        t.createBees();
    }
}