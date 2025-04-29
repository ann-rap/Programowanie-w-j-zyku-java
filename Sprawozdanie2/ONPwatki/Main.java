import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class Main {
    private static final String FILE_PATH = "./src/equations.txt";
    private static final ReentrantReadWriteLock fileLock = new ReentrantReadWriteLock();
    private static final Lock readLock = fileLock.readLock();
    private static final Lock writeLock = fileLock.writeLock();

    public static void main(String[] args) {
        try {
            List<String> equations = readEquations();
            ExecutorService executor = Executors.newFixedThreadPool(equations.size() * 2);
            List<Future<CalculationResult>> futures = new ArrayList<>();

            for (int i = 0; i < equations.size(); i++) {
                EquationReaderTask readerTask = new EquationReaderTask(i, equations.get(i));
                Future<CalculationResult> readerFuture = executor.submit(readerTask);
                futures.add(readerFuture);
            }

            for (Future<CalculationResult> future : futures) {
                try {
                    CalculationResult result = future.get();
                    EquationCalculatorTask calculatorTask = new EquationCalculatorTask(result.getLineNumber(), result.getEquation());

                    FutureTask<Void> futureTask = new FutureTask<>(calculatorTask) {
                        @Override
                        protected void done() {
                            calculatorTask.done();
                        }
                    };

                    executor.submit(futureTask);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

            executor.shutdown();
            try {
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }

            System.out.println("Zakonczono wszystkie obliczenia");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<String> readEquations() throws IOException {
        List<String> equations = new ArrayList<>();
        readLock.lock();
        try (Scanner scanner = new Scanner(new File(FILE_PATH))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty()) {
                    equations.add(line);
                }
            }
        } finally {
            readLock.unlock();
        }
        return equations;
    }

    private static void updateEquation(int lineNumber, String result) throws IOException {
        writeLock.lock();
        try {
            Path filePath = Paths.get(FILE_PATH);
            List<String> allLines = Files.readAllLines(filePath);

            if (lineNumber < allLines.size()) {
                String currentLine = allLines.get(lineNumber);
                allLines.set(lineNumber, currentLine + " " + result);

                Files.write(filePath, allLines);

                System.out.println("Zaktualizowano linijke " + lineNumber + " z wynikiem: " + result);
            }
        } finally {
            writeLock.unlock();
        }
    }

    static class CalculationResult {
        private final int lineNumber;
        private final String equation;

        public CalculationResult(int lineNumber, String equation) {
            this.lineNumber = lineNumber;
            this.equation = equation;
        }

        public int getLineNumber() {
            return lineNumber;
        }

        public String getEquation() {
            return equation;
        }
    }

    static class EquationReaderTask implements Callable<CalculationResult> {
        private final int lineNumber;
        private final String equation;

        public EquationReaderTask(int lineNumber, String equation) {
            this.lineNumber = lineNumber;
            this.equation = equation;
        }

        @Override
        public CalculationResult call() {
            System.out.println("Watek reader odczytuje rownanie z linii " + lineNumber + ": " + equation);
            return new CalculationResult(lineNumber, equation);
        }
    }

    static class EquationCalculatorTask implements Callable<Void> {
        private final int lineNumber;
        private final String equation;
        private String result;

        public EquationCalculatorTask(int lineNumber, String equation) {
            this.lineNumber = lineNumber;
            this.equation = equation;
        }

        @Override
        public Void call() {
            try {
                System.out.println("Watek Calculator oblicza wynik dla linii " + lineNumber + ": " + equation);
                ONP onp = new ONP();
                String onpExpression = onp.przeksztalcNaOnp(equation);
                result = onp.obliczOnp(onpExpression);
            } catch (Exception e) {
                System.err.println("Błąd przy obliczaniu linii " + lineNumber + ": " + e.getMessage());
                result = "ERROR: " + e.getMessage();
            }
            return null;
        }

        public void done() {
            try {
                System.out.println("Zapisywanie wyniku dla linii " + lineNumber + ": " + result);
                updateEquation(lineNumber, result);
            } catch (IOException e) {
                System.err.println("Błąd przy aktualizacji pliku: " + e.getMessage());
            }
        }
    }
}
