import java.rmi.Naming;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try {

            CalculatorInterface calculator = (CalculatorInterface) Naming.lookup("rmi://localhost:1099/Calculator");

            Scanner scanner = new Scanner(System.in);

            System.out.println("=== KLIENT KALKULATORA ONP RMI ===");
            System.out.println("Wprowadź wyrażenie matematyczne (np. (2 + 4) * (3 - 8) ^ 2 =)");
            System.out.println("lub 'exit' aby zakończyć");

            while (true) {
                System.out.print("\nWyrażenie: ");
                String input = scanner.nextLine().trim();

                if ("exit".equalsIgnoreCase(input)) {
                    System.out.println("Zamykanie klienta...");
                    break;
                }

                if (input.isEmpty()) {
                    System.out.println("Wprowadź poprawne wyrażenie!");
                    continue;
                }

                try {
                    System.out.println("\n--- Krok 1: Konwersja na ONP ---");
                    String rpnExpression = calculator.convertToRPN(input);
                    System.out.println("Wyrażenie wejściowe: " + input);
                    System.out.println("Postać ONP: " + rpnExpression);

                    System.out.println("\n--- Krok 2: Obliczenie wyniku ---");
                    String result = calculator.calculateRPN(rpnExpression);
                    System.out.println("Wynik: " + result);

                    System.out.println("\n=== PODSUMOWANIE ===");
                    System.out.println("Wyrażenie: " + input);
                    System.out.println("ONP: " + rpnExpression);
                    System.out.println("Wynik: " + result);

                } catch (Exception e) {
                    System.err.println("Błąd: " + e.getMessage());
                }
            }

            scanner.close();

        } catch (Exception e) {
            System.err.println("Błąd klienta: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
