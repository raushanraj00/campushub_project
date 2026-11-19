package com.campushub.gui;

import com.campushub.exceptions.BookNotAvailableException;
import com.campushub.gui.theme.IconFactory;
import com.campushub.gui.theme.IconFactory.IconType;
import com.campushub.gui.theme.UIStyles;
import com.campushub.models.Book;
import com.campushub.models.User;
import com.campushub.services.LibraryService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 * UNIT 1: METHOD OVERLOADING DEMONSTRATION
 * UNIT 3: SWING GUI & TRANSACTIONAL CHECKOUT
 * LibraryPanel manages catalog lookup, checkout transactions, and inventory.
 */
public class LibraryPanel extends JPanel {

    private final LibraryService libraryService;
    private final User currentUser;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearchParam1;
    private JTextField txtSearchParam2;
    private JComboBox<String> cbSearchMode;

    public LibraryPanel(User currentUser) {
        this.currentUser = currentUser;
        this.libraryService = new LibraryService();
        setLayout(new BorderLayout(12, 12));
        setBackground(new Color(248, 250, 252));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        initComponents();
        loadBooksData();
    }

    private void initComponents() {
        // Top Toolbar
        JPanel topBar = new JPanel(new BorderLayout(8, 8));
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        JLabel lblTitle = new JLabel("Campus Library & Resource Catalog");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(30, 41, 59));
        topBar.add(lblTitle, BorderLayout.WEST);

        // Search controls demonstrating Method Overloading
        JPanel searchControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        searchControls.setBackground(Color.WHITE);

        cbSearchMode = new JComboBox<>(new String[]{
            "Method Overload 2: Search Title (String)",
            "Method Overload 1: Search ID (int)",
            "Method Overload 3: Search Title & Author (String, String)"
        });
        cbSearchMode.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbSearchMode.addActionListener(e -> updateSearchInputsVisibility());

        txtSearchParam1 = new JTextField(12);
        txtSearchParam1.putClientProperty("JTextField.placeholderText", "Keyword / Title / ID");

        txtSearchParam2 = new JTextField(10);
        txtSearchParam2.putClientProperty("JTextField.placeholderText", "Author name");
        txtSearchParam2.setVisible(false);

        JButton btnSearch = UIStyles.createPrimaryButton("Search", IconType.SEARCH);
        btnSearch.addActionListener(e -> executeOverloadedSearch());

        JButton btnReset = UIStyles.createSecondaryButton("Reset", IconType.DASHBOARD);
        btnReset.addActionListener(e -> {
            txtSearchParam1.setText("");
            txtSearchParam2.setText("");
            loadBooksData();
        });

        searchControls.add(cbSearchMode);
        searchControls.add(txtSearchParam1);
        searchControls.add(txtSearchParam2);
        searchControls.add(btnSearch);
        searchControls.add(btnReset);

        if ("ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            JButton btnAddBook = UIStyles.createPrimaryButton("Add Book", IconType.PLUS);
            btnAddBook.addActionListener(e -> showAddBookDialog());
            searchControls.add(btnAddBook);
        }

        topBar.add(searchControls, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // Books Table
        String[] columns = {"ID", "Title", "Author", "ISBN", "Category", "Total Copies", "Available Copies", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        UIStyles.styleModernTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(UIStyles.BORDER_LIGHT, 1));
        add(scroll, BorderLayout.CENTER);

        // Bottom Actions (Issue Book, Return Book, Fine Calc)
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        bottomBar.setBackground(UIStyles.BG_PAGE);

        JButton btnIssue = UIStyles.createPrimaryButton("Issue Selected Book", IconType.CHECK);
        btnIssue.addActionListener(e -> issueSelectedBook());
        bottomBar.add(btnIssue);

        JButton btnReturn = UIStyles.createSecondaryButton("Return Book & Calculate Fine", IconType.BOOKING);
        btnReturn.addActionListener(e -> returnBookDialog());
        bottomBar.add(btnReturn);

        if ("ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            JButton btnDelete = UIStyles.createDangerButton("Delete Book", IconType.DELETE);
            btnDelete.addActionListener(e -> deleteSelectedBook());
            bottomBar.add(btnDelete);
        }

        add(bottomBar, BorderLayout.SOUTH);
    }

