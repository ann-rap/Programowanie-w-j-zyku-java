public class Set <T extends Comparable>{
    private T [] set;
    private int pojemnosc; //maksymalny rozmiar
    private int rozmiar; //obecna liczba elementow


    public Set(int p){
        this.pojemnosc = p;
        this.rozmiar = 0;
        this.set = (T[]) new Comparable[p];
    }

    public void dodajElement(T elem)  {
        if(rozmiar >= pojemnosc)
            throw new IllegalStateException("Przepelnienie");
        if(this.szukaj(elem)!=-1)
        {
            System.out.println("Element juz jest w zestawie");
            return;}

       int i=rozmiar-1;
        while(i>=0 && set[i].compareTo(elem)>0)
        {
            set[i+1]=set[i];
            i--;}

        set[i+1]=elem;
        rozmiar++;
        
    }

    public int szukaj(T elem)
    {
       for(int i=0;i<rozmiar;i++)
       {
           if(set[i].equals(elem))
               return i;
       }
       return -1; //zwraca -1 jesli nie odnaleziono
    }

    public void usunElement(T elem)
    {
        int i=szukaj(elem);
        if(i!=-1)
        {
            for(int j=i;j<rozmiar;j++)
                set[j]=set[j+1];
            rozmiar--;
        }
    }

    public Set<T> dodajElementy(Set<T> s1, Set<T> s2) {
        Set<T> wynik = new Set<>(s1.pojemnosc + s2.pojemnosc);
        int i = 0, j = 0;

        while (i < s1.rozmiar && j < s2.rozmiar) {
            int cmp = s1.set[i].compareTo(s2.set[j]);
            if (cmp < 0) {
                wynik.dodajElement(s1.set[i++]);
            } else if (cmp > 0) {
                wynik.dodajElement(s2.set[j++]);
            } else {
                wynik.dodajElement(s1.set[i++]);
                j++;
            }
        }

        //reszta pozycji
        while (i < s1.rozmiar) wynik.dodajElement(s1.set[i++]);
        while (j < s2.rozmiar) wynik.dodajElement(s2.set[j++]);

        return wynik;
    }

    public Set<T> OdejmijElementy(Set<T> s1, Set<T> s2) {
        Set<T> wynik = new Set<>(s1.pojemnosc);

        for (int i = 0; i < s1.rozmiar; i++) {
            // Dodaj element, jeśli nie występuje w drugim zbiorze
            if (s2.szukaj(s1.set[i]) == -1) {
                wynik.dodajElement(s1.set[i]);
            }
        }
        return wynik;
    }

    public Set<T> przecięcie(Set<T> s1, Set<T> s2) {
        // Utwórz nowy zbiór o pojemności mniejszej ze zbiorów
        Set<T> wynik = new Set<>(Math.min(s1.pojemnosc, s2.pojemnosc));

        for (int i = 0; i < s1.rozmiar; i++) {
            if (s2.szukaj(s1.set[i]) != -1) {
                wynik.dodajElement(s1.set[i]);
            }
        }
        return wynik;
    }

   @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Pojemnosc: "+ pojemnosc+ " Obecny rozmiar: "+ rozmiar+"\n");
        for(int i=0;i<rozmiar;i++)
            sb.append(set[i]+"\n");
        return sb.toString();
   }
}
