import java.io.Serializable;

public class Rezerwacja implements Serializable {
    private static final long serialVersionUID = 30L;
        private Seans seans;
        private char rzad;
        private int miejsce;

        public Rezerwacja(Seans seans, char rzad, int miejsce) {
            this.seans = seans;
            this.rzad = rzad;
            this.miejsce = miejsce;
        }

        public Seans getSeans() {
            return seans;
        }

        public char getRzad() {
            return rzad;
        }

        public int getMiejsce() {
            return miejsce;
        }
    }