    private void updateSearchInputsVisibility() {
        int index = cbSearchMode.getSelectedIndex();
        txtSearchParam2.setVisible(index == 2); // visible only for Overload 3 (Title & Author)
        revalidate();
        repaint();
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public void loadBooksData() {
        new Thread(() -> {
            List<Book> books = libraryService.getAllBooks();
            displayBooks(books);
        }).start();
    }

    private void displayBooks(List<Book> books) {
        SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0);
            for (Book b : books) {
                tableModel.addRow(new Object[]{
                    b.getBookId(),
                    b.getTitle(),
                    b.getAuthor(),
                    b.getIsbn(),
                    b.getCategory(),
                    b.getTotalQuantity(),
                    b.getAvailableQuantity(),
                    b.isAvailable() ? "In Stock (" + b.getAvailableQuantity() + ")" : "Out of Stock"
                });
            }
        });
    }

    /**
     * UNIT 1: DEMONSTRATING METHOD OVERLOADING
     * Calls one of three overloaded methods in BookDAO based on the selector.
     */
    private void executeOverloadedSearch() {
        int mode = cbSearchMode.getSelectedIndex();
        String p1 = txtSearchParam1.getText().trim();
        String p2 = txtSearchParam2.getText().trim();

        new Thread(() -> {
            List<Book> results;
            if (mode == 1) { // Overload 1: searchBook(int bookId)
                try {
                    int id = Integer.parseInt(p1);
                    results = libraryService.search(id);
                } catch (NumberFormatException e) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, 
                            "Overload 1 requires integer Book ID.", "Type Error", JOptionPane.ERROR_MESSAGE));
                    return;
                }
            } else if (mode == 2) { // Overload 3: searchBook(String title, String author)
                results = libraryService.search(p1, p2);
            } else { // Overload 2: searchBook(String title)
                results = libraryService.search(p1);
            }
            displayBooks(results);
        }).start();
    }

    private void issueSelectedBook() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book from the table first.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int bookId = (int) tableModel.getValueAt(row, 0);
        String title = (String) tableModel.getValueAt(row, 1);
        int available = (int) tableModel.getValueAt(row, 6);

        if (available <= 0) {
            JOptionPane.showMessageDialog(this, 
                    "BookNotAvailableException: All copies of '" + title + "' are currently issued out.", 
                    "Unavailable", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String inputStudentId = JOptionPane.showInputDialog(this, 
                "Enter Student ID for issuing '" + title + "':", "1");
        if (inputStudentId != null && !inputStudentId.trim().isEmpty()) {
            try {
                int studentId = Integer.parseInt(inputStudentId.trim());
                libraryService.issueBookToStudent(studentId, bookId, 14);
                JOptionPane.showMessageDialog(this, 
                        "Success! Book checked out successfully. Due in 14 days.", 
                        "Book Issued", JOptionPane.INFORMATION_MESSAGE);
                loadBooksData();
            } catch (BookNotAvailableException bne) {
                JOptionPane.showMessageDialog(this, bne.getMessage(), "Checkout Conflict", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Checkout error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void returnBookDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Return Book & Fine Calculation", true);
        dialog.setSize(380, 280);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtIssueId = new JTextField("1", 10);
        JTextField txtBookId = new JTextField("1", 10);
        JTextField txtDaysLate = new JTextField("0", 10);

        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Issue Record ID:"), gbc);
        gbc.gridx = 1; panel.add(txtIssueId, gbc);

        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Book ID:"), gbc);
        gbc.gridx = 1; panel.add(txtBookId, gbc);

        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Days Overdue:"), gbc);
        gbc.gridx = 1; panel.add(txtDaysLate, gbc);

        JButton btnProcess = new JButton("Process Return");
        btnProcess.setBackground(new Color(16, 185, 129));
        btnProcess.setForeground(Color.WHITE);
        btnProcess.addActionListener(e -> {
            try {
                int issueId = Integer.parseInt(txtIssueId.getText().trim());
                int bookId = Integer.parseInt(txtBookId.getText().trim());
                int lateDays = Integer.parseInt(txtDaysLate.getText().trim());

                LocalDate due = LocalDate.now().minusDays(lateDays);
                double fine = libraryService.calculateOverdueFine(due, LocalDate.now());

                libraryService.processBookReturn(issueId, bookId, fine);

                JOptionPane.showMessageDialog(dialog, 
                        "Return processed successfully!\nCalculated Overdue Fine: ₹" + fine, 
                        "Return Complete", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadBooksData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Return error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(btnProcess, gbc);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private void showAddBookDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Add Book to Inventory", true);
        dialog.setSize(380, 380);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtTitle = new JTextField(15);
        JTextField txtAuthor = new JTextField(15);
        JTextField txtIsbn = new JTextField(15);
        JTextField txtCat = new JTextField("Computer Science", 15);
        JTextField txtQty = new JTextField("5", 15);

        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1; panel.add(txtTitle, gbc);

        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Author:"), gbc);
        gbc.gridx = 1; panel.add(txtAuthor, gbc);

        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("ISBN:"), gbc);
        gbc.gridx = 1; panel.add(txtIsbn, gbc);

        gbc.gridx = 0; gbc.gridy = 3; panel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1; panel.add(txtCat, gbc);

        gbc.gridx = 0; gbc.gridy = 4; panel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1; panel.add(txtQty, gbc);

        JButton btnSave = new JButton("Save Book");
        btnSave.setBackground(new Color(22, 163, 74));
        btnSave.setForeground(Color.WHITE);
        btnSave.addActionListener(e -> {
            try {
                int q = Integer.parseInt(txtQty.getText().trim());
                Book b = new Book(txtTitle.getText().trim(), txtAuthor.getText().trim(), 
                                  txtIsbn.getText().trim(), txtCat.getText().trim(), q);
                libraryService.registerNewBook(b);
                JOptionPane.showMessageDialog(dialog, "Book added to catalog!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadBooksData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        panel.add(btnSave, gbc);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private void deleteSelectedBook() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a book to delete.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int bookId = (int) tableModel.getValueAt(row, 0);
        String title = (String) tableModel.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Delete book: '" + title + "'?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                libraryService.removeBook(bookId);
                JOptionPane.showMessageDialog(this, "Book removed.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                loadBooksData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

