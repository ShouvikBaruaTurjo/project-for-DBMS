package dao;

import java.sql.*;

public class AccountDAO {
    private Connection conn;

    public AccountDAO(Connection conn) {
        this.conn = conn;
    }

    // Method to create an account
    public boolean createAccount(int customerId, String accountType, double balance) {
        String query = "INSERT INTO accounts (customer_id, account_type, balance) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, customerId);       // Assuming customer_id is provided
            stmt.setString(2, accountType);   // Account type (SAVINGS or CHECKING)
            stmt.setDouble(3, balance);       // Initial balance
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;  // If rows are affected, account is created
        } catch (SQLException e) {
            e.printStackTrace();
            return false;  // Return false in case of error
        }
    }

    // Method to get current balance for a customer
    public double getBalance(int customerId) {
        String query = "SELECT balance FROM accounts WHERE customer_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("balance");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if not found or error
    }

    // Method to withdraw amount from customer's account
    public boolean withdraw(int customerId, double amount) {
        String updateQuery = "UPDATE accounts SET balance = balance - ? WHERE customer_id = ? AND balance >= ?";
        String insertTransaction = "INSERT INTO transactions (account_id, type, amount) VALUES (?, ?, ?)";
        try {
            // Get the account_id for this customer
            String getAccountId = "SELECT account_id FROM accounts WHERE customer_id = ?";
            int accountId = -1;
            try (PreparedStatement stmt = conn.prepareStatement(getAccountId)) {
                stmt.setInt(1, customerId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    accountId = rs.getInt("account_id");
                } else {
                    return false;
                }
            }
            // Update balance
            try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
                stmt.setDouble(1, amount);
                stmt.setInt(2, customerId);
                stmt.setDouble(3, amount);
                if (stmt.executeUpdate() == 0) return false;
            }
            // Insert transaction
            try (PreparedStatement stmt = conn.prepareStatement(insertTransaction)) {
                stmt.setInt(1, accountId);
                stmt.setString(2, "withdrawal");
                stmt.setDouble(3, amount);
                stmt.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deposit(int customerId, double amount) {
        String updateQuery = "UPDATE accounts SET balance = balance + ? WHERE customer_id = ?";
        String insertTransaction = "INSERT INTO transactions (account_id, type, amount) VALUES (?, ?, ?)";
        try {
            // Get the account_id for this customer
            String getAccountId = "SELECT account_id FROM accounts WHERE customer_id = ?";
            int accountId = -1;
            try (PreparedStatement stmt = conn.prepareStatement(getAccountId)) {
                stmt.setInt(1, customerId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    accountId = rs.getInt("account_id");
                } else {
                    return false;
                }
            }
            // Update balance
            try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
                stmt.setDouble(1, amount);
                stmt.setInt(2, customerId);
                if (stmt.executeUpdate() == 0) return false;
            }
            // Insert transaction
            try (PreparedStatement stmt = conn.prepareStatement(insertTransaction)) {
                stmt.setInt(1, accountId);
                stmt.setString(2, "deposit");
                stmt.setDouble(3, amount);
                stmt.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to transfer money from one customer to another
    public boolean transfer(int fromCustomerId, int toCustomerId, double amount) {
        String getFromAccount = "SELECT account_id, balance FROM accounts WHERE customer_id = ?";
        String getToAccount = "SELECT account_id FROM accounts WHERE customer_id = ?";
        String updateFrom = "UPDATE accounts SET balance = balance - ? WHERE account_id = ? AND balance >= ?";
        String updateTo = "UPDATE accounts SET balance = balance + ? WHERE account_id = ?";
        String insertTransaction = "INSERT INTO transactions (account_id, type, amount) VALUES (?, ?, ?)";
        
        try {
            conn.setAutoCommit(false);
            int fromAccountId = -1, toAccountId = -1;
            double fromBalance = 0;
            // Get source account id and balance
            try (PreparedStatement stmt = conn.prepareStatement(getFromAccount)) {
                stmt.setInt(1, fromCustomerId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    fromAccountId = rs.getInt("account_id");
                    fromBalance = rs.getDouble("balance");
                } else {
                    conn.rollback();
                    return false;
                }
            }
            // Get destination account id
            try (PreparedStatement stmt = conn.prepareStatement(getToAccount)) {
                stmt.setInt(1, toCustomerId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    toAccountId = rs.getInt("account_id");
                } else {
                    conn.rollback();
                    return false;
                }
            }
            if (fromBalance < amount) {
                conn.rollback();
                return false;
            }
            // Deduct from source
            try (PreparedStatement stmt = conn.prepareStatement(updateFrom)) {
                stmt.setDouble(1, amount);
                stmt.setInt(2, fromAccountId);
                stmt.setDouble(3, amount);
                if (stmt.executeUpdate() == 0) {
                    conn.rollback();
                    return false;
                }
            }
            // Add to destination
            try (PreparedStatement stmt = conn.prepareStatement(updateTo)) {
                stmt.setDouble(1, amount);
                stmt.setInt(2, toAccountId);
                if (stmt.executeUpdate() == 0) {
                    conn.rollback();
                    return false;
                }
            }
            // Record transfer_out for source
            try (PreparedStatement stmt = conn.prepareStatement(insertTransaction)) {
                stmt.setInt(1, fromAccountId);
                stmt.setString(2, "transfer_out");
                stmt.setDouble(3, amount);
                stmt.executeUpdate();
            }
            // Record transfer_in for destination
            try (PreparedStatement stmt = conn.prepareStatement(insertTransaction)) {
                stmt.setInt(1, toAccountId);
                stmt.setString(2, "transfer_in");
                stmt.setDouble(3, amount);
                stmt.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

}





