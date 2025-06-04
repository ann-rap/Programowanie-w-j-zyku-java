import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class CalculatorImpl extends UnicastRemoteObject implements CalculatorInterface {

    private ONP onpCalculator;

    public CalculatorImpl() throws RemoteException {
        super();
        this.onpCalculator = new ONP();
    }

    @Override
    public String convertToRPN(String expression) throws RemoteException {
        try {
            System.out.println("Server: Konwersja na ONP wyrażenia: " + expression);
            String result = onpCalculator.przeksztalcNaOnp(expression);
            System.out.println("Server: Wynik konwersji: " + result);
            return result;
        } catch (Exception e) {
            System.err.println("Server: Błąd podczas konwersji: " + e.getMessage());
            throw new RemoteException("Błąd konwersji na ONP: " + e.getMessage(), e);
        }
    }

    @Override
    public String calculateRPN(String rpnExpression) throws RemoteException {
        try {
            System.out.println("Server: Obliczanie wyrażenia ONP: " + rpnExpression);
            String result = onpCalculator.obliczOnp(rpnExpression);
            System.out.println("Server: Wynik obliczenia: " + result);
            return result;
        } catch (Exception e) {
            System.err.println("Server: Błąd podczas obliczania: " + e.getMessage());
            throw new RemoteException("Błąd obliczania ONP: " + e.getMessage(), e);
        }
    }
}
