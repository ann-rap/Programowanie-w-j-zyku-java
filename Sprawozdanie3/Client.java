import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Runnable {
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private Scanner scanner;
    private volatile boolean waitingForAnswer = false;
    private volatile boolean running = true;

    @Override
    public void run() {
        try {
            client = new Socket("127.0.0.1", 999); //polaczenie w sieci lokalnej
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            scanner = new Scanner(System.in);

            // Wątek odbierający wiadomości od serwera
            Thread receiverThread = new Thread(() -> {
                try {
                    String line;
                    while (running && (line = in.readLine()) != null) {
                        wyswietlPytanie(line);

                        if (line.equals("Minął czas")) {
                            waitingForAnswer = false;
                            System.out.println("Czas na odpowiedź minął, oczekiwanie na następne pytanie...");
                        }
                        else if (line.contains(";") || line.startsWith("Podaj")) {
                            waitingForAnswer = true;
                            System.out.print("> ");
                        }

                        else if (line.startsWith("Wynik:")) {
                            waitingForAnswer = false;
                            System.out.println("Test zakonczony!");
                            close();
                            System.out.println("Naciśnij ENTER, aby zamknąć program...");
                            System.exit(0);

                        }

                        else if (!waitingForAnswer) {
                            System.out.print("- ");
                        }
                    }
                } catch (IOException e) {
                    if (running) {
                        System.out.println("Połączenie z serwerem zostało przerwane: " + e.getMessage());
                    }
                }
            });

            receiverThread.start();

            // Główny wątek zajmuje się wprowadzaniem danych
            while (running) {
                if (scanner.hasNextLine()) {
                    String input = scanner.nextLine();

                    if (waitingForAnswer || !receiverThread.isAlive()) {
                        if(input.equals("/quit")) {
                            System.out.println("Przerwano test. Oczekiwanie na wynik...");
                            waitingForAnswer = false;
                        }
                        out.println(input);

                        // Po wysłaniu odpowiedzi na pytanie, czekamy na następne
                        if (waitingForAnswer) {
                            waitingForAnswer = false;
                        }
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Błąd klienta: " + e.getMessage());
        } finally {
            close();
        }
    }

    private void wyswietlPytanie(String linia) {
        if (linia.contains(";")) {
            String[] czesci = linia.split(";");
            for (String czesc : czesci) {
                System.out.println(czesc.trim());
            }
        } else {
            System.out.println(linia);
        }
    }


    private void close() {
        running = false;
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (client != null && !client.isClosed()) client.close();
            if (scanner != null) scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Thread(new Client()).start();
    }
}
