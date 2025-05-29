package dao;
// dao -> Data Access Object.
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet; // ResultSet use korbo database theke returned result hold korar jonno

public class CustomerDAO {
    private Connection conn;

    public CustomerDAO(Connection conn) {
        this.conn = conn;
    }

    // Create customer and return generated customer_id
    public int createCustomer(String firstName, String lastName, String email, String phone, String address) {
        String query = "INSERT INTO customers (first_name, last_name, email, phone, address) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) { //amader database theke auto-generated key return korbe.CustomerID tah ekhane auto-generated
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setString(4, phone);
            stmt.setString(5, address);

            int rows = stmt.executeUpdate(); //return number of rows affected in the database

            if (rows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) { //ekhane Result-set object ekta table-like structure. Jekhane ekta row aar ekta column thakbe
                    if (rs.next()) { //rs.next() diye amader cursor first-row teh point korbe
                        return rs.getInt(1); // return generated customer_id
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1; // failure
    }
}





