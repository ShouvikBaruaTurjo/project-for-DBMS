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
        setSize(900, 600);
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

        // === DEPOSIT PANEL ===
        JPanel depositPanel = new JPanel(new GridLayout(1, 5, 10, 5));
        depositPanel.setBorder(BorderFactory.createTitledBorder("Deposit Funds"));

        JTextField depositCustomerIdField = new JTextField();
        JTextField depositAmountField = new JTextField();
        JButton depositButton = new JButton("Deposit");

        depositPanel.add(new JLabel("Customer ID:"));
        depositPanel.add(depositCustomerIdField);
        depositPanel.add(new JLabel("Amount:"));
        depositPanel.add(depositAmountField);
        depositPanel.add(depositButton);

        // === WITHDRAWAL PANEL ===
        JPanel withdrawPanel = new JPanel(new GridLayout(1, 5, 10, 5));
        withdrawPanel.setBorder(BorderFactory.createTitledBorder("Withdraw Funds"));

        JTextField withdrawCustomerIdField = new JTextField();
        JTextField withdrawAmountField = new JTextField();
        JButton withdrawButton = new JButton("Withdraw");

        withdrawPanel.add(new JLabel("Customer ID:"));
        withdrawPanel.add(withdrawCustomerIdField);
        withdrawPanel.add(new JLabel("Amount:"));
        withdrawPanel.add(withdrawAmountField);
        withdrawPanel.add(withdrawButton);

        // === TRANSFER PANEL ===
        JPanel transferPanel = new JPanel(new GridLayout(1, 6, 10, 5));
        transferPanel.setBorder(BorderFactory.createTitledBorder("Transfer Funds"));

        JTextField transferFromCustomerIdField = new JTextField();
        JTextField transferToCustomerIdField = new JTextField();
        JTextField transferAmountField = new JTextField();
        JButton transferButton = new JButton("Transfer");

        transferPanel.add(new JLabel("From Customer ID:"));
        transferPanel.add(transferFromCustomerIdField);
        transferPanel.add(new JLabel("To Customer ID:"));
        transferPanel.add(transferToCustomerIdField);
        transferPanel.add(new JLabel("Amount:"));
        transferPanel.add(transferAmountField);
        transferPanel.add(transferButton);

        // === TOP PANEL: FORM + DEPOSIT + WITHDRAW + TRANSFER ===
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        JPanel actionsPanel = new JPanel(new GridLayout(3, 1, 10, 10)); // Now 3 rows: Deposit, Withdraw, Transfer
        actionsPanel.add(depositPanel);
        actionsPanel.add(withdrawPanel);
        actionsPanel.add(transferPanel);
        topPanel.add(formPanel, BorderLayout.NORTH);
        topPanel.add(actionsPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // === TABLE VIEW PANEL ===
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[]{
                "Customer ID", "First Name", "Last Name", "Email", "Account Type", "Balance"
        });
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Existing Customers & Accounts"));

        // === Add components to layout ===
        add(scrollPane, BorderLayout.CENTER); // table tah center e thakbe. Window resize korleo shob thik thakar kotha ekhon

        // === CREATE BUTTON ACTION ===
        submitButton.addActionListener(e -> {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String address = addressField.getText().trim();
            String accountType = (String) accountTypeBox.getSelectedItem();

            try {
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
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid balance input.");
            }
        });

        // === DEPOSIT BUTTON ACTION ===
        depositButton.addActionListener(e -> {
            try {
                int customerId = Integer.parseInt(depositCustomerIdField.getText().trim());
                double amount = Double.parseDouble(depositAmountField.getText().trim());

                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid deposit amount.");
                    return;
                }

                boolean success = accountDAO.deposit(customerId, amount);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Deposit successful.");
                    loadTableData();
                } else {
                    JOptionPane.showMessageDialog(this, "Deposit failed.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid input. Please enter numbers only.");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error occurred during deposit.");
            }
        });

        // === WITHDRAW BUTTON ACTION ===
        withdrawButton.addActionListener(e -> {
            try {
                int customerId = Integer.parseInt(withdrawCustomerIdField.getText().trim());
                double amount = Double.parseDouble(withdrawAmountField.getText().trim());

                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid withdrawal amount.");
                    return;
                }

                double currentBalance = accountDAO.getBalance(customerId);
                if (currentBalance < amount) {
                    JOptionPane.showMessageDialog(this, "Insufficient funds.");
                    return;
                }

                boolean success = accountDAO.withdraw(customerId, amount);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Withdrawal successful.");
                    loadTableData();
                } else {
                    JOptionPane.showMessageDialog(this, "Withdrawal failed.");
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid input. Please enter numbers only.");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error occurred during withdrawal.");
            }
        });

        // === TRANSFER BUTTON ACTION ===
        transferButton.addActionListener(e -> {
            try {
                int fromCustomerId = Integer.parseInt(transferFromCustomerIdField.getText().trim());
                int toCustomerId = Integer.parseInt(transferToCustomerIdField.getText().trim());
                double amount = Double.parseDouble(transferAmountField.getText().trim());

                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid transfer amount.");
                    return;
                }
                if (fromCustomerId == toCustomerId) {
                    JOptionPane.showMessageDialog(this, "Cannot transfer to the same customer.");
                    return;
                }

                boolean success = accountDAO.transfer(fromCustomerId, toCustomerId, amount);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Transfer successful.");
                    loadTableData();
                } else {
                    JOptionPane.showMessageDialog(this, "Transfer failed. Check balances and customer IDs.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid input. Please enter numbers only.");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error occurred during transfer.");
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
