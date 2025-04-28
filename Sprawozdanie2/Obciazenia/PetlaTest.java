import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class PetlaTest {
    private static final int N_watkow = 4;
    private static final int pom = 20000;
    private static final AtomicInteger finishedThreads = new AtomicInteger(0);
    private static final AtomicInteger counter = new AtomicInteger(0);

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        List<Thread> threads = new ArrayList<>();
        List<BigInteger> results = new ArrayList<>();
        List<AtomicBoolean> threadFinished = new ArrayList<>();

        for (int i = 0; i < N_watkow; i++) {
            results.add(BigInteger.ZERO);
            threadFinished.add(new AtomicBoolean(false));
        }

        for (int i = 0; i < N_watkow; i++) {
            final int nr_watku = i;
            Thread thread = new Thread(() -> {

                BigInteger result = silnia(pom);
                results.set(nr_watku, result);
                System.out.println("Watek " + nr_watku + " zakonczyl zadanie");

                threadFinished.get(nr_watku).set(true);
                finishedThreads.incrementAndGet();
            });
            threads.add(thread);
            thread.start();
        }


        while (true) {
            counter.incrementAndGet();


            if (counter.get() % 1000000 == 0) {
                System.out.println("Glowny watek czeka...");
                System.out.println("Ukonczono: " + finishedThreads.get() + "/" + N_watkow);
            }


            boolean allFinished = true;
            for (AtomicBoolean finished : threadFinished) {
                if (!finished.get()) {
                    allFinished = false;
                    break;
                }
            }

            if (allFinished) {
                System.out.println("Wszystkie watki skonczyly prace");
                break;
            }
        }

        BigInteger sum = BigInteger.ZERO;
        for (BigInteger result : results) {
            sum = sum.add(result);
        }

        long endTime = System.currentTimeMillis();

        System.out.println("Execution time: " + (endTime - startTime) + " ms");
    }

    private static BigInteger silnia(int n) {
        BigInteger result = BigInteger.ONE;
        for (int i = 2; i <= n; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }
        return result;
    }
}
