import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CalculatorInterface extends Remote {
    String convertToRPN(String expression) throws RemoteException;
    String calculateRPN(String rpnExpression) throws RemoteException;
}