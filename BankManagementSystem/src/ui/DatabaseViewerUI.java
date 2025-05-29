package ui;

import util.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class DatabaseViewerUI extends JFrame {
    private JTable customerTable;
    private JTable accountTable;

    public DatabaseViewerUI() {
        setTitle("Database Viewer - Customers and Accounts");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        customerTable = new JTable();
        accountTable = new JTable();

        tabbedPane.addTab("Customers", new JScrollPane(customerTable));
        tabbedPane.addTab("Accounts", new JScrollPane(accountTable));

        add(tabbedPane, BorderLayout.CENTER);
        loadCustomerData();
        loadAccountData();

        setVisible(true);
    }

    private void loadCustomerData() {
        String[] columns = {"ID", "First Name", "Last Name", "Email", "Phone", "Address"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        Connection conn = DBConnection.getConnection();

        String query = "SELECT * FROM customers";
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("customer_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address")
                };
                model.addRow(row);
            }
            customerTable.setModel(model);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading customers.");
        }
    }

    private void loadAccountData() {
        String[] columns = {"Account ID", "Customer ID", "Type", "Balance"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        Connection conn = DBConnection.getConnection();

        String query = "SELECT * FROM accounts";
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("account_id"),
                        rs.getInt("customer_id"),
                        rs.getString("account_type"),
                        rs.getDouble("balance")
                };
                model.addRow(row);
            }
            accountTable.setModel(model);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading accounts.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DatabaseViewerUI::new);
    }
}

