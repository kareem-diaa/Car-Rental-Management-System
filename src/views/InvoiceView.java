package views;

import utils.AppColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import car_rental.Main;

import java.io.IOException;
import java.io.InputStream;

public class InvoiceView extends JPanel {

    private String invoiceNumber, customerName, booking_id, date;
    private double total;

    public InvoiceView(Main main,String invoiceNumber, String customerName, String booking_id, 
            double total, String date) {
        this.invoiceNumber = invoiceNumber;
        this.customerName = customerName;
        this.booking_id = booking_id;
        this.total = total;
        this.date = date;

        setLayout(new BorderLayout(10, 10));
        setBackground(AppColors.MAIN_BG);
        setPreferredSize(new Dimension(700, 600)); 
        main.setSize(800, 1000); 
        // Header
        JLabel title = new JLabel("Car Rental Receipt", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setForeground(AppColors.ACCENT_TIFFANY);
        title.setBorder(new EmptyBorder(30, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        // Main content
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(AppColors.MAIN_BG);
        content.setBorder(new EmptyBorder(30, 60, 30, 60));

        content.add(invoiceRow("Invoice #:", invoiceNumber));
        content.add(invoiceRow("Date:", date));
        content.add(invoiceRow("Customer:", customerName));
        content.add(invoiceRow("Booking #", booking_id));
        content.add(Box.createVerticalStrut(16));
        content.add(divider());
        content.add(divider());
        content.add(invoiceRowBold("Total:", String.format("%.2f EGP", total)));

        add(content, BorderLayout.CENTER);

        // Footer panel with thanks and export button
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        footerPanel.setBackground(AppColors.MAIN_BG);

        JLabel thanks = new JLabel("Thank you for choosing us!", SwingConstants.CENTER);
        thanks.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 20));
        thanks.setForeground(AppColors.ACCENT_PURPLE);
        thanks.setAlignmentX(Component.CENTER_ALIGNMENT);
        thanks.setBorder(new EmptyBorder(20, 0, 10, 0));
        footerPanel.add(thanks);

        JButton exportBtn = new JButton("Export as PDF");
        exportBtn.setFont(new Font("Arial", Font.BOLD, 18));
        exportBtn.setBackground(AppColors.ACCENT_TIFFANY);
        exportBtn.setForeground(AppColors.LIGHT_TEXT);
        exportBtn.setFocusPainted(false);
        exportBtn.setBorder(new EmptyBorder(12, 30, 12, 30));
        exportBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        exportBtn.addActionListener(_ -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Invoice as PDF");
            fileChooser.setSelectedFile(new java.io.File("invoice.pdf"));
            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                exportToPDF(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });
        footerPanel.add(exportBtn);

        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel invoiceRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(AppColors.MAIN_BG);
        JLabel l = new JLabel(label);
        l.setForeground(AppColors.LIGHT_TEXT);
        l.setFont(new Font("Arial", Font.PLAIN, 20));
        JLabel v = new JLabel(value);
        v.setForeground(AppColors.LIGHT_TEXT);
        v.setFont(new Font("Arial", Font.PLAIN, 20));
        row.add(l, BorderLayout.WEST);
        row.add(v, BorderLayout.EAST);
        row.setBorder(new EmptyBorder(8, 0, 8, 0));
        return row;
    }

    private JPanel invoiceRowBold(String label, String value) {
        JPanel row = invoiceRow(label, value);
        for (Component c : row.getComponents()) {
            c.setFont(new Font("Arial", Font.BOLD, 24));
            c.setForeground(AppColors.ACCENT_TIFFANY);
        }
        return row;
    }

    private JSeparator divider() {
        JSeparator sep = new JSeparator();
        sep.setForeground(AppColors.DIVIDER_DARK_GRAY);
        sep.setBackground(AppColors.DIVIDER_DARK_GRAY);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        return sep;
    }

    public void exportToPDF(String filePath) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                float margin = 50;
                float yStart = page.getMediaBox().getHeight() - margin;
                float y = yStart;
                float leading = 28;

                // Load font from resources
                InputStream fontStream = getClass().getResourceAsStream("/fonts/LuxuriousRoman-Regular.ttf");
                if (fontStream == null) {
                    throw new IOException("Font file not found in resources!");
                }
                PDType0Font luxuriousFont = PDType0Font.load(document, fontStream);

                // Title
                contentStream.beginText();
                contentStream.setFont(luxuriousFont, 28);
                contentStream.newLineAtOffset(margin, y);
                contentStream.showText("Car Rental Receipt");
                contentStream.endText();

                y -= leading * 2;

                // Invoice info
                y = writeLine(contentStream, "Invoice #: " + invoiceNumber, margin, y, leading, luxuriousFont, 16);
                y = writeLine(contentStream, "Date: " + date, margin, y, leading, luxuriousFont, 16);
                y = writeLine(contentStream, "Customer: " + customerName, margin, y, leading, luxuriousFont, 16);
                y = writeLine(contentStream, "Booking #" + booking_id, margin, y, leading, luxuriousFont, 16);

                y -= leading * 2;

                y = writeLine(contentStream, "Total: " + String.format("%.2f EGP", total), margin, y, leading,
                        luxuriousFont, 20);

                y -= leading * 2;

                // Footer
                contentStream.beginText();
                contentStream.setFont(luxuriousFont, 16);
                contentStream.newLineAtOffset(margin, y);
                contentStream.showText("Thank you for choosing us!");
                contentStream.endText();
            }

            document.save(filePath);
            JOptionPane.showMessageDialog(this, "PDF exported successfully!");

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to export PDF: " + e.getMessage());
        }
    }

    private float writeLine(PDPageContentStream cs, String text, float x, float y, float leading, PDType0Font font,
            int fontSize) throws IOException {
        cs.beginText();
        cs.setFont(font, fontSize);
        cs.newLineAtOffset(x, y);
        cs.showText(text);
        cs.endText();
        return y - leading;
    }
}