package herbtea.reactive.live;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *  Reactive Streams - Operators
 *
 *  pub -> [Data1] -> mapPub -> [Data2] -> logSub
 *             <- subscribe(logSub)
 *             -> onScribe(subscribe)
 *             -> onNext
 *             -> ..onNext
 *             -> onComplete
 *  1.map (d1 -> f -> d2)
 *
 *  onSubscribe onNext* (onError | onComplete)?
 */
public class PubSubOperators {
    public static void main(String[] args) {
        Publisher<Integer> pub = iterPub(Stream.iterate(1, a -> a + 1).limit(10).collect(Collectors.toList()));
//        Publisher<String> mapPub = mapPub(pub, s -> "[" + s + "]");
//        Publisher<Integer> map2Pub = mapPub(mapPub, s -> -s);
//        Publisher<Integer> sumPub = sumPub(pub);
        Publisher<StringBuilder> reducePub = reducePub(pub, new StringBuilder(), (a, b) -> a.append(b+","));
        reducePub.subscribe(logSub());
    }

    //1, 2, 3, 4, 5
    //0 -> (0, 1) 0 + 1 = 1
    //1 -> (1, 2) 1 + 2 = 3
    //3 -> (3, 3) 3 + 3 = 6
    // ...
    private static <T, R> Publisher<R> reducePub(Publisher<T> pub, R init, BiFunction<R, T, R> bf) {
        return new Publisher<R>() {
            @Override
            public void subscribe(Subscriber<? super R> subscriber) {
                pub.subscribe(new DelegateSub<T, R>(subscriber){
                    R result = init;

                    @Override
                    public void onNext(T integer) {
                        result = bf.apply(result, integer);
                    }

                    @Override
                    public void onComplete() {
                        subscriber.onNext(result);
                        subscriber.onComplete();
                    }
                });
            }
        };
    }

//    private static Publisher<Integer> sumPub(Publisher<Integer> pub) {
//        return new Publisher<Integer>() {
//            @Override
//            public void subscribe(Subscriber<? super Integer> subscriber) {
//                pub.subscribe(new DelegateSub(subscriber){
//                    int sum = 0;
//                    @Override
//                    public void onNext(Integer integer) {
//                        sum += integer;
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        subscriber.onNext(sum);
//                        subscriber.onComplete();
//                    }
//                });
//            }
//        };
//    }
    // T -> R
    private static <T, R> Publisher<R> mapPub(Publisher<T> pub, Function<T, R> f) {
        return new Publisher<R>() {
            @Override
            public void subscribe(Subscriber<? super R> subscriber) {
                pub.subscribe(new DelegateSub<T, R>(subscriber) {
                    @Override
                    public void onNext(T integer) {
                        subscriber.onNext(f.apply(integer));
                    }
                });
            }
        };
    }

    private static <T> Subscriber<T> logSub() {
        return new Subscriber<T>() {
            @Override
            public void onSubscribe(Subscription subscription) {
                System.out.println("onSubscribe:");
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(T i) {
                System.out.println("onNext:{}" + i);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("onError:{}" + throwable.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        };
    }

    private static Publisher<Integer> iterPub(List<Integer> iter) {
        return new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> subscriber) {
                subscriber.onSubscribe(new Subscription() {
                    @Override
                    public void request(long l) {
                        try {
                            iter.forEach(s -> subscriber.onNext(s));
                            subscriber.onComplete();
                        } catch (Throwable t) {
                            subscriber.onError(t);
                        }
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        };
    }
}
