import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class Klient implements Serializable {
    private static final long serialVersionUID = 10L;
    private String nazwisko;
    private String imie;
    private String mail;
    private String telefon;
    private HashMap<Seans, List<Rezerwacja>> rezerwacje;

    public Klient(String nazwisko, String imie, String mail, String telefon) {
        this.nazwisko = nazwisko;
        this.imie = imie;
        this.mail = mail;
        this.telefon = telefon;
        rezerwacje=new HashMap<>();
    }

    public boolean dodajRezerwacje(Seans seans, char rzad, int miejsce) {
        if (seans.zarezerwujMiejsce(rzad, miejsce)) {
            rezerwacje.putIfAbsent(seans, new ArrayList<>());
            rezerwacje.get(seans).add(new Rezerwacja(seans, rzad, miejsce));
            System.out.println("Dodano rezerwację");
            return true;
        } else {
            System.out.println("Nie można zarezerwować tego miejsca");
            return false;
        }
    }

    public String wyswietlRezerwacje() {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<Seans, List<Rezerwacja>> entry : rezerwacje.entrySet()) {
            sb.append("Seans: ").append(entry.getKey().info()).append("\n");

            for (Rezerwacja r : entry.getValue()) {
                sb.append("[").append(r.getRzad()).append(r.getMiejsce()+1).append("] "); // Format: [A1]
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return "Klient: " + nazwisko + ", " + imie + ", " + mail + ", " + telefon;
    }

    public String getNazwisko() {
        return nazwisko;
    }

    public void setNazwisko(String nazwisko) {
        this.nazwisko = nazwisko;
    }

    public String getImie() {
        return imie;
    }

    public void setImie(String imie) {
        this.imie = imie;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public HashMap<Seans, List<Rezerwacja>> getRezerwacje() {
        return rezerwacje;
    }

    public void setRezerwacje(HashMap<Seans, List<Rezerwacja>> rezerwacje) {
        this.rezerwacje = rezerwacje;
    }
}
