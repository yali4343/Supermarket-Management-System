package Inventory.Domain;

import Inventory.DTO.ProductDTO;
import Inventory.Repository.ProductRepositoryImpl;

import java.util.HashMap;

public class DiscountController {
    private final HashMap<Integer, Product> products; // All products in memory
    private final ProductRepositoryImpl productRepository;

    public DiscountController(HashMap<Integer, Product> products) {
        this.products = products;
        this.productRepository = new ProductRepositoryImpl();
    }

    public boolean setStoreDiscountForCategory(String category, Discount discount) {
        return applyDiscountToGroup(discount, category, null, -1, DiscountType.STORE, -1);
    }

    public boolean setStoreDiscountForSubCategory(String sub_category, Discount discount) {
        return applyDiscountToGroup(discount, null, sub_category, -1, DiscountType.STORE, -1);
    }

    public boolean setStoreDiscountForCatalogNumber(int catalogNumber, Discount discount) {
        return applyDiscountToGroup(discount, null, null, catalogNumber, DiscountType.STORE, -1);
    }

    public boolean setStoreDiscountForSize(int size, Discount discount) {
        return applyDiscountToGroup(discount, null, null, -1, DiscountType.STORE, size);
    }

    public boolean setSupplierDiscountForCategory(String category, Discount discount) {
        return applyDiscountToGroup(discount, category, null, -1, DiscountType.SUPPLIER, -1);
    }

    public boolean setSupplierDiscountForSubCategory(String sub_category, Discount discount) {
        return applyDiscountToGroup(discount, null, sub_category, -1, DiscountType.SUPPLIER, -1);
    }

    public boolean setSupplierDiscountForCatalogNumber(int catalogNumber, Discount discount) {
        return applyDiscountToGroup(discount, null, null, catalogNumber, DiscountType.SUPPLIER, -1);
    }

    public boolean setSupplierDiscountForSize(int size, Discount discount) {
        return applyDiscountToGroup(discount, null, null, -1, DiscountType.SUPPLIER, size);
    }

    private boolean applyDiscountToGroup(Discount discount, String category, String sub_category,
                                         int catalogNumber, DiscountType type, int size) {
        if (discount.getStartDate() == null || discount.getEndDate() == null || discount.getEndDate().isBefore(discount.getStartDate())) {
            return false;
        }

        boolean applied = false;

        for (Product product : products.values()) {
            boolean match =
                    (category != null && product.getCategory().equalsIgnoreCase(category)) ||
                            (sub_category != null && product.getSubCategory().equalsIgnoreCase(sub_category)) ||
                            (catalogNumber != -1 && product.getCatalogNumber() == catalogNumber) ||
                            (size != -1 && product.getSize() == size);

            if (match) {
                // Apply discount to memory
                if (type == DiscountType.STORE) {
                    product.setStoreDiscount(discount.getDiscountRate());
                } else {
                    product.setSupplierDiscount(discount.getDiscountRate());
                }

                product.setDiscount(discount);
                recalculatePrices(product);

                // Apply to DB
                ProductDTO dto = new ProductDTO(
                        product.getCatalogNumber(),
                        product.getProductName(),
                        product.getCategory(),
                        product.getSubCategory(),
                        product.getSupplierName(),
                        product.getSize(),
                        product.getCostPriceBeforeSupplierDiscount(),
                        product.getSupplierDiscount(),
                        product.getStoreDiscount(),
                        product.getSupplyDaysInTheWeek(),
                        product.getProductDemandLevel()
                );

                dto.setCostPriceAfterSupplierDiscount(product.getCostPriceAfterSupplierDiscount());
                dto.setSalePriceBeforeStoreDiscount(product.getSalePriceBeforeStoreDiscount());
                dto.setSalePriceAfterStoreDiscount(product.getSalePriceAfterStoreDiscount());

                try {
                    productRepository.updateProduct(dto);
                } catch (Exception e) {
                    System.err.println("❌ Failed to update product in DB: " + product.getCatalogNumber());
                }

                applied = true;

                if (catalogNumber != -1) break; // Single product match only
            }
        }

        return applied;
    }

    private void recalculatePrices(Product product) {
        double costAfter = product.getCostPriceBeforeSupplierDiscount() * (1 - product.getSupplierDiscount() / 100.0);
        double saleBefore = costAfter * 2;
        double saleAfter = saleBefore * (1 - product.getStoreDiscount() / 100.0);

        product.setCostPriceAfterSupplierDiscount(costAfter);
        product.setSalePriceBeforeStoreDiscount(saleBefore);
        product.setSalePriceAfterStoreDiscount(saleAfter);
    }
}
