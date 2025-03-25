public class Main {
    public static void main(String[] args) {
        try{
        // Tworzenie pierwszego zbioru książek
        Set<Ksiazka> biblioteka1 = new Set<>(5);
        biblioteka1.dodajElement(new Ksiazka("W pustyni i w puszczy", "Henryk Sienkiewicz", 1911));
        biblioteka1.dodajElement(new Ksiazka("Potop", "Henryk Sienkiewicz", 1886));
        biblioteka1.dodajElement(new Ksiazka("Quo Vadis", "Henryk Sienkiewicz", 1896));

        // Tworzenie drugiego zbioru książek
        Set<Ksiazka> biblioteka2 = new Set<>(5);
        biblioteka2.dodajElement(new Ksiazka("Potop", "Henryk Sienkiewicz", 1886));
        biblioteka2.dodajElement(new Ksiazka("Pan Tadeusz", "Adam Mickiewicz", 1834));
        biblioteka2.dodajElement(new Ksiazka("Dziady", "Adam Mickiewicz", 1824));

        // Wyświetlenie pierwszej biblioteki
        System.out.println("Biblioteka 1:");
        System.out.println(biblioteka1);

        // Wyświetlenie drugiej biblioteki
        System.out.println("\nBiblioteka 2:");
        System.out.println(biblioteka2);


        // Test sumy zbiorów
        System.out.println("\nSuma bibliotek:");
        Set<Ksiazka> sumaBibliotek = biblioteka1.dodajElementy(biblioteka1, biblioteka2);
        System.out.println(sumaBibliotek);

        // Test różnicy zbiorów
        System.out.println("\nRóżnica bibliotek (elementy z biblioteki 1 nie występujące w bibliotece 2):");
        Set<Ksiazka> roznicaBibliotek = biblioteka1.OdejmijElementy(biblioteka1, biblioteka2);
        System.out.println(roznicaBibliotek);

        // Test przecięcia zbiorów
        System.out.println("\nPrzecinięcie bibliotek:");
        Set<Ksiazka> przeciecieBibliotek = biblioteka1.przecięcie(biblioteka1, biblioteka2);
        System.out.println(przeciecieBibliotek);

        // Test wyszukiwania
        System.out.println(biblioteka1);
        Ksiazka szukanaKsiazka = new Ksiazka("Potop", "Henryk Sienkiewicz", 1886);
        int pozycja = biblioteka1.szukaj(szukanaKsiazka);
        System.out.println("\nWyszukiwanie książki \"Potop\":");
        System.out.println(pozycja != -1 ?
                "Książka znaleziona na pozycji: " + pozycja :
                "Książka nie została znaleziona");

        // Test usuwania elementu
        System.out.println("\nUsuwanie książki \"Quo Vadis\":");
        Ksiazka doUsuniecia = new Ksiazka("Quo Vadis", "Henryk Sienkiewicz", 1896);
        biblioteka1.usunElement(doUsuniecia);
        System.out.println(biblioteka1);}
        catch(Exception e){
            System.out.println(e);
        }
    }
}
