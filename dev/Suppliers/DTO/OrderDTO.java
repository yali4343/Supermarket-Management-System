package Suppliers.DTO;

import java.time.LocalDateTime;
import java.util.List;

public class OrderDTO {    private int orderID; // unique order identifier
    private long phoneNumber; // customer phone number
    private LocalDateTime orderDate; // order creation date
    private List<OrderItemDTO> items; // list of ordered products

        // New constructor

    public OrderDTO(long phoneNumber, LocalDateTime orderDate, List<OrderItemDTO> items) {
        orderID = 0;
        this.phoneNumber = phoneNumber;
        this.orderDate = orderDate;
        this.items = items;
    }

    public int getOrderID() {
        return orderID;
    }

    public long getPhoneNumber() {
        return phoneNumber;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public void setPhoneNumber(long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }
}
