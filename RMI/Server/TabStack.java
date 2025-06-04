/**
 * @author Sławek
 * Klasa implementująca stos za pomocą tablicy
 */
public class TabStack {
    private String[] stack = new String[20];
    private int size = 0;

    public class EmptyStackException extends RuntimeException {
        public EmptyStackException(String message) {
            super(message);
        }
    }

    public class StackOverflowException extends RuntimeException {
        public StackOverflowException(String message) {
            super(message);
        }
    }

    public class InvalidIndexException extends RuntimeException {
        public InvalidIndexException(String message) {
            super(message);
        }
    }

    public String pop() {
        if (size <= 0) {
            throw new EmptyStackException("Błąd: pusty stos!");
        }
        size--;
        return stack[size];
    }

    public void push(String a) {
        if (size >= stack.length) {
            throw new StackOverflowException("Błąd: stos przepełniony!");
        }
        stack[size] = a;
        size++;
    }

    public String toString() {
        String tmp = "";
        for(int i = 0; i < size; i++)
            tmp += stack[i] + " ";
        return tmp;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int i) {
        if (i < 0 || i > stack.length) {
            throw new InvalidIndexException("Nieprawidłowy rozmiar stosu: " + i);
        }
        size = i;
    }

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
