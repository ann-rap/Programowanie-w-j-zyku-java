import java.io.BufferedWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Pytanie {
    String tresc;
    String odpA;
    String odpB;
    String odpC;
    String odpD;
    Set<String> poprawneOdpowiedzi;

    public Pytanie(String line) {
        int indexOdp = line.indexOf("|");

        String poprawne = "";
        if (indexOdp != -1) {
            poprawne = line.substring(indexOdp + 1).trim(); // część po kresce
            line = line.substring(0, indexOdp).trim(); // obcinamy do kreski
        }

        poprawneOdpowiedzi = new HashSet<>();
        if (!poprawne.isEmpty()) {
            poprawneOdpowiedzi.addAll(Arrays.asList(poprawne.split("\\s*,\\s*")));
        }

        int indexA = line.indexOf("A. ");
        int indexB = line.indexOf("B. ");
        int indexC = line.indexOf("C. ");
        int indexD = line.indexOf("D. ");

        if (indexA == -1 || indexB == -1 || indexC == -1 || indexD == -1) {
            throw new IllegalArgumentException("Nieprawidłowy format pytania: " + line);
        }

        this.tresc = line.substring(0, indexA).trim();
        this.odpA = line.substring(indexA, indexB).trim();
        this.odpB = line.substring(indexB, indexC).trim();
        this.odpC = line.substring(indexC, indexD).trim();
        this.odpD = line.substring(indexD).trim();
    }


    @Override
    public String toString() {
        return tresc + ";" + odpA + ";" + odpB + ";" + odpC + ";" + odpD;
    }

    public boolean czyPoprawne(String odp)
    {
        Set<String> odpowiedziUzytkownika = Arrays.stream(odp.split("\\s*,\\s*"))
                .map(String::toUpperCase)
                .collect(Collectors.toSet());

        return odpowiedziUzytkownika.equals(poprawneOdpowiedzi);

    }


}