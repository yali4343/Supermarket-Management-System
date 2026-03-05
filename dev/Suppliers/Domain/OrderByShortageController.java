package Suppliers.Domain;

import Suppliers.DTO.*;
import Suppliers.Repository.IInventoryOrderRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

    public class OrderByShortageController {
    private final IInventoryOrderRepository orderRepository;

    public OrderByShortageController(IInventoryOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }


        public List<OrderProductDetailsDTO> getShortageOrderProductDetails(HashMap<Integer,Integer> inventoryProducts, long phoneNumber) throws SQLException {
        List<OrderProductDetailsDTO> productDetails = new ArrayList<>();
        for (int productId : inventoryProducts.keySet()) {
            int quantity = inventoryProducts.get(productId);
            List<ProductSupplierDTO> productSuppliers = orderRepository.getProductsByProductID(productId); // V
            List<DiscountDTO> discountDTOList = orderRepository.getDiscountsForProductByID(productId, quantity);// V

            if (!productSuppliers.isEmpty() && discountDTOList.isEmpty()) {
                productDetails.add(getCheapestPriceWithoutDiscount(productId, quantity, productSuppliers));
            } else if (!productSuppliers.isEmpty() && !discountDTOList.isEmpty()) {
                productDetails.add(discountHandle(productId, quantity, discountDTOList, productSuppliers));
            } else {
                System.out.println("No product supplier found for product id " + productId);
            }
        }
        saveOrder(productDetails, phoneNumber);
        return productDetails;
    }

    public OrderProductDetailsDTO getCheapestPriceWithoutDiscount(int productId, int quantity, List<ProductSupplierDTO> productSuppliers) throws SQLException {
        ProductSupplierDTO productSupplierDTO = orderRepository.getCheapestProductSupplier(productId);// V
        if (productSupplierDTO != null) {
            // בניית אובייקט ההחזרה
            int supplierID = productSupplierDTO.getSupplierID();
            SupplierDTO supplierDTO = orderRepository.getSupplierByID(supplierID); // V
            String supplierName = supplierDTO.getSupplierName();

            int agreementID = productSupplierDTO.getAgreement_id();
            AgreementDTO agreementDTO = orderRepository.getAgreementByID(agreementID);// V
            String[] deliveryDays = agreementDTO.getDeliveryDays();

            double price = productSupplierDTO.getPrice();
            double discount = 0.0;

            return new OrderProductDetailsDTO(
                    supplierID,
                    supplierName,
                    deliveryDays,
                    agreementID,
                    productId,
                    price,
                    discount,
                    quantity
            );
        }
        return null;
    }

    public OrderProductDetailsDTO discountHandle(int productId, int quantity, List<DiscountDTO> discountDTOList, List<ProductSupplierDTO> productSuppliers) throws SQLException {
        double minTotalCost = Double.MAX_VALUE;
        DiscountDTO bestDiscount = null;
        ProductSupplierDTO bestProductSupplier = null;

        for (ProductSupplierDTO ps : productSuppliers) {
            boolean matchedDiscount = false;
            for (DiscountDTO discount : discountDTOList) {
                if (ps.getSupplierID() == discount.getSupplierID() && ps.getAgreement_id() == discount.getAgreementID()) {
                    double discountPercentage = discount.getDiscountPercentage();
                    double totalCost = ps.getPrice() * quantity * (1 - (discountPercentage / 100.0));

                    if (totalCost < minTotalCost) {
                        minTotalCost = totalCost;
                        bestProductSupplier = ps;
                        bestDiscount = discount;
                    }
                    matchedDiscount = true;
                }
            }

            if (!matchedDiscount) {
                double totalCost = ps.getPrice() * quantity;
                if (totalCost < minTotalCost) {
                    minTotalCost = totalCost;
                    bestProductSupplier = ps;
                    bestDiscount = null;
                }
            }
        }

        if (bestProductSupplier == null)
            throw new RuntimeException("No supplier found for product ID: " + productId);

        // בניית אובייקט ההחזרה
        int supplierID = bestProductSupplier.getSupplierID();
        SupplierDTO supplierDTO = orderRepository.getSupplierByID(supplierID);
        String supplierName = supplierDTO.getSupplierName();

        int agreementID = bestProductSupplier.getAgreement_id();
        AgreementDTO agreementDTO = orderRepository.getAgreementByID(agreementID);
        String[] deliveryDays = agreementDTO.getDeliveryDays();

        double price = bestProductSupplier.getPrice();
        double discount = (bestDiscount != null) ? bestDiscount.getDiscountPercentage() : 0.0;

        return new OrderProductDetailsDTO(
                supplierID,
                supplierName,
                deliveryDays,
                agreementID,
                productId,
                price,
                discount,
                quantity
        );
    }



    public void saveOrder(List<OrderProductDetailsDTO> productsDetails, long phoneNumber) throws SQLException {
        if (productsDetails == null || productsDetails.isEmpty()) {
            throw new IllegalArgumentException("Cannot save order: no products provided.");
        }

        List<OrderItemDTO> orderItems = new ArrayList<>();

        for (OrderProductDetailsDTO details : productsDetails) {
            int productID = details.getProductId();
            int supplierID = details.getSupplierId();
            int quantity = details.getQuantity();

            if (quantity > 0) {
                orderItems.add(new OrderItemDTO(productID, supplierID, quantity));
            }
        }

        OrderDTO order = new OrderDTO(phoneNumber, LocalDateTime.now(), orderItems);
        orderRepository.createOrder(order);
    }

}

