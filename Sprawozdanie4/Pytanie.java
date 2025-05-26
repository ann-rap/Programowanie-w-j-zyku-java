import java.io.BufferedWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Pytanie {
    private String tresc;
    private String odpA;
    private String odpB;
    private String odpC;
    private String odpD;
    private Set<String> poprawneOdpowiedzi;

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

        // Usuń numer pytania, np. "1. " na początku
        line = line.replaceFirst("^\\d+\\.\\s*", "");

        int indexA = line.indexOf("A. ");
        int indexB = line.indexOf("B. ");
        int indexC = line.indexOf("C. ");
        int indexD = line.indexOf("D. ");

        if (indexA == -1 || indexB == -1 || indexC == -1 || indexD == -1) {
            throw new IllegalArgumentException("Nieprawidłowy format pytania: " + line);
        }

        this.tresc = line.substring(0, indexA).trim();
        this.odpA = line.substring(indexA + 3, indexB).trim(); // pomiń "A. "
        this.odpB = line.substring(indexB + 3, indexC).trim(); // pomiń "B. "
        this.odpC = line.substring(indexC + 3, indexD).trim(); // pomiń "C. "
        this.odpD = line.substring(indexD + 3).trim();         // pomiń "D. "
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

    public Set<String> getPoprawneOdpowiedzi() {
        return poprawneOdpowiedzi;
    }




}