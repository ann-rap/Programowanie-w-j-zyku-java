import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class Seans implements Serializable {
    private static final long serialVersionUID = 20L;

    private String tytul;
    private LocalDate data;
    private LocalTime godzina;
    private int ograniczenie_wiekowe=-1; //domyslnie brak ograniczenia
    private HashMap<Character, HashMap<Integer, Boolean>> miejsca;

    public Seans(String t, LocalDate d, LocalTime g, int w, int rzedzy, int w_rzedzie ) {
        this.tytul = t;
        this.data = d;
        this.godzina = g;
        this.ograniczenie_wiekowe = w;
        this.miejsca = new HashMap<>();

        stworzMiejsca(rzedzy,w_rzedzie);
    }

    private void stworzMiejsca(int rzedzy, int w_rzedzie)
    {
        char rzad='A';
        for(int i=0; i<rzedzy; i++)
        {
            HashMap<Integer, Boolean> cols = new HashMap<>();
            for(int j=0; j<w_rzedzie; j++)
                cols.put(j, false);
            miejsca.put((char)(rzad+i),cols);
        }
    }

    public boolean zarezerwujMiejsce(char rzad, int miejsce)
    {
        if(miejsca.containsKey(rzad) && miejsca.get(rzad).containsKey(miejsce))
        {
            if(!miejsca.get(rzad).get(miejsce))
            {
                miejsca.get(rzad).put(miejsce,true);
                return true; //zarezerwowano
            }
        }
        return false; //juz zajete lub niepoprawne
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Seans: ").append(tytul).append(", ").append(data).append(", ").append(godzina).append(", Wiek: ").append(ograniczenie_wiekowe).append("\n");
        for (Map.Entry<Character, HashMap<Integer, Boolean>> entry : miejsca.entrySet()) {
            sb.append(entry.getKey()).append(": ");
            for (Map.Entry<Integer, Boolean> miejsce : entry.getValue().entrySet()) {
                sb.append((miejsce.getValue() ? "[X]" : "[ ]")).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public String info()
    {
        return tytul + " | "+data+" | "+godzina +" | PG:"+ograniczenie_wiekowe;
    }


    public String getTytul() {
        return tytul;
    }

    public LocalDateTime getCzas() {
        return LocalDateTime.of(data, godzina);
    }

    public String getFormattedCzas() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return getCzas().format(formatter);
    }



}

