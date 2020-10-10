package herbtea.reactive.live;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FluxScEx {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FluxScEx.class);
    public static void main(String[] args) throws InterruptedException {

//        Flux.range(1, 10)
//                .publishOn(Schedulers.newSingle("pub"))
//                .log()
//                //.subscribeOn(Schedulers.newSingle("sub"))
//                .subscribe(System.out::println);
//
//        System.out.println("exit");

        //User Thread , Deamon Thread
        //데이터를 선별하여 추출 가능.
        Flux.interval(Duration.ofMillis(200))
                .take(10)
                .subscribe(s -> log.debug("onNext{}", s));

        TimeUnit.SECONDS.sleep(10);


//        Executors.newSingleThreadExecutor().execute(() -> {
//            try {
//                TimeUnit.SECONDS.sleep(2);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            System.out.println("Hello");
//        });
//
//        System.out.println("exit");
    }
}
