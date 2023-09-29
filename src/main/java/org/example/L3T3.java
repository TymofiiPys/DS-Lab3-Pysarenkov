package org.example;
import java.util.Random;
import java.util.concurrent.Semaphore;
public class L3T3 {

    class Smoke{
        int[] items;
        Semaphore giveaway = new Semaphore(1);
        Semaphore smoking = new Semaphore(1);
        boolean started = false;
        Smoke() {
            this.items = new int[2];
        }

        synchronized void get(int myItem){
            while(myItem == items[0] || myItem == items[1] || !started){
                try {
                    System.out.println(Thread.currentThread().getName() + " очікує на свій предмет");
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try{
                smoking.acquire();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " курить сигарету...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            notifyAll();
            smoking.release();
        }

        synchronized void put(){
            try{
                smoking.acquire();
            } catch (InterruptedException e){
                e.printStackTrace();
            }

            items[0] = new Random().nextInt(3);
            do{
                items[1] = new Random().nextInt(3);
            } while (items[1] == items[0]);
            System.out.println(Thread.currentThread().getName() + "обрав предмети " + items[0] + " і " + items[1]);
            started = true;
            notifyAll();
            smoking.release();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class Smoker implements Runnable{
        Smoke s;
        int item;
        Smoker(Smoke s, int item){
            this.s = s;
            this.item = item;
            new Thread(this, "Курець №" + item).start();
        }
        @Override
        public void run() {
            while(true){
                s.get(item);
            }
        }
    }

    class Host implements Runnable{
        Smoke s;
        Host (Smoke s){
            this.s = s;
            new Thread(this, "Посередник ").start();
        }
        @Override
        public void run() {
            while(true){
                s.put();
            }
        }
    }

    public void startSmoking(){
        Smoke s = new Smoke();
        new Smoker(s, 0);
        new Smoker(s, 1);
        new Smoker(s, 2);
        new Host(s);
    }

    public static void main(String[] args) {
        L3T3 l = new L3T3();
        l.startSmoking();
    }
}
