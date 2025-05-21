package views;

import controllers.PaymentController;
import models.Payment;
import utils.AppColors;

import car_rental.Main;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminPaymentsView extends JPanel {
    private final PaymentController paymentController;
    private final Main mainFrame;
    private JTable paymentTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public AdminPaymentsView(Main mainFrame) {
        this.mainFrame = mainFrame;
        this.paymentController = new PaymentController();

        setLayout(new BorderLayout(10, 10));
        setBackground(AppColors.MAIN_BG);

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);

        loadPayments(null);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(AppColors.ACCENT_TIFFANY);
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("All Payments", SwingConstants.CENTER);
        titleLabel.setForeground(AppColors.LIGHT_TEXT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(AppColors.ACCENT_TIFFANY);

        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search by User Name");
        styleButton(searchButton);
        searchButton.addActionListener(_ -> loadPayments(searchField.getText().trim()));

        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        headerPanel.add(searchPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(AppColors.MAIN_BG);

        tableModel = new DefaultTableModel(new Object[] {
                "Payment ID", "Booking ID", "User Name", "Amount", "Status", "Method", "Date", "Invoice"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Only the "Invoice" column is editable (for the button)
                return column == 7;
            }
        };

        paymentTable = new JTable(tableModel);
        paymentTable.setFillsViewportHeight(true);
        paymentTable.setRowHeight(30);
        paymentTable.setFont(new Font("Arial", Font.PLAIN, 14));

        // Set custom renderer and editor for the "Invoice" button column
        paymentTable.getColumn("Invoice").setCellRenderer(new InvoiceButtonRenderer());
        paymentTable.getColumn("Invoice").setCellEditor(new InvoiceButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(paymentTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Payments"));
        scrollPane.setPreferredSize(new Dimension(900, 400));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        footerPanel.setBackground(AppColors.ACCENT_TIFFANY);

        JButton backButton = new JButton("Back to Dashboard");
        styleButton(backButton);
        backButton.addActionListener(_ -> navigateBack());
        footerPanel.add(backButton);

        return footerPanel;
    }

    private void styleButton(JButton button) {
        button.setBackground(AppColors.ACCENT_TIFFANY);
        button.setForeground(AppColors.LIGHT_TEXT);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    }

    private void loadPayments(String userName) {
        tableModel.setRowCount(0);
        List<Payment> payments = paymentController.getPaymentsByUserName(userName);
        if (payments == null || payments.isEmpty()) {
            tableModel.addRow(new Object[] { "No payments found", "", "", "", "", "", "", "" });
            paymentTable.setEnabled(false);
        } else {
            for (Payment payment : payments) {
                tableModel.addRow(new Object[] {
                        payment.getPaymentId(),
                        payment.getBookingId(),
                        payment.getUserName() != null ? payment.getUserName() : "",
                        payment.getAmount(),
                        payment.getPaymentStatus(),
                        payment.getPaymentMethod(),
                        payment.getPaymentDate(),
                        "Make Invoice"
                });
            }
            paymentTable.setEnabled(true);
        }
    }

    private void navigateBack() {
        mainFrame.setSize(600, 400);
        mainFrame.getContentPane().removeAll();
        mainFrame.add(new AdminDashboard(mainFrame));
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    // Renderer for the "Invoice" button
    class InvoiceButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public InvoiceButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            setText(value == null ? "Make Invoice" : value.toString());
            return this;
        }
    }

    // Editor for the "Invoice" button
    class InvoiceButtonEditor extends DefaultCellEditor {
        private JButton button;
        private boolean isPushed;
        private int row;

        public InvoiceButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("Make Invoice");
            button.setOpaque(true);
            button.addActionListener(_ -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                int column) {
            this.row = row;
            isPushed = true;
            button.setText(value == null ? "Make Invoice" : value.toString());
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                // Get payment data from the table model
                int paymentId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
                int booking_id = Integer.parseInt(tableModel.getValueAt(row, 1).toString()); 
                String userName = tableModel.getValueAt(row, 2).toString(); 
                double amount = Double.parseDouble(tableModel.getValueAt(row, 3).toString());
                String date = tableModel.getValueAt(row, 6).toString();

                InvoiceView invoiceView = new InvoiceView(
                        mainFrame,
                        "INV-" + paymentId,
                        userName,
                        "Booking-" + booking_id,
                        amount,
                        date);
                JFrame invoiceFrame = new JFrame("Invoice");
                invoiceFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                invoiceFrame.setContentPane(invoiceView);
                invoiceFrame.pack();
                invoiceFrame.setLocationRelativeTo(button);
                invoiceFrame.setVisible(true);
            }
            isPushed = false;
            return "Make Invoice";
        }
    }
}