package ThreadRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TaskManagerConsole {

    private final List<MyTask> tasks = new ArrayList<>();

    private static class MyTask {
        private final Thread thread;
        private volatile String result = null;

        public MyTask(Runnable task) {
            this.thread = new Thread(() -> {
                try {
                    task.run();
                } catch (Exception e) {
                    result = "Wynik zadania: Przerwane przez wyjatek";
                    return;
                }
                if (Thread.currentThread().isInterrupted()) {
                    result = "Wynik zadania: Anulowane";
                } else {
                    result = "Wynik zadania: Sukces";
                }
            });
            thread.start();
        }

        public boolean isAlive() {
            return thread.isAlive();
        }

        public void cancel() {
            thread.interrupt();
        }

        public String getResult() {
            return result;
        }
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nMenadzer zadan:");
            System.out.println("1. Dodaj zadanie");
            System.out.println("2. Sprawdz status");
            System.out.println("3. Anuluj zadanie");
            System.out.println("4. Wyjscie");
            System.out.print("Wybierz opcje: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1 -> addTask();
                case 2 -> checkStatus();
                case 3 -> cancelTask(scanner);
                case 4 -> {
                    shutdown();
                    return;
                }
                case 5 -> addTaskInf();
                default -> System.out.println("Nieprawidlowa opcja. Sprobuj ponownie.");
            }
        }
    }

    private void addTask() {
        Runnable task = () -> {
            try {
                System.out.println("Zadanie wystartowalo.");
                Thread.sleep((long) (Math.random() * 5000 + 5000));
                System.out.println("\nZadanie zakonczone!");
            } catch (InterruptedException e) {
                System.out.println("Zadanie przerwane.");
                Thread.currentThread().interrupt();
            }
        };
        tasks.add(new MyTask(task));
        System.out.println("Zadanie " + tasks.size() + " dodane i uruchomione.");
    }

    //zadanie ktore wykonuje sie wystarczajaco dlugo, ze mozna je anulowac
    private void addTaskInf() {
        Runnable task = () -> {
            try {
                System.out.println("Zadanie wystartowalo.");
                Thread.sleep((long) (Math.random() * 500000 + 5000));
                System.out.println("\nZadanie zakonczone!");
            } catch (InterruptedException e) {
                System.out.println("Zadanie przerwane.");
                Thread.currentThread().interrupt();
            }
        };
        tasks.add(new MyTask(task));
        System.out.println("Zadanie " + tasks.size() + " dodane i uruchomione.");
    }

    private void checkStatus() {
        for (int i = 0; i < tasks.size(); i++) {
            MyTask task = tasks.get(i);
            if (task.isAlive()) {
                System.out.println("Zadanie " + (i + 1) + ": W trakcie...");
            } else if (task.getResult() != null) {
                System.out.println("Zadanie " + (i + 1) + ": " + task.getResult());
            } else {
                System.out.println("Zadanie " + (i + 1) + ": Zakonczone bez wyniku.");
            }
        }
    }

    private void cancelTask(Scanner scanner) {
        System.out.print("Podaj numer zadania do anulowania: ");
        int index = scanner.nextInt() - 1;
        if (index >= 0 && index < tasks.size()) {
            MyTask task = tasks.get(index);
            if (task.isAlive()) {
                task.cancel();
                System.out.println("Zadanie " + (index + 1) + " anulowane.");

            } else {
                System.out.println("Nie mozna anulowac — zadanie juz zakonczone.");
            }
        } else {
            System.out.println("Nieprawidlowy numer zadania.");
        }
    }

    private void shutdown() {
        System.out.println("Zamykanie menadzera zadan...");
        for (MyTask task : tasks) {
            if (task.isAlive()) {
                task.cancel();
            }
        }
    }

    public static void main(String[] args) {
        new TaskManagerConsole().start();
    }
}
