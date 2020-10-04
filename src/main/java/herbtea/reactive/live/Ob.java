package herbtea.reactive.live;

import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("deprecation")
public class Ob {
    //1. How to Complete Event
    //2. Error Process

    //Iterable <---> Observable (duality)
    //Pull           Push
    /*Iterable<Integer> iter = () ->
            new Iterator<Integer>() {
                int i = 0;
                final static int MAX = 10;

                public boolean hasNext() {
                    return i < MAX;
                }

                public Integer next() {
                    return ++i;
                }
            };

    //java for-each는 collection이 아닌 iterable을 구현한  collection을 넣는다.
        for(Integer i : iter) {
        System.out.println(i);
    }

        for(Iterator<Integer> it = iter.iterator();it.hasNext();){
        System.out.println(it.next());
    }*/

    static class IntObservable extends Observable implements Runnable {

        @Override
        public void run() {
            for(int i=1;i<=10;i++) {
                setChanged();
                notifyObservers(i);     //push
                //int i= it.next();     //pull
            }
        }
    }

    public static void main(String[] args) {
        Observer observer = new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                System.out.println(Thread.currentThread().getName() + " " + arg);
            }
        };

        IntObservable intObservable = new IntObservable();
        intObservable.addObserver(observer);
        ExecutorService es = Executors.newSingleThreadExecutor();
        ExecutorService es2 = Executors.newSingleThreadExecutor();
        es.execute(intObservable);
        es2.execute(intObservable);

        System.out.println(Thread.currentThread().getName() + " EXIT");
        es.shutdown();
        es2.shutdown();
    }
}
