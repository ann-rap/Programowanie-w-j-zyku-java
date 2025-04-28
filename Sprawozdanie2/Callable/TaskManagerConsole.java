import java.util.*;
import java.util.concurrent.*;

public class TaskManagerConsole {

    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final List<MyFutureTask> tasks = new ArrayList<>();
    private final Random random = new Random();

    private static class MyFutureTask extends FutureTask<List<Integer>> {

        public MyFutureTask(Callable<List<Integer>> callable) {
            super(callable);
        }

        @Override
        protected void done() {
            try {
                if (isCancelled()) {
                    System.out.println("Zadanie zostało anulowane.");
                } else {
                    System.out.println("Zadanie zakończone! Znaleziono " + get().size() + " liczb pierwszych.");
                }
            } catch (InterruptedException | ExecutionException e) {
                System.out.println("Błąd w zadaniu: " + e.getMessage());
            }
        }

    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nMenadzer zadan:");
            System.out.println("1. Dodaj nowe zadanie (szukanie liczb pierwszych)");
            System.out.println("2. Sprawdz status");
            System.out.println("3. Anuluj zadanie");
            System.out.println("4. Pokaz wynik zadania");
            System.out.println("5. Wyjscie");
            System.out.print("Wybierz opcje: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1 -> addTask();
                case 2 -> checkStatus();
                case 3 -> cancelTask(scanner);
                case 4 -> showResult(scanner);
                case 5 -> {
                    shutdown();
                    return;
                }
                case 6 -> addTaskInf();
                default -> System.out.println("Nieprawidlowa opcja.");
            }
        }
    }

    private void addTask() {
        int from = random.nextInt(100000) + 1;
        int to = from + random.nextInt(10000000) + 100000;

        Callable<List<Integer>> task = () -> {
            List<Integer> primes = new ArrayList<>();
            for (int i = from; i <= to; i++) {
                if (isPrime(i)) {
                    primes.add(i);
                }
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException();
                }
            }
            return primes;
        };


        MyFutureTask futureTask = new MyFutureTask(task);
        tasks.add(futureTask);
        executor.submit(futureTask);

        System.out.println("Zadanie " + tasks.size() + " dodane: szukaj liczb pierwszych w zakresie [" + from + ", " + to + "]");
    }

    private void addTaskInf() {
        int from = random.nextInt(100000) + 1;
        int to =Integer.MAX_VALUE;

        Callable<List<Integer>> task = () -> {
            List<Integer> primes = new ArrayList<>();
            for (int i = from; i <= to; i++) {
                if (isPrime(i)) {
                    primes.add(i);
                }
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException();
                }
            }
            return primes;
        };


        MyFutureTask futureTask = new MyFutureTask(task);
        tasks.add(futureTask);
        executor.submit(futureTask);

        System.out.println("Zadanie " + tasks.size() + " dodane: szukaj liczb pierwszych w zakresie [" + from + ", " + to + "]");
    }

    private boolean isPrime(int n) {
        if (n < 2) return false;
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) return false;
        }
        return true;
    }

    private void checkStatus() {
        for (int i = 0; i < tasks.size(); i++) {
            MyFutureTask task = tasks.get(i);
            if (task.isCancelled()) {
                System.out.println("Zadanie " + (i + 1) + ": Anulowane");
            } else if (task.isDone()) {
                System.out.println("Zadanie " + (i + 1) + ": Zakonczone");
            } else {
                System.out.println("Zadanie " + (i + 1) + ": W trakcie...");
            }
        }
    }

    private void cancelTask(Scanner scanner) {
        System.out.print("Podaj numer zadania do anulowania: ");
        int index = scanner.nextInt() - 1;
        if (index >= 0 && index < tasks.size()) {
            boolean cancelled = tasks.get(index).cancel(true);
            if (cancelled) {
                System.out.println("Zadanie " + (index + 1) + " zostalo anulowane.");
            } else {
                System.out.println("Nie udalo sie anulowac zadania (byc moze juz zakonczone).");
            }
        } else {
            System.out.println("Nieprawidlowy numer zadania.");
        }
    }

    private void showResult(Scanner scanner) {
        System.out.print("Podaj numer zadania, aby pokazac wynik: ");
        int index = scanner.nextInt() - 1;
        if (index >= 0 && index < tasks.size()) {
            MyFutureTask task = tasks.get(index);
            if (task.isDone() && !task.isCancelled()) {
                try {
                    List<Integer> primes = task.get();
                    System.out.println("Wynik zadania " + (index + 1) + ":");
                    System.out.println("Znalezione liczby pierwsze (" + primes.size() + "): " + primes);
                } catch (InterruptedException | ExecutionException e) {
                    System.out.println("Blad podczas pobierania wyniku: " + e.getMessage());
                }
            } else if (task.isCancelled()) {
                System.out.println("Zadanie " + (index + 1) + " zostalo anulowane. Brak wyniku.");
            } else {
                System.out.println("Zadanie jeszcze trwa.");
            }
        } else {
            System.out.println("Nieprawidlowy numer zadania.");
        }
    }

    private void shutdown() {
        System.out.println("Zamykanie menadzera zadan...");
        executor.shutdownNow();
    }

    public static void main(String[] args) {
        new TaskManagerConsole().start();
    }
}
