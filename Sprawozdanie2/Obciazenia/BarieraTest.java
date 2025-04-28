import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class BarieraTest{
    private static final int N_watkow = 4;
    private static final int pom = 20000;

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        List<BigInteger> results = new ArrayList<>();

        for (int i = 0; i < N_watkow; i++) {
            results.add(BigInteger.ZERO);
        }
        
        CyclicBarrier barrier = new CyclicBarrier(N_watkow + 1); // +1 dla głównego wątku
        
        for (int i = 0; i < N_watkow; i++) {
            final int nr_watku = i;
            Thread thread = new Thread(() -> {
                BigInteger result = silnia(pom);
                results.set(nr_watku, result);
                System.out.println("Thread " + nr_watku + " calculated factorial");

                try {
                    // Czekaj na dotarcie wszystkich wątków do bariery
                    barrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            });
            thread.start();
        }

        try {
            barrier.await();
            
            BigInteger sum = BigInteger.ZERO;
            for (BigInteger result : results) {
                sum = sum.add(result);
            }

            long endTime = System.currentTimeMillis();

            System.out.println("Zakonczono watki pomocnicze " + sum);
            System.out.println("Execution time: " + (endTime - startTime) + " ms");
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

    private static BigInteger silnia(int n) {
        BigInteger result = BigInteger.ONE;
        for (int i = 2; i <= n; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }
        return result;
    }
}
