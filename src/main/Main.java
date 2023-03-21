import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Stream;

public class Main {

    private String sum(String s) {
        return s + "kkk";
    }

    private static Function<String, String> wibble = x -> x + "ii";

    public static void main(String[] args) {
        // Mapping option:-
        {
            Optional<String> a = Optional.of("bla");
            Optional<String> t = a.map(wibble);
            System.out.println("Hello world!" + t);
        }

        // Mapping sequence
        {
            List<String> a = List.of("1", "2", "3£");
            // a.add("added item"); <- Can't do this because java.util.List is immutable
            Stream<String> t = a.stream().map(wibble);
            t.forEach(s -> System.out.println(s));
        }

        // Futures
//        {
//            ExecutorService executor
//                    = Executors.newSingleThreadExecutor();
//
//            Future<String> a = executor.submit(
//                    () -> {
//                        Thread.sleep(1000);
//                        return "Future";
//                    }
//            );
//
//
//        }
    }

}
