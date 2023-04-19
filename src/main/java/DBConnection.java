import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBConnection {

    private static Connection connection;

    private static String dbName = "Voters";
    private static String dbUser = "root";
    private static String dbPass = "root";
    private static StringBuilder insertQuery = new StringBuilder();

    public static Connection getConnection() {

        if (connection == null) {
            try {
                connection = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/" + dbName +
                                "?user=" + dbUser + "&password=" + dbPass);
                connection.createStatement().execute("DROP TABLE IF EXISTS voter_count");
                connection.createStatement().execute("CREATE TABLE voter_count(" +
                        "id INT NOT NULL AUTO_INCREMENT, " +
                        "name TINYTEXT NOT NULL, " +
                        "birthDate DATE NOT NULL, " +
                        "`count` INT NOT NULL, " +
                        "PRIMARY KEY(id))");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }
    //Новый метод
    public static int customSelect() throws SQLException {
        String sql = "SELECT id FROM voter_count WHERE name = 'Исаичев Эмилан'";
        ResultSet rs = DBConnection.getConnection().createStatement().executeQuery(sql);
        if (!rs.next()) {
            return -1;
        } else {
            return rs.getInt("id");
        }
    }

    public static void executeMultiInsert() throws SQLException {
        String sql = "INSERT into voter_count(name, birthDate, `count`)" +
                "VALUES" + insertQuery.toString() +
                "ON DUPLICATE KEY UPDATE `count` = `count` + 1";
        DBConnection.getConnection().createStatement().execute(sql);
    }

    public static void countVoter(String name, String birthDay) throws SQLException {
        birthDay = birthDay.replace('.', '-');

        insertQuery.append((insertQuery.length() > 0 ? "," : "") + "('" + name + "', '" + birthDay + "', 1)");
        if (insertQuery.length() > 400000) {
            executeMultiInsert();
            insertQuery = new StringBuilder();
        }
    }

    public static void printVoterCounts() throws SQLException {
        String sql = "SELECT name, birthDate, `count` FROM voter_count WHERE `count` > 1";
        ResultSet rs = DBConnection.getConnection().createStatement().executeQuery(sql);
        while (rs.next()) {
            System.out.println("\t" + rs.getString("name") + " (" +
                    rs.getString("birthDate") + ") - " + rs.getInt("count"));
        }
    }
}
