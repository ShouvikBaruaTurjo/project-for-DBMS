package ui;

import dao.AccountDAO;
import dao.CustomerDAO;
import util.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CustomerAccountUI extends JFrame { // JFrame holo amader UI Window
    private JTextField firstNameField, lastNameField, emailField, phoneField, addressField, balanceField; //UI er fields gulo
    private JComboBox<String> accountTypeBox; // drop-down box preparation.
    private JTable table; // customers info show korar jonno table
    private DefaultTableModel tableModel; // table er model basic layout

    private Connection conn; //database er shathe connection

    public CustomerAccountUI() { //ei method er moddhe most works are done
        conn = DBConnection.getConnection(); //amader prepared mySQl database er shathe connection preparation er jonno
        CustomerDAO customerDAO = new CustomerDAO(conn); // customer er data handle
        AccountDAO accountDAO = new AccountDAO(conn); // accounts er data handle
        //window layout. Just used the basics
        setTitle("Customer & Account Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 500);
        setLayout(new BorderLayout(10, 10)); //BorderLayout components gulo arrange korbe. I don't have to worry about this.

        // === FORM PANEL ===
        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Create Customer & Account"));
        //nicher egulo input field
        firstNameField = new JTextField();
        lastNameField = new JTextField();
        emailField = new JTextField();
        phoneField = new JTextField();
        addressField = new JTextField();
        balanceField = new JTextField();
        accountTypeBox = new JComboBox<>(new String[]{"SAVINGS", "CHECKING"});
        //components baah field gulo add korlam formPanel e
        formPanel.add(new JLabel("First Name:"));
        formPanel.add(firstNameField);
        formPanel.add(new JLabel("Last Name:"));
        formPanel.add(lastNameField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Phone:"));
        formPanel.add(phoneField);
        formPanel.add(new JLabel("Address:"));
        formPanel.add(addressField);
        formPanel.add(new JLabel("Account Type:"));
        formPanel.add(accountTypeBox);
        formPanel.add(new JLabel("Initial Balance:"));
        formPanel.add(balanceField);

        JButton submitButton = new JButton("Create");
        formPanel.add(submitButton);

        // === TABLE VIEW PANEL ===
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[]{
                "Customer ID", "First Name", "Last Name", "Email", "Account Type", "Balance"
        });
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Existing Customers & Accounts"));

        // === Add components to layout ===
        add(formPanel, BorderLayout.NORTH); //amader formPanel tah top e thakbe. North diye ekhane top bujhacche
        add(scrollPane, BorderLayout.CENTER); // table tah center e thakbe. Window resize korleo shob thik thakar kotha ekhon

        // === BUTTON ACTION ===
        submitButton.addActionListener(e -> {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String address = addressField.getText().trim();
            String accountType = (String) accountTypeBox.getSelectedItem();
            double balance = Double.parseDouble(balanceField.getText().trim());

            int customerId = customerDAO.createCustomer(firstName, lastName, email, phone, address);

            if (customerId > 0) {
                boolean success = accountDAO.createAccount(customerId, accountType, balance);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Customer and account created!");
                    loadTableData();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to create account.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create customer.");
            }
        });

        loadTableData(); // Load data on startup
        setVisible(true);
    }

    private void loadTableData() {
        tableModel.setRowCount(0); // clear table
        String query = """
            SELECT c.customer_id, c.first_name, c.last_name, c.email, a.account_type, a.balance
            FROM customers c
            JOIN accounts a ON c.customer_id = a.customer_id
            """;

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Object[] row = {
                        rs.getInt("customer_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("account_type"),
                        rs.getDouble("balance")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Entry point for running the app
    public static void main(String[] args) {
        SwingUtilities.invokeLater(CustomerAccountUI::new);
    }
}





