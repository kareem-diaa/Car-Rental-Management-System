package controllers;

import dao.PaymentDAO;
import models.Payment;

import java.util.List;

public class PaymentController {
    private final PaymentDAO paymentDAO;

    public PaymentController() {
        this.paymentDAO = new PaymentDAO();
    }

    public boolean addPayment(Payment payment) {
        return paymentDAO.addPayment(payment, payment.getBookingId());
    }

    public Payment getPaymentById(int paymentId) {
        return paymentDAO.getPaymentById(paymentId);
    }

    public boolean updatePayment(Payment payment) {
        return paymentDAO.updatePayment(payment);
    }

    public boolean deletePayment(Payment payment) {
        return paymentDAO.deletePayment(payment.getPaymentId());
    }

    public List<Payment> getPaymentsByBookingId(int bookingId) {
        return paymentDAO.getPaymentsByBookingId(bookingId);
    }

    public List<Payment> getAllPayments() {
        return paymentDAO.getAllPayments();
    }

    public List<Payment> getPaymentsByUserName(String userName) {
        if (userName == null || userName.trim().isEmpty()) {
            return paymentDAO.getAllPayments();
        }
        return paymentDAO.getPaymentsByUserName(userName.trim());
    }
}