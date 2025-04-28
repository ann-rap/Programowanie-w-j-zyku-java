import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ZasuwaTest {
    private static final int N_watkow = 4;
    private static final int pom = 20000;

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        List<BigInteger> results = new ArrayList<>();

        for (int i = 0; i < N_watkow; i++) {
            results.add(BigInteger.ZERO);
        }
        
        CountDownLatch latch = new CountDownLatch(N_watkow);

        for (int i = 0; i < N_watkow; i++) {
            final int nr_watku = i;
            Thread thread = new Thread(() -> {
                BigInteger result = silnia(pom);
                results.set(nr_watku, result);
                System.out.println("Watek " + nr_watku + " zakonczyl zadanie");

                latch.countDown(); 
            });
            thread.start();
        }

        try {
            latch.await();
            System.out.println("Zakonczono watki pomocnicze...");
            
            BigInteger sum = BigInteger.ZERO;
            for (BigInteger result : results) {
                sum = sum.add(result);
            }

            long endTime = System.currentTimeMillis();
            
            System.out.println("Execution time: " + (endTime - startTime) + " ms");
        } catch (InterruptedException e) {
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
