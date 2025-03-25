public class Mieszkanie implements Comparable<Mieszkanie> {
    private String adres;
    int metraz;
    boolean swiatlowod;

    public Mieszkanie(String adres, int metraz, boolean swiatlowod) {
        this.adres = adres;
        this.metraz = metraz;
        this.swiatlowod = swiatlowod;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj == null || getClass() != obj.getClass()) return false;

        Mieszkanie mieszkanie = (Mieszkanie) obj;

        return metraz == mieszkanie.metraz &&
                swiatlowod == mieszkanie.swiatlowod &&
                adres.equals(mieszkanie.adres);
    }


    @Override
    public int compareTo(Mieszkanie o) {
        return Integer.compare(this.metraz, o.metraz);
    }

    @Override
    public String toString() {
        return adres + " " + metraz + " m^2 s:" + swiatlowod;
    }
}
