package models;

import java.util.Date;

public class Invoice {
    private int invoice_id;
    private Date invoice_date;
    private double total_price;
    private String details;
    private Date createdAt;
    private Date updatedAt;

    public Invoice(int invoice_id, Date invoice_date, double total_price, String details) {
        this.invoice_id = invoice_id;
        this.invoice_date = invoice_date;
        this.total_price = total_price;
        this.details = details;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public int getInvoiceId() {
        return invoice_id;
    }
    public Date getInvoiceDate() {
        return invoice_date;
    }
    public double getTotalPrice() {
        return total_price;
    }
    public String getDetails() {
        return details;
    }
    public Date getCreatedAt() {
        return createdAt;
    }   
    public Date getUpdatedAt() {
        return updatedAt;
    }

}
