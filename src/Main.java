import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;

public class Main {
    public static final Map<Integer, Integer> sizeToFreq = new ConcurrentHashMap<>();

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        int numbersOfRoute = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        List<Future<?>> futures = new LinkedList<>();

        long start = System.currentTimeMillis();

        for (int i = 0; i < numbersOfRoute; i++) {
            futures.add(executorService.submit(() -> {
                String route = generateRoute("RLRFR", 100);
                int countR = route.length() - route.replace("R", "").length();

                synchronized (sizeToFreq) {
                    sizeToFreq.merge(countR, 1, Integer::sum);
                }
            }));
        }
        for (Future<?> future : futures) {
            future.get();
        }
        executorService.shutdown();

        int maxFreq = sizeToFreq.values().stream().max(Integer::compareTo).orElse(0);

        int mostCommonSize = sizeToFreq.entrySet().stream()
                .filter(e -> e.getValue() == maxFreq)
                .findFirst().get().getKey();

        System.out.println("Самое частое количество повторений " + mostCommonSize + " (встретилось " + maxFreq + " раз");
        System.out.println("Другие размеры: ");

        sizeToFreq.entrySet().stream()
                .filter(e -> e.getKey() != mostCommonSize)
                .sorted((e1, e2) -> e2.getValue() - e1.getValue())
                .forEach(e -> System.out.println("- " + e.getKey() + " (" + e.getValue() + " раз)"));

    }
    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }
}