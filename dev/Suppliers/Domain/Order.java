package Suppliers.Domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Represents a customer's order, including the order ID,
 * phone number, date and time of order, and the list of products ordered.
 */
public class Order {
    private int orderID; // Unique identifier of the order
    private long phoneNumber; // Customer's phone number
    private LocalDateTime orderDate; // The date and time when the order was placed
    private int supplierID;

    /**
     * Constructs a new Order with the given details.
     *
     * @param orderID         the unique ID of the order
     * @param phoneNumber     the customer's phone number
     * @param orderDate       the date and time when the order was placed
     * @param productsInOrder a map of product IDs to their ordered quantities
     */
    public Order(int orderID, long phoneNumber, LocalDateTime orderDate, Map<Integer, Integer> productsInOrder,int supplierID) {
        this.orderID = orderID;
        this.phoneNumber = phoneNumber;
        this.orderDate = orderDate;
        this.supplierID = supplierID;
    }

    /**
     * Returns the unique ID of the order.
     *
     * @return the order ID
     */
    public int getOrderID() {
        return orderID;
    }

    /**
     * Returns the customer's phone number.
     *
     * @return the phone number
     */
    public long getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Returns the order date formatted as a string in the pattern "dd/MM/yyyy HH:mm".
     *
     * @return the formatted order date string
     */
    public String getFormattedOrderDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return orderDate.format(formatter);
    }

    /**
     * Returns the supplier's ID associated with this order.
     *
     * @return the supplier ID
     */
    public int getSupplierID() {
        return supplierID;
    }

}
