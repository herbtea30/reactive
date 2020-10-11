package herbtea.reactive;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;

@SpringBootApplication
@Slf4j
@EnableAsync
public class ReactiveApplication {
    @RestController
    public static class  MyConstroller {
        Queue<DeferredResult<String>> results = new ConcurrentLinkedQueue<>();
//        @GetMapping("/callable")
//        public Callable<String> callable() throws InterruptedException {
//            log.info("callable");
//            return () -> {
//                log.info("async");
//                Thread.sleep(2000);
//                return "hello";
//            };
//        }

        @GetMapping("/dr")
        public DeferredResult<String> deferredResult() throws InterruptedException {
            log.info("dr");
            DeferredResult<String> dr = new DeferredResult<>(600000L);
            results.add(dr);
            return dr;
        }

        @GetMapping("dr/count")
        public String drCount() {
            return String.valueOf(results.size());
        }

        @GetMapping("dr/event")
        public String drEvent(String msg) {
            for(DeferredResult<String> dr : results) {
                dr.setResult("Hello " + msg);
                results.remove(dr);
            }
            return "OK";
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(ReactiveApplication.class, args);
    }
}
