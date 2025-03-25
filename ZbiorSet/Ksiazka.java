import java.util.Objects;

public class Ksiazka implements Comparable<Ksiazka>{
    private String tytul;
    private String autor;
    private int rokWydania;

    public Ksiazka(String tytul, String autor, int rokWydania) {
        this.tytul = tytul;
        this.autor = autor;
        this.rokWydania = rokWydania;
    }

    @Override
    public int compareTo(Ksiazka o) {
        return Integer.compare(this.rokWydania, o.rokWydania);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Ksiazka ksiazka = (Ksiazka) obj;
        return rokWydania == ksiazka.rokWydania &&
                tytul.equals(ksiazka.tytul) &&
                autor.equals(ksiazka.autor);
    }


    @Override
    public String toString() {
        return tytul + " " + autor + " " + rokWydania;
    }
}
