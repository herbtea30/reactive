package herbtea.reactive.live;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.*;

//Future blocking method get
//Callback

@Slf4j
public class FutureEx {
    interface SuccessCallback {
        void onSuccess(String result);
    }
    interface ExceptionCallback {
        void onError(Throwable t);
    }

    public static class CallbackFutureTask extends FutureTask<String> {
        SuccessCallback sc;
        ExceptionCallback ec;

        public CallbackFutureTask(Callable<String> callable, SuccessCallback sc, ExceptionCallback ec) {
            super(callable);
            this.sc = Objects.requireNonNull(sc);
            this.ec = Objects.requireNonNull(ec);
        }

        @Override
        protected void done() {
            try {
                sc.onSuccess(get());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
                ec.onError(e.getCause());
            }
        }
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService es = Executors.newCachedThreadPool();

        CallbackFutureTask f = new CallbackFutureTask(() -> {
            Thread.sleep(2000);
            if(1==1) throw new RuntimeException("Async Error!!!");
            log.info("Async");
            return "Hello";
        },
                s-> System.out.println("Result: "+s),
                e-> System.out.println("Error: " + e.getMessage()));

//        FutureTask<String> f = new FutureTask<String>(() -> {
//            Thread.sleep(2000);
//            log.info("Async");
//            return "Hello";
//        }) {
//            @Override
//            protected void done() {
//                try {
//                    System.out.println(get());
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                }
//            }
//        };

        es.execute(f);
        es.shutdown();

//        System.out.println(f.isDone());
//        Thread.sleep(2100);
//        log.info("Exit");
//        System.out.println(f.isDone());
//        log.info(f.get()); //Blocking Method get

    }
}
