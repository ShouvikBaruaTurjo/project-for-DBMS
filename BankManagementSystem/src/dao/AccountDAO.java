package dao; //package er name DAO disi. Karon ei pakaage er under shob class gulo diye amra Data Acess Object ready korbo. Ei objects guloi database ke access kore various data manipulation korte parbe.

import java.sql.*; // sql related classes gulo import korar jonno. ekhane je Connection, PreparedStatement, SQLException class gulo use kortesi, ei libary er karanei partesi.

public class AccountDAO {
    private Connection conn; // ekhane conn holo ekta Connection object. We need this. Database er shathe etai amader link

    public AccountDAO(Connection conn) { //constructor
        this.conn = conn; //ei constructor ekta existing Connection "conn" niye ei class er "conn" variable e assign korbe
    }

    // account creation er method
    public boolean createAccount(int customerId, String accountType, double balance) {
        String query = "INSERT INTO accounts (customer_id, account_type, balance) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) { //Amra sqlstatement tah ekhane prepare korlam
            stmt.setInt(1, customerId);//customer ID set korlam
            stmt.setString(2, accountType); //account type set korlam
            stmt.setDouble(3, balance); // balance set korlam
            int rowsAffected = stmt.executeUpdate(); // sql query tah execute korlo and number of rows affected return korlo. eta int type
            return rowsAffected > 0;  // If rows are affected, account is created. jodi ektao row affected na hoy, tahole eta false return korbe
        } catch (SQLException e) { //jodi try fail kore tahole sqlException error show korbe. Hoito kono sql syntax error chilo etc..
            e.printStackTrace(); // e.printStackTrace IDE er kon number line e, kon method e error show kortese, sheta dekhabe.
            return false;  // Return false in case of error
        }
    }
}




