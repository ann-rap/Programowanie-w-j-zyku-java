/**
 * @author Sławek
 * Klasa implementująca stos za pomocą tablicy
 */
public class TabStack {
    private String[] stack = new String[20];
    private int size = 0;

    /**
     * Wyjątek - pusty stos
     */
    public class EmptyStackException extends RuntimeException {
        public EmptyStackException(String message) {
            super(message);
        }
    }

    /**
     * Wyjątek - pelny stos
     */
    public class StackOverflowException extends RuntimeException {
        public StackOverflowException(String message) {
            super(message);
        }
    }

    /**
     * Wyjątek - nieprawny indeks
     */
    public class InvalidIndexException extends RuntimeException {
        public InvalidIndexException(String message) {
            super(message);
        }
    }

    /**
     * Metoda zdejmująca wartość ze stosu
     * @return wartość z góry stosu
     * @throws EmptyStackException gdy stos jest pusty
     */
    public String pop() {
        if (size <= 0) {
            throw new EmptyStackException("Błąd: pusty stos!");
        }
        size--;
        return stack[size];
    }

    /**
     * metoda dokładająca na stos
     * @param a - wartość dokładana do stosu
     * @throws StackOverflowException gdy stos jest pełny
     */
    public void push(String a) {
        if (size >= stack.length) {
            throw new StackOverflowException("Błąd: stos przepełniony!");
        }
        stack[size] = a;
        size++;
    }

    /**
     * Metoda zwraca tekstową reprezentację stosu
     */
    public String toString() {
        String tmp = "";
        for(int i = 0; i < size; i++)
            tmp += stack[i] + " ";
        return tmp;
    }

    /**
     * Metoda zwraca rozmiar stosu
     * @return size rozmiar stosu
     */
    public int getSize() {
        return size;
    }

    /**
     * Ustawia wartość stosu
     * @param i nowy rozmiar stosu
     * @throws InvalidIndexException gdy próba ustawienia nieprawidłowego rozmiaru
     */
    public void setSize(int i) {
        if (i < 0 || i > stack.length) {
            throw new InvalidIndexException("Nieprawidłowy rozmiar stosu: " + i);
        }
        size = i;
    }

    /**
     * Metoda zwraca wartość z określonej pozycji stosu
     * @param i pozycja parametru do zobaczenia
     * @return wartość stosu lub null jeśli indeks poza zakresem
     * @throws InvalidIndexException gdy podany indeks jest ujemny
     */
    public String showValue(int i) {
        if (i < 0) {
            throw new InvalidIndexException("Ujemny indeks: " + i);
        }
        if (i < size) {
            return stack[i];
        } else {
            return null;
        }
    }
}