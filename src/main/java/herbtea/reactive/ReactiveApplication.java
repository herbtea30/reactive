package herbtea.reactive;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Future;

@SpringBootApplication
@Slf4j
@EnableAsync
public class ReactiveApplication {
    @RestController
    public static class Controller {
        @RequestMapping("hello")
        public Publisher<String> hello(String name) {
            return new Publisher<String>() {
                @Override
                public void subscribe(Subscriber<? super String> subscriber) {
                    subscriber.onSubscribe(new Subscription() {
                        @Override
                        public void request(long l) {
                            subscriber.onNext("Hello " + name);
                            subscriber.onComplete();
                        }

                        @Override
                        public void cancel() {

                        }
                    });
                }
            };
        }
    }

    @Component
    public static class MyService {
        @Async(value = "tp")
        public ListenableFuture<String> hello() throws InterruptedException {
            log.info("hello");
            Thread.sleep(2000);
            return new AsyncResult<>("Hello");
        }
    }

    @Bean
    ThreadPoolTaskExecutor tp() {
        ThreadPoolTaskExecutor te = new ThreadPoolTaskExecutor();
        te.setCorePoolSize(10); //첫 요청시에 10개를 만듬
        te.setMaxPoolSize(100); //큐가 꽉 차면 그때 풀사이즈를 늘려줌
        te.setQueueCapacity(200);
        te.setThreadNamePrefix("myThread");
        te.initialize();
        return te;
    }

    public static void main(String[] args) {
        try(ConfigurableApplicationContext c = SpringApplication.run(ReactiveApplication.class, args)){
        }
    }

    @Autowired MyService myService;

    @Bean
    ApplicationRunner run() {
        return args -> {
            log.info("run()");
            ListenableFuture<String> f = myService.hello();
            f.addCallback(s-> System.out.println(s), e-> System.out.println(e.getMessage()));
            log.info("exit");
//            log.info("exit : " + f.isDone());
//            log.info("result: " + f.get());
        };
    }
}
