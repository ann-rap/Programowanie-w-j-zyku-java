import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Serwer implements Runnable {
    private List<Pytanie> quiz;
    private ServerSocket server;
    private ExecutorService pool;
    private boolean acceptingClients;
    private final Lock lockZapis = new ReentrantLock();

    public Serwer(String nazwaPliku) throws FileNotFoundException {
        quiz = new ArrayList<>();
        acceptingClients = true;
        pool = Executors.newFixedThreadPool(250);

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(nazwaPliku)) {
            if (is == null) {
                throw new FileNotFoundException("Plik '" + nazwaPliku + "' nie został znaleziony.");
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                String linia;
                while ((linia = br.readLine()) != null) {
                    quiz.add(new Pytanie(linia));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopAcceptingClients()
    {
        acceptingClients=false;
    }


    @Override
    public void run() {

        try{
            server = new ServerSocket(999);
            pool = Executors.newFixedThreadPool(250);
            while(acceptingClients){
                Socket socket = server.accept();
                ClientSession cs = new ClientSession(socket);
                pool.execute(cs);

            }


        }catch(IOException e)
        {
            if (acceptingClients) {
                e.printStackTrace();
            } else {
                System.out.println("Zamknieto serwer.");
            }
        }
        finally{
            try{
                if(server!=null && !server.isClosed()){
                    server.close();
                }
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }

    }

    private void zapiszOdpowiedzi(String dane) {
        try (FileWriter fw = new FileWriter("bazaOdpowiedzi.txt", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter pw = new PrintWriter(bw)) {
            pw.println(dane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void zapiszWynik(String id, int wynik) {
        try (FileWriter fw = new FileWriter("wyniki.txt", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter pw = new PrintWriter(bw)) {
            pw.println(id + " : " + wynik);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class ClientSession implements Runnable {
        private Socket klient;
        private BufferedReader in;
        private PrintWriter out;

        private String nazwa;
        private String nrAlbumu;
        private int obecnePytanie;
        private int wynik;
        private String odp;


        public ClientSession(Socket socket) {
            this.klient = socket;
            this.obecnePytanie = 0;
            this.wynik = 0;
            this.odp = "";
        }

        public void close(){
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (klient != null && !klient.isClosed()) klient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Sesja klienta zakończona: " + nazwa);
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(klient.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(klient.getInputStream()));
                out.println("Podaj numer albumu ");
                nrAlbumu = in.readLine();
                out.println("Podaj swoje imie i nazwisko ");
                nazwa = in.readLine();
                odp += nrAlbumu + " - " + nazwa;

                for (obecnePytanie = 0; obecnePytanie < quiz.size(); obecnePytanie++) {
                    Pytanie pytanie = quiz.get(obecnePytanie);
                    out.println(pytanie);

                    String odpowiedz;
                    klient.setSoTimeout(15000); // 15 sekund na odpowiedź

                    try {
                        odpowiedz = in.readLine();
                        if (odpowiedz != null && odpowiedz.trim().equals("/quit")) {
                            System.out.println("Klient " + nazwa + " przerwał test poleceniem /quit");
                            break;
                        }
                    } catch (SocketTimeoutException e) {
                        out.println("Minął czas");
                        odpowiedz=null;
                    }
                    if(Objects.equals(odpowiedz, "/quit"))
                        break;
                    //odrazu sprawdzamy poprawnosc
                    if (odpowiedz != null && pytanie.czyPoprawne(odpowiedz)) {
                        wynik++;
                    }

                    odp += " " + (obecnePytanie + 1) + ". " + ((odpowiedz != null) ? odpowiedz : "(brak odpowiedzi)");
                }

                out.println("Wynik: " + wynik + "/" + quiz.size());

                lockZapis.lock();
                try {
                    zapiszOdpowiedzi(odp);
                    zapiszWynik(nrAlbumu, wynik);
                } finally {
                    lockZapis.unlock();
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
               close();
            }
        }
    }

    public static void main(String[] args) {
        try{
            Serwer serwer = new Serwer("bazaPytan.txt");
            System.out.println("Uruchomiono serwer.");

            Thread serwerThread = new Thread(serwer);
            serwerThread.start();

            //watek nasluchujacy na zamkniecie serwera
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String command = scanner.nextLine();
                if (command.equalsIgnoreCase("close")) {
                    serwer.stopAcceptingClients();
                    System.out.println("Zatrzymanie przymowania nowych klientow...");


                    if(serwer.server!=null && !serwer.server.isClosed()){
                        serwer.server.close();
                    }
                    serwer.pool.shutdown();
                    while(!serwer.pool.isTerminated()){
                        try{
                            if(serwer.pool.awaitTermination(1, TimeUnit.SECONDS))
                                System.out.println("Wszyscy klienci zakonczyli. Serwer wylaczony");
                        }catch(InterruptedException e){
                            e.printStackTrace();
                        }
                    }
                    break;

                }
            }
        }
        catch(FileNotFoundException e){
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}