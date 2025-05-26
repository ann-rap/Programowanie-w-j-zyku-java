import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Serwer implements Runnable {
    int num_questions;
    private ServerSocket server;
    private ExecutorService pool;
    private boolean acceptingClients;
    private DatabaseConnection dbConnection;

    public Serwer(int num_questions) throws FileNotFoundException, SQLException {
        dbConnection = new DatabaseConnection();
        this.num_questions = num_questions;
        acceptingClients = true;
        pool = Executors.newFixedThreadPool(250);

    }

    public void stopAcceptingClients()
    {
        acceptingClients=false;
    }


    @Override
    public void run() {
        try{
            server = new ServerSocket(999);
            while(acceptingClients){
                Socket socket = server.accept();
                ClientSession cs = new ClientSession(socket);
                pool.execute(cs); }
        }catch(IOException e) {
            if (acceptingClients)
                e.printStackTrace();
             else
                System.out.println("Zamknieto serwer.");
        }
        finally{
            try{
                if(server!=null && !server.isClosed())
                    server.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
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



        public ClientSession(Socket socket) {
            this.klient = socket;
            this.obecnePytanie = 0;
            this.wynik = 0;
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

                try{
                    dbConnection.dodajStudenta(nrAlbumu, nazwa);
                } catch (SQLException e) {
                    System.out.println("Błąd bazy danych podczas zapisywania studenta");
                    e.printStackTrace();
                }

                for (obecnePytanie = 0; obecnePytanie < num_questions; obecnePytanie++) {
                    String pytanie=dbConnection.pobierzPytanieZBazy(obecnePytanie+1);
                    out.println(pytanie);

                    String odpowiedz=null;
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
                    catch(IOException e) {
                        e.printStackTrace();
                    }
                    if (odpowiedz != null && dbConnection.czyOdpowiedzPoprawnaZBazy(obecnePytanie+1,odpowiedz)) {
                        wynik++;
                    }

                    if(odpowiedz==null) odpowiedz="brak odp";
                    dbConnection.zapiszOdpowiedz(nrAlbumu,obecnePytanie+1,odpowiedz);
                }

                out.println("Wynik: " + wynik + "/" + num_questions);
                
                try {
                    dbConnection.zapiszWynik(nrAlbumu, wynik,num_questions);
                } catch (SQLException e) {
                e.printStackTrace();
            } finally {
               close();
            }} catch (SocketException e) {
                throw new RuntimeException(e);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }}

        }

    public static void main(String[] args) {
        try{
            Serwer serwer = new Serwer(21);
            System.out.println("Uruchomiono serwer.");

            Thread serwerThread = new Thread(serwer);
            serwerThread.start();


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
                            e.printStackTrace(); }
                    }

                    if (serwer.dbConnection != null) {
                        serwer.dbConnection.close();
                    }

                    break;

                }
            }
        }
        catch(FileNotFoundException| SQLException e){
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
