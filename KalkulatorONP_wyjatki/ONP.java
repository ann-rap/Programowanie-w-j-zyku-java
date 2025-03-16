import java.util.Scanner;

/**
 * @author Sławek Klasa implementująca kalkulator ONP
 */
public class ONP {
    private TabStack stack = new TabStack();

    /**
     * Wyjątek - niepoprawne rownanie
     */
    public class InvalidEquationException extends RuntimeException {
        public InvalidEquationException(String message) {
            super(message);
        }
    }

    /**
     * Wyjątek - nieobsługiwany operator
     */
    public class UnsupportedOperatorException extends RuntimeException {
        public UnsupportedOperatorException(String message) {
            super(message);
        }
    }

    /**
     * Wyjątek - niedozwolone operacje matematyczne
     */
    public class MathOperationException extends RuntimeException {
        public MathOperationException(String message) {
            super(message);
        }
    }

    /**
     * Metoda sprawdza czy równanie kończy się znakiem '='
     *
     * @param rownanie równanie do sprawdzenia
     * @return true jeśli równanie jest poprawne, false jeśli niepoprawne
     */
    boolean czyPoprawneRownanie(String rownanie) {
        if (rownanie == null || rownanie.isEmpty()) {
            throw new InvalidEquationException("Równanie nie może być puste");
        }
        if (rownanie.endsWith("="))
            return true;
        else
            return false;
    }

    /**
     * Metoda oblicza wartość wyrażenia zapisanego w postaci ONP
     *
     * @param rownanie równanie zapisane w postaci ONP
     * @return wartość obliczonego wyrażenia
     * @throws InvalidEquationException gdy równanie ma niepoprawną składnię
     * @throws UnsupportedOperatorException gdy operator nie jest obsługiwany
     * @throws MathOperationException gdy operacja matematyczna jest niedozwolona
     * @throws TabStack.EmptyStackException gdy stos jest pusty podczas próby pobrania wartości
     * @throws TabStack.StackOverflowException gdy stos jest pełny podczas próby dodania wartości
     */
    public String obliczOnp(String rownanie) {
        try {
            if (czyPoprawneRownanie(rownanie)) {
                stack.setSize(0);
                String wynik = "";
                Double a = 0.0;
                Double b = 0.0;
                for (int i = 0; i < rownanie.length(); i++) {
                    // Handle negative numbers - check if it's a minus sign followed by a digit
                    if (rownanie.charAt(i) == '-' && i + 1 < rownanie.length() &&
                            rownanie.charAt(i + 1) >= '0' && rownanie.charAt(i + 1) <= '9') {
                        wynik += rownanie.charAt(i);  // add the minus sign
                        continue;  // continue to next iteration to process the digit
                    }
                    if (rownanie.charAt(i) >= '0' && rownanie.charAt(i) <= '9') {
                        wynik += rownanie.charAt(i);
                        if (i + 1 < rownanie.length() &&
                                !((rownanie.charAt(i + 1) >= '0' && rownanie.charAt(i + 1) <= '9'))) {
                            stack.push(wynik);
                            wynik = "";
                        }
                    }
                    else if (rownanie.charAt(i) == '=') {
                        if (stack.getSize() != 1) {
                            throw new InvalidEquationException("Niepoprawne wyrażenie ONP - na stosie pozostało " + stack.getSize() + " elementów");
                        }
                        return stack.pop();
                    }
                    else if (rownanie.charAt(i) == '!') {
                        if(stack.getSize() <1) {
                            throw new InvalidEquationException("Błąd składni ONP - za mało operandów");
                        }

                        a=Double.parseDouble(stack.pop());

                        if(a<0 || a != Math.floor(a))
                            throw new MathOperationException("Proba obliczenia silnii liczby nienaturalnej!");

                        double pom=1;
                        for(int j=2; j<=a; j++)
                            pom*=j;
                        if(a==0)
                            pom=0;
                        stack.push(pom+"");
                    }else if (rownanie.charAt(i) != ' ') {
                        // Sprawdzamy czy na stosie są co najmniej dwie wartości
                        if (stack.getSize() < 2) {
                            throw new InvalidEquationException("Błąd składni ONP - za mało operandów");
                        }

                        b = Double.parseDouble(stack.pop());
                        a = Double.parseDouble(stack.pop());

                        switch (rownanie.charAt(i)) {
                            case ('+'): {
                                stack.push((a + b) + "");
                                break;
                            }
                            case ('-'): {
                                stack.push((a - b) + "");
                                break;
                            }
                            case ('x'):
                            case ('*'): {
                                stack.push((a * b) + "");
                                break;
                            }
                            case ('/'): {
                                if (b == 0) {
                                    throw new MathOperationException("Próba dzielenia przez zero");
                                }
                                stack.push((a / b) + "");
                                break;
                            }
                            case('%'):{
                                if (b == 0)
                                    throw new MathOperationException("Proba modulo przez zero");

                                if(a!= Math.floor(a) || b!= Math.floor(b))
                                    throw new MathOperationException("Nieprawidłowe argumenty dla modulo");

                                stack.push((a % b) + "");
                                break;
                            }

                            case ('^'): {
                                // Obsługa szczególnych przypadków potęgowania
                                if (a < 0 && b != Math.floor(b)) {
                                    //uwzglednienie pierwiastkowania liczb ujemnych pierwiastek stopnia nieparzystego
                                    if(1/b %2 !=1)
                                        throw new MathOperationException("Nieprawidłowe pierwiastkowanie");
                                    else
                                    {  stack.push(-Math.pow(-a,b)+"");
                                        break;
                                    }
                                }
                                stack.push(Math.pow(a, b) + "");
                                break;
                            }
                            default:
                                throw new UnsupportedOperatorException("Nieobsługiwany operator: " + rownanie.charAt(i));
                        }
                    }
                }
                return "0.0";
            } else {
                throw new InvalidEquationException("Brak znaku '=' na końcu równania");
            }
        } catch (NumberFormatException e) {
            throw new InvalidEquationException("Nieprawidłowy format liczby: " + e.getMessage());
        }
    }

