import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class CalculatorServer {
    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099);
            System.out.println("Rejestr RMI uruchomiony na porcie 1099");

            CalculatorImpl calculator = new CalculatorImpl();

            Naming.rebind("rmi://localhost:1099/Calculator", calculator);

            System.out.println("Serwer kalkulatora ONP uruchomiony!");
            System.out.println("Oczekiwanie na połączenia klientów...");

        } catch (Exception e) {
            System.err.println("Błąd serwera: " + e.getMessage());
            e.printStackTrace();
        }
    }
}