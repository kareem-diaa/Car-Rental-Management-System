package controllers;

import dao.BookingDAO;
import dao.CarDAO;
import models.Booking;
import models.Car;

import java.util.List;

public class BookingController {
    private final BookingDAO bookingDAO;

    public BookingController() {
        this.bookingDAO = new BookingDAO();
    }

    // Add a new booking
    public boolean addBooking(int userId, int carId, String status, java.util.Date startDate, java.util.Date endDate) {
        Booking booking = new Booking();
        booking.setCustomerId(userId);
        booking.setCarId(carId);
        booking.setStatus(status);
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);
        
        return bookingDAO.addBooking(booking);
    }

    // Retrieve a booking by ID
    public Booking getBookingById(int bookingId) {
        return bookingDAO.getBookingById(bookingId);
    }

    // Retrieve all bookings for a specific user
    public List<Booking> getBookingsByUserId(int userId) {
        return bookingDAO.getBookingsByUserId(userId);
    }

    // Retrieve all bookings
    public List<Booking> getAllBookings() {
        return bookingDAO.getAllBookings();
    }

    // Update an existing booking
    public boolean updateBooking(int bookingId, int userId, int carId, String status, java.util.Date startDate, java.util.Date endDate) {
        Booking booking = new Booking();
        booking.setBookingId(bookingId);
        booking.setCustomerId(userId);
        booking.setCarId(carId);
        booking.setStatus(status);
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);
        return bookingDAO.updateBooking(booking);
    }

    // update booking status 
public boolean updateBookingStatus(int bookingId, String status) {
    Booking booking = bookingDAO.getBookingById(bookingId);
    if (booking != null) {
        booking.setStatus(status);
        boolean bookingUpdated = bookingDAO.updateBooking(booking);

        if ("Accepted".equalsIgnoreCase(status)) {
            CarDAO carDAO = new CarDAO();
            Car car = carDAO.getCarById(booking.getCarId());
            if (car != null) {
                car.setAvailabilityStatus(false);
                carDAO.updateCar(car);
            }
        }
        return bookingUpdated;
    }
    return false;
}

    // Delete a booking by ID
    public boolean deleteBooking(int bookingId) {
        return bookingDAO.deleteBooking(bookingId);
    }
}