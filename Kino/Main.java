import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static ArrayList<Klient> klienci = new ArrayList<>();
    private static ArrayList<Seans> seanse = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        wczytajDane(); // Wczytanie danych z pliku jeśli istnieją
        boolean wyjscie = false;

        while (!wyjscie) {
            wyswietlMenu();
            int wybor = pobierzWybor();

            switch (wybor) {
                case 1:
                    wyswietlKlientow();
                    break;
                case 2:
                    wyswietlSeanse();
                    break;
                case 3:
                    dodajKlienta();
                    break;
                case 4:
                    dodajSeans();
                    break;
                case 5:
                    dokonajRezerwacji();
                    break;
                case 0:
                    zapiszDane(); // Zapis danych przed wyjściem
                    wyjscie = true;
                    System.out.println("Do widzenia!");
                    break;
                default:
                    System.out.println("Nieprawidłowy wybór. Spróbuj ponownie.");
            }
        }
        scanner.close();
    }

    private static void wyswietlMenu() {
        System.out.println("\n===== SYSTEM REZERWACJI SEANSÓW =====");
        System.out.println("1. Wyświetl klientów");
        System.out.println("2. Wyświetl seanse");
        System.out.println("3. Dodaj klienta");
        System.out.println("4. Dodaj seans");
        System.out.println("5. Dokonaj rezerwacji");
        System.out.println("0. Wyjście (Zapis danych)");
        System.out.print("Wybierz opcję: ");
    }

    private static int pobierzWybor() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1; // Nieprawidłowy wybór
        }
    }

    private static void wyswietlKlientow() {
        System.out.println("\n===== LISTA KLIENTÓW =====");
        if (klienci.isEmpty()) {
            System.out.println("Brak klientów w systemie.");
            return;
        }

        for (int i = 0; i < klienci.size(); i++) {
            System.out.println((i + 1) + ". " + klienci.get(i));
        }

        System.out.println("\nCzy chcesz zobaczyć rezerwacje klienta? (T/N)");
        String wybor = scanner.nextLine();

        if (wybor.equalsIgnoreCase("T")) {
            System.out.print("Podaj numer klienta: ");
            try {
                int numerKlienta = Integer.parseInt(scanner.nextLine()) - 1;
                if (numerKlienta >= 0 && numerKlienta < klienci.size()) {
                    String rezerwacje = klienci.get(numerKlienta).wyswietlRezerwacje();
                    if (rezerwacje.isEmpty() || rezerwacje.equals("Rezerwacje:\n")) {
                        System.out.println("Klient nie posiada żadnych zarezerwowanych seansów");
                    } else {
                        System.out.println(rezerwacje);
                    }
                } else {
                    System.out.println("Nieprawidłowy numer klienta.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Nieprawidłowy format numeru.");
            }
        }
    }

    private static void wyswietlSeanse() {
        System.out.println("\n===== LISTA SEANSÓW =====");
        if (seanse.isEmpty()) {
            System.out.println("Brak seansów w systemie.");
            return;
        }

        for (int i = 0; i < seanse.size(); i++) {
            System.out.println((i + 1) + ". " + seanse.get(i).info());
        }

        System.out.println("\nCzy chcesz zobaczyć szczegóły któregoś z seansów? (T/N)");
        String wybor = scanner.nextLine();

        if (wybor.equalsIgnoreCase("T")) {
            System.out.print("Podaj numer seansu: ");
            try {
                int numerSeansu = Integer.parseInt(scanner.nextLine()) - 1;
                if (numerSeansu >= 0 && numerSeansu < seanse.size()) {
                    System.out.println(seanse.get(numerSeansu));
                } else {
                    System.out.println("Nieprawidłowy numer seansu.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Nieprawidłowy format numeru.");
            }
        }

        System.out.println("\nNaciśnij ENTER, aby kontynuować...");
        scanner.nextLine();
    }

    private static void dodajKlienta() {
        System.out.println("\n===== DODAWANIE KLIENTA =====");

        System.out.print("Podaj imię: ");
        String imie = scanner.nextLine();

        System.out.print("Podaj nazwisko: ");
        String nazwisko = scanner.nextLine();


        System.out.print("Podaj e-mail: ");
        String mail = scanner.nextLine();

        System.out.print("Podaj numer telefonu: ");
        String telefon = scanner.nextLine();

        Klient nowyKlient = new Klient(nazwisko, imie, mail, telefon);
        klienci.add(nowyKlient);

        System.out.println("\nKlient został dodany pomyślnie!");
        System.out.println(nowyKlient);
    }

    private static void dodajSeans() {
        System.out.println("\n===== DODAWANIE SEANSU =====");

        System.out.print("Podaj tytuł filmu: ");
        String tytul = scanner.nextLine();

        LocalDate data = null;
        while (data == null) {
            System.out.print("Podaj datę seansu (format: YYYY-MM-DD): ");
            String dataStr = scanner.nextLine();
            try {
                data = LocalDate.parse(dataStr);
            } catch (Exception e) {
                System.out.println("Nieprawidłowy format daty. Spróbuj ponownie.");
            }
        }

        LocalTime godzina = null;
        while (godzina == null) {
            System.out.print("Podaj godzinę seansu (format: HH:MM): ");
            String godzinaStr = scanner.nextLine();
            try {
                godzina = LocalTime.parse(godzinaStr);
            } catch (Exception e) {
                System.out.println("Nieprawidłowy format godziny. Spróbuj ponownie.");
            }
        }

        int ograniczenieWiekowe = -1;
        while (ograniczenieWiekowe < 0) {
            System.out.print("Podaj ograniczenie wiekowe (0 dla braku ograniczeń): ");
            try {
                ograniczenieWiekowe = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Nieprawidłowy format. Podaj liczbę.");
            }
        }

        int rzedy = 0;
        while (rzedy <= 0) {
            System.out.print("Podaj liczbę rzędów: ");
            try {
                rzedy = Integer.parseInt(scanner.nextLine());
                if (rzedy <= 0) {
                    System.out.println("Liczba rzędów musi być większa od 0.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Nieprawidłowy format. Podaj liczbę.");
            }
        }

        int miejscaWRzedzie = 0;
        while (miejscaWRzedzie <= 0) {
            System.out.print("Podaj liczbę miejsc w rzędzie: ");
            try {
                miejscaWRzedzie = Integer.parseInt(scanner.nextLine());
                if (miejscaWRzedzie <= 0) {
                    System.out.println("Liczba miejsc musi być większa od 0.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Nieprawidłowy format. Podaj liczbę.");
            }
        }

        Seans nowySeans = new Seans(tytul, data, godzina, ograniczenieWiekowe, rzedy, miejscaWRzedzie);
        seanse.add(nowySeans);

        System.out.println("\nSeans został dodany pomyślnie!");
        System.out.println(nowySeans.info());
    }

    private static void dokonajRezerwacji() {
        System.out.println("\n===== DOKONYWANIE REZERWACJI =====");

        if (klienci.isEmpty()) {
            System.out.println("Brak klientów w systemie. Najpierw dodaj klienta.");
            return;
        }

        if (seanse.isEmpty()) {
            System.out.println("Brak seansów w systemie. Najpierw dodaj seans.");
            return;
        }

        // Wybór klienta
        System.out.println("\nWybierz klienta:");
        for (int i = 0; i < klienci.size(); i++) {
            System.out.println((i + 1) + ". " + klienci.get(i));
        }

        int indeksKlienta = -1;
        while (indeksKlienta < 0 || indeksKlienta >= klienci.size()) {
            System.out.print("Podaj numer klienta: ");
            try {
                indeksKlienta = Integer.parseInt(scanner.nextLine()) - 1;
                if (indeksKlienta < 0 || indeksKlienta >= klienci.size()) {
                    System.out.println("Nieprawidłowy numer klienta.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Nieprawidłowy format numeru.");
            }
        }

        Klient wybranyKlient = klienci.get(indeksKlienta);

        // Wybór seansu
        System.out.println("\nWybierz seans:");
        for (int i = 0; i < seanse.size(); i++) {
            System.out.println((i + 1) + ". " + seanse.get(i).info());
        }

        int indeksSeansu = -1;
        while (indeksSeansu < 0 || indeksSeansu >= seanse.size()) {
            System.out.print("Podaj numer seansu: ");
            try {
                indeksSeansu = Integer.parseInt(scanner.nextLine()) - 1;
                if (indeksSeansu < 0 || indeksSeansu >= seanse.size()) {
                    System.out.println("Nieprawidłowy numer seansu.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Nieprawidłowy format numeru.");
            }
        }

        Seans wybranySeans = seanse.get(indeksSeansu);

        // Wybór liczby miejsc do rezerwacji
        int liczbaMiejsc = 0;
        while (liczbaMiejsc <= 0) {
            System.out.print("Ile miejsc chcesz zarezerwować? ");
            try {
                liczbaMiejsc = Integer.parseInt(scanner.nextLine());
                if (liczbaMiejsc <= 0) {
                    System.out.println("Liczba miejsc musi być większa od zera.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Nieprawidłowy format liczby.");
            }
        }

        // Rezerwacja miejsc w pętli
        for (int i = 0; i < liczbaMiejsc; i++) {
            System.out.println("\nRezerwacja miejsca " + (i + 1) + " z " + liczbaMiejsc);

            // Wyświetlenie układu miejsc
            System.out.println("\nAktualny układ miejsc:");
            System.out.println(wybranySeans);

            // Wybór miejsca
            System.out.print("Podaj rząd (np. A, B, C): ");
            char rzad = scanner.nextLine().toUpperCase().charAt(0);

            int miejsce = -1;
            while (miejsce < 0) {
                System.out.print("Podaj numer miejsca: ");
                try {
                    miejsce = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Nieprawidłowy format numeru miejsca.");
                }
            }

            // Dodanie rezerwacji
           if(!wybranyKlient.dodajRezerwacje(wybranySeans, rzad, miejsce-1))
               i--;
        }

        // Wyświetlenie aktualnych rezerwacji klienta
        System.out.println("\nAktualne rezerwacje klienta:");
        System.out.println(wybranyKlient.wyswietlRezerwacje());
    }

    // Serializacja listy Seansów do XML
    public static String serializeListToXML(java.util.List<Seans> seanse) {
        XStream xstream = new XStream(new DomDriver());
        xstream.allowTypes(new Class[] {Seans.class, java.util.List.class});  // Allow Seans and List types
        return xstream.toXML(seanse);
    }

    // Deserializacja listy Seansów z XML
    public static java.util.List<Seans> deserializeListFromXML(String xml) {
        XStream xstream = new XStream(new DomDriver());
        xstream.allowTypes(new Class[] {Seans.class});  // Allow Seans class
        return (List<Seans>) xstream.fromXML(xml);
    }

    private static void wczytajDane() {
        try {
            File plikKlienci = new File("klienci.dat");
            File plikSeanse = new File("seanse.xml");

            // Wczytywanie klientów
            if (plikKlienci.exists()) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(plikKlienci))) {
                    klienci = (ArrayList<Klient>) ois.readObject();
                    System.out.println("Wczytano " + klienci.size() + " klientów.");
                }
            } else {
                System.out.println("Plik 'klienci.dat' nie istnieje.");
            }

            // Wczytywanie seansów z XML
            if (plikSeanse.exists()) {
                String xml = new String(Files.readAllBytes(Paths.get("seanse.xml")));
                seanse = (ArrayList<Seans>) deserializeListFromXML(xml);
                System.out.println("Wczytano " + seanse.size() + " seansów.");
            } else {
                System.out.println("Plik 'seanse.xml' nie istnieje.");
            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Błąd podczas wczytywania danych: " + e.getMessage());
        }
    }

    // Zapis danych do plików
    private static void zapiszDane() {
        try {
            // Zapis klientów w formacie binarnym
            try (ObjectOutputStream oosKlienci = new ObjectOutputStream(new FileOutputStream("klienci.dat"))) {
                oosKlienci.writeObject(klienci);
                System.out.println("Zapisano " + klienci.size() + " klientów.");
            }

            // Zapis seansów w formacie XML
            String seanseXML = serializeListToXML(seanse);
            Files.write(Paths.get("seanse.xml"), seanseXML.getBytes());
            System.out.println("Zapisano " + seanse.size() + " seansów.");

        } catch (IOException e) {
            System.out.println("Błąd podczas zapisywania danych: " + e.getMessage());
        }
    }

}
