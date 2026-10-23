import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Opens JDBC connections. Credentials come from environment variables, never from code. */
public final class Db {
    private Db() {}

    public static Connection connect() throws SQLException {
        String url = env("DB_URL", "jdbc:mysql://localhost:3306/prasad_db");
        String user = env("DB_USER", "root");
        String pass = env("DB_PASS", "");
        return DriverManager.getConnection(url, user, pass);
    }

    static String env(String key, String fallback) {
        String v = System.getenv(key);
        return (v == null || v.isBlank()) ? fallback : v;
    }
}
