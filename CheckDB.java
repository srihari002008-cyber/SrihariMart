import java.sql.*;

public class CheckDB {
    public static void main(String[] args) throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:h2:./data/sriharimart", "SA", "");
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT id, name, email FROM users");
        while(rs.next()) {
            System.out.println(rs.getLong("id") + " | " + rs.getString("name") + " | " + rs.getString("email"));
        }
        conn.close();
    }
}
