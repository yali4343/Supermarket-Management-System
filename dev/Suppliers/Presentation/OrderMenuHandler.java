package Suppliers.Presentation;

import Suppliers.DTO.OrderDTO;
import Suppliers.DTO.OrderItemDTO;
import Suppliers.Domain.OrderManagementController;
import Suppliers.DAO.IOrderDAO;
import Suppliers.DAO.JdbcOrderDAO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class OrderMenuHandler {
    private final OrderManagementController orderManagementController;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public OrderMenuHandler() {
        IOrderDAO orderDAO = new JdbcOrderDAO();
        orderManagementController = new OrderManagementController(orderDAO);
    }

    public void manageOrders(Scanner scanner) throws SQLException {
        int choice;
        do {
            System.out.println("\n===== Order Management Menu =====");
            System.out.println("1. Search Orders");
            System.out.println("2. View All Orders");
            System.out.println("3. View Orders by Supplier");
            System.out.println("4. View Order Details");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter your choice: ");

            choice = scanner.nextInt();
            scanner.nextLine(); // Clear buffer

            switch (choice) {
                case 1:
                    searchOrders(scanner);
                    break;
                case 2:
                    displayOrders(orderManagementController.getAllOrders());
                    break;
                case 3:
                    System.out.print("Enter Supplier ID: ");
                    int supplierId = scanner.nextInt();
                    scanner.nextLine(); // Clear buffer
                    displayOrders(orderManagementController.getOrdersBySupplierId(supplierId));
                    break;
                case 4:
                    System.out.print("Enter Order ID: ");
                    int orderId = scanner.nextInt();
                    scanner.nextLine(); // Clear buffer
                    OrderDTO order = orderManagementController.getOrderById(orderId);
                    if (order != null) {
                        System.out.println("\n===== Order Details =====");
                        System.out.println("Order ID: " + order.getOrderID());
                        System.out.println("Customer Phone: " + order.getPhoneNumber());
                        System.out.println("Order Date: " + order.getOrderDate().format(DATETIME_FORMATTER));
                        System.out.println("\nItems:");
                        for (OrderItemDTO item : order.getItems()) {
                            System.out.println("  • Product ID: " + item.getProductId());
                            System.out.println("    Quantity: " + item.getQuantity());
                            System.out.println("    Supplier ID: " + item.getSupplierId());
                        }
                    } else {
                        System.out.println("Order not found.");
                    }
                    break;
                case 0:
                    System.out.println("Returning to Main Menu...");
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        } while (choice != 0);
    }

    public void searchOrders(Scanner scanner) throws SQLException {
        System.out.println("\n===== Search Orders =====");
        System.out.println("1. Search by date range");
        System.out.println("2. Search by supplier");
        System.out.println("3. Search by date range and supplier");
        System.out.println("4. View all orders");
        System.out.println("0. Back to main menu");
        
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Clear buffer

        LocalDateTime startDate = null;
        LocalDateTime endDate = null;
        Integer supplierId = null;

        switch (choice) {
            case 1:
            case 3:
                try {
                    System.out.print("Enter start date (dd/MM/yyyy) or press Enter for no start date: ");
                    String startDateStr = scanner.nextLine().trim();
                    if (!startDateStr.isEmpty()) {
                        startDate = LocalDate.parse(startDateStr, DATE_FORMATTER).atStartOfDay();
                    }

                    System.out.print("Enter end date (dd/MM/yyyy) or press Enter for no end date: ");
                    String endDateStr = scanner.nextLine().trim();
                    if (!endDateStr.isEmpty()) {
                        endDate = LocalDate.parse(endDateStr, DATE_FORMATTER).plusDays(1).atStartOfDay();
                    }
                } catch (DateTimeParseException e) {
                    System.out.println("❌ Invalid date format. Please use dd/MM/yyyy");
                    return;
                }
                if (choice == 1) break;
                // Fall through if choice == 3
            case 2:
                System.out.print("Enter supplier ID: ");
                supplierId = scanner.nextInt();
                scanner.nextLine(); // Clear buffer
                break;
            case 4:
                break;
            case 0:
                return;
            default:
                System.out.println("Invalid choice!");
                return;
        }

        List<OrderDTO> orders = orderManagementController.searchOrders(startDate, endDate, supplierId);
        displayOrders(orders);
    }

    private void displayOrders(List<OrderDTO> orders) {
        if (orders == null || orders.isEmpty()) {
            System.out.println("No orders found.");
            return;
        }

        System.out.println("\n===== Found Orders =====");
        for (OrderDTO order : orders) {
            System.out.println("\nOrder ID: " + order.getOrderID());
            System.out.println("Customer Phone: " + order.getPhoneNumber());
            System.out.println("Order Date: " + order.getOrderDate().format(DATETIME_FORMATTER));
            System.out.println("Items:");
            
            for (OrderItemDTO item : order.getItems()) {
                System.out.println("  • Product ID: " + item.getProductId());
                System.out.println("    Quantity: " + item.getQuantity());
                System.out.println("    Supplier ID: " + item.getSupplierId());
            }
            System.out.println("----------------------------------------");
        }
    }

    public void printPastOrder() throws SQLException {
        List<OrderDTO> orders = orderManagementController.getAllOrders();
        
        displayOrders(orders);
    }
}