    /**
     * Metoda zamienia równanie na postać ONP
     *
     * @param rownanie równanie do zamiany na postać ONP
     * @return równanie w postaci ONP
     * @throws InvalidEquationException gdy równanie ma niepoprawną składnię
     */
    public String przeksztalcNaOnp(String rownanie) {
        try {
            if (czyPoprawneRownanie(rownanie)) {
                stack.setSize(0);
                String wynik = "";
                int nawiasyOtwarte = 0;

                for (int i = 0; i < rownanie.length(); i++) {
                    if ((rownanie.charAt(i) >= '0' && rownanie.charAt(i) <= '9') ||
                            (rownanie.charAt(i) == '-' && i > 0 && rownanie.charAt(i - 1) == '(')) {
                        wynik += rownanie.charAt(i);
                        if (i + 1 < rownanie.length() && !(rownanie.charAt(i + 1) >= '0' && rownanie.charAt(i + 1) <= '9'))
                            wynik += " ";
                    } else
                        switch (rownanie.charAt(i)) {
                            case ('+'):
                            case ('-'): {
                                while (stack.getSize() > 0 && !stack.showValue(stack.getSize() - 1).equals("(")) {
                                    wynik = wynik + stack.pop() + " ";
                                }
                                String str = "" + rownanie.charAt(i);
                                stack.push(str);
                                break;
                            }
                            case ('x'):
                            case ('*'):
                            case ('/'):
                            case ('%'):{
                                while (stack.getSize() > 0 && !stack.showValue(stack.getSize() - 1).equals("(")
                                        && !stack.showValue(stack.getSize() - 1).equals("+")
                                        && !stack.showValue(stack.getSize() - 1).equals("-")) {
                                    wynik = wynik + stack.pop() + " ";
                                }
                                String str = "" + rownanie.charAt(i);
                                stack.push(str);
                                break;
                            }
                            case ('^'): {
                                while (stack.getSize() > 0 && stack.showValue(stack.getSize() - 1).equals("^")) {
                                    wynik = wynik + stack.pop() + " ";
                                }
                                String str = "" + rownanie.charAt(i);
                                stack.push(str);
                                break;
                            }
                            case('!'):
                            {
                                wynik=wynik+"! ";
                                break;
                            }
                            case ('('): {
                                String str = "" + rownanie.charAt(i);
                                stack.push(str);
                                nawiasyOtwarte++;
                                break;
                            }
                            case (')'): {
                                if (nawiasyOtwarte <= 0) {
                                    throw new InvalidEquationException("Liczba nawiasow jest niepoprawna!");
                                }
                                while (stack.getSize() > 0 && !stack.showValue(stack.getSize() - 1).equals("(")) {
                                    wynik = wynik + stack.pop() + " ";
                                }
                                // zdjęcie ze stosu znaku (
                                if (stack.getSize() > 0) {
                                    stack.pop();
                                    nawiasyOtwarte--;
                                } else {
                                    throw new InvalidEquationException("Błąd w sparowaniu nawiasów");
                                }
                                break;
                            }
                            case ('='): {
                                if (nawiasyOtwarte > 0) {
                                    throw new InvalidEquationException("Niepoprawna liczba nawiasow!");
                                }
                                while (stack.getSize() > 0) {
                                    wynik = wynik + stack.pop() + " ";
                                }
                                wynik += "=";
                                break;
                            }
                            default:
                                if (rownanie.charAt(i) != ' ') {
                                    throw new UnsupportedOperatorException("Nieobsługiwany operator: " + rownanie.charAt(i));
                                }
                        }
                }
                return wynik;
            } else {
                throw new InvalidEquationException("Brak znaku '=' na końcu równania");
            }
        } catch (TabStack.EmptyStackException | TabStack.StackOverflowException | TabStack.InvalidIndexException e) {
            throw new InvalidEquationException("Błąd podczas przetwarzania równania: " + e.getMessage());
        }
    }

    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);
        String tmp = in.nextLine();

        ONP onp = new ONP();
        System.out.print("Wyrażenie wejściowe: " + tmp + " ");

        try {
            String rownanieOnp = onp.przeksztalcNaOnp(tmp);
            System.out.print("Postać ONP: " + rownanieOnp);
            String wynik = onp.obliczOnp(rownanieOnp);
            System.out.println(" Wynik: " + wynik);
        } catch (InvalidEquationException | UnsupportedOperatorException | MathOperationException |
                 TabStack.EmptyStackException | TabStack.StackOverflowException | TabStack.InvalidIndexException e) {
            System.err.println("Błąd: " + e.getMessage());
        }
    }
}
