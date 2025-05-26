import java.sql.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "kolokwium";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "hasloProgram";

    private Connection connection;

    public DatabaseConnection() throws SQLException {
        initializeDatabase();
    }

    private void initializeDatabase() throws SQLException {
        try {

            connection = DriverManager.getConnection(DB_URL + DB_NAME, USERNAME, PASSWORD);
            System.out.println("Nawiązywanie połączenia...");
            Statement stmt=connection.createStatement();
            if(stmt.executeUpdate("USE " + DB_NAME)==0)
            {
                System.out.println("Nawiązano połączenie z bazą danych.");
            }
        } catch (SQLException e) {
            System.out.println("Baza danych nie istnieje. Tworzenie nowej bazy: " + DB_NAME);
            try (Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                 Statement stmt = conn.createStatement()) {

                stmt.executeUpdate("CREATE DATABASE " + DB_NAME);
                System.out.println("Baza danych utworzona.");
            }
            connection = DriverManager.getConnection(DB_URL + DB_NAME, USERNAME, PASSWORD);
            createAllTables();
            wczytajPytaniaZPliku("bazaPytan.txt");
        }
    }

    private void createAllTables() throws SQLException {
        String createStudenciTable = """
                CREATE TABLE IF NOT EXISTS studenci (
                    nr_albumu VARCHAR(20) PRIMARY KEY,
                    imie_nazwisko VARCHAR(100) NOT NULL)
                """;

        String createWynikiTable = """
                CREATE TABLE IF NOT EXISTS wyniki (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    nr_albumu VARCHAR(20) NOT NULL,
                    wynik INT NOT NULL,
                    max_punkty INT NOT NULL,
                    data_testu TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (nr_albumu) REFERENCES studenci(nr_albumu)
                        ON DELETE CASCADE ON UPDATE CASCADE)
                """;

        String createOdpowiedziTable = """
                CREATE TABLE IF NOT EXISTS odpowiedzi (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    nr_albumu VARCHAR(20) NOT NULL,
                    nr_pytania INT NOT NULL,
                    odpowiedz TEXT,
                    FOREIGN KEY (nr_albumu) REFERENCES studenci(nr_albumu)
                        ON DELETE CASCADE ON UPDATE CASCADE)
                """;

        String createPytaniaTable = """
                CREATE TABLE IF NOT EXISTS pytania (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    tresc TEXT NOT NULL,
                    odpA TEXT,
                    odpB TEXT,
                    odpC TEXT,
                    odpD TEXT,
                    poprawne_odpowiedzi TEXT)
                """;

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(createStudenciTable);
            stmt.executeUpdate(createWynikiTable);
            stmt.executeUpdate(createOdpowiedziTable);
            stmt.executeUpdate(createPytaniaTable);
            System.out.println("Tabele utworzone lub już istnieją.");
        }
    }

    private void wczytajPytaniaZPliku(String nazwaPliku) throws SQLException {
        List<Pytanie> pytania = new ArrayList<>();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(nazwaPliku)) {
            if (is == null) {
                throw new FileNotFoundException("Plik '" + nazwaPliku + "' nie został znaleziony.");
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                String linia;
                while ((linia = br.readLine()) != null) {
                    pytania.add(new Pytanie(linia));
                }
            }

            String insert = "INSERT INTO pytania (tresc, odpA, odpB, odpC, odpD, poprawne_odpowiedzi) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(insert)) {
                for (Pytanie p : pytania) {
                    pstmt.setString(1, p.toString().split(";")[0]);
                    pstmt.setString(2, p.toString().split(";")[1]);
                    pstmt.setString(3, p.toString().split(";")[2]);
                    pstmt.setString(4, p.toString().split(";")[3]);
                    pstmt.setString(5, p.toString().split(";")[4]);
                    pstmt.setString(6, String.join(",", p.getPoprawneOdpowiedzi()));
                    pstmt.executeUpdate();
                }
                System.out.println("Pytania wczytane do bazy danych.");
            }

        } catch (IOException | SQLException e) {
            System.err.println("Błąd przy wczytywaniu pytań: " + e.getMessage());
        }
    }

    public  String pobierzPytanieZBazy(int id) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT id, tresc, odpA, odpB, odpC, odpD FROM pytania WHERE id = ?");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int pid = rs.getInt("id");
                String tresc = rs.getString("tresc");
                String A = rs.getString("odpA");
                String B = rs.getString("odpB");
                String C = rs.getString("odpC");
                String D = rs.getString("odpD");

                return pid + ". " + tresc + ";A. " + A + ";B. " + B + ";C. " + C + ";D. " + D;
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean czyOdpowiedzPoprawnaZBazy(int id, String odpowiedz) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT poprawne_odpowiedzi FROM pytania WHERE id = ?");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String poprawna = rs.getString("poprawne_odpowiedzi").trim().toUpperCase();
                return poprawna.equals(odpowiedz.trim().toUpperCase());
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void zapiszWynik(String nrAlbumu,int wynik, int maxPunkty) throws SQLException {
        String query = "INSERT INTO wyniki (nr_albumu,wynik, max_punkty) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, nrAlbumu);
            pstmt.setInt(2, wynik);
            pstmt.setInt(3, maxPunkty);
            pstmt.executeUpdate();
        }
    }

    public void zapiszOdpowiedz(String nrAlbumu, int nrPytania, String odpowiedz) throws SQLException {
        String query = "INSERT INTO odpowiedzi (nr_albumu, nr_pytania, odpowiedz) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, nrAlbumu);
            pstmt.setInt(2, nrPytania);
            pstmt.setString(3, odpowiedz);
            pstmt.executeUpdate();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Połączenie z bazą danych zamknięte.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}