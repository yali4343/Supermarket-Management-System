package Inventory.Domain;

import Inventory.DAO.JdbcProductDAO;
import Inventory.DTO.ProductDTO;

import Inventory.InventoryUtils.DateUtils;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import Inventory.DTO.ItemDTO;
import Inventory.Repository.IItemRepository;
import Inventory.Repository.IProductRepository;
import Inventory.Repository.ItemRepositoryImpl;
import Inventory.Repository.ProductRepositoryImpl;

/**
 * Controller class for generating inventory-related reports.
 * Manages reports for defective, expired, and low-stock items per branch.
 */
public class ReportController {
    private final HashMap<Integer, Branch> branches;
    private final HashMap<Integer, Product> products;
    private final IItemRepository itemRepository;
    private final IProductRepository productRepository;

    /**
     * Constructs a new ReportController.
     *
     * @param branches Map of branch IDs to Branch objects.
     * @param products Map of product catalog numbers to Product objects.
     */
    public ReportController(HashMap<Integer, Branch> branches, HashMap<Integer, Product> products) {
        this.branches = branches;
        this.products = products;
        this.productRepository = new ProductRepositoryImpl();
        this.itemRepository = new ItemRepositoryImpl();
    }


    public String defectAndExpiredReport(int current_branch_id) {
        StringBuilder report = new StringBuilder();
        LocalDate today = LocalDate.now();

        IItemRepository itemRepository = new ItemRepositoryImpl();
        IProductRepository productRepository = new ProductRepositoryImpl();

        Map<Integer, ProductDTO> productCache = new HashMap<>();

        // Get all items in branch for defect check
        List<ItemDTO> items;
        items = itemRepository.getItemsByBranchId(current_branch_id);

        if (items.isEmpty()) {
            return "No items found for Branch " + current_branch_id + ".\n";
        }

        report.append("Defective Items in Branch ").append(current_branch_id).append(":\n");
        int counter = 1;
        boolean hasDefects = false;

        for (ItemDTO item : items) {
            if (item.IsDefective()) {
                int catalogNumber = item.getCatalogNumber();
                ProductDTO product = productCache.computeIfAbsent(catalogNumber, id -> {
                    try {
                        return productRepository.getProductByCatalogNumber(id);
                    } catch (SQLException e) {
                        System.err.println("❌ Failed to fetch product " + id + ": " + e.getMessage());
                        return null;
                    }
                });

                if (product != null) {
                    hasDefects = true;
                    report.append(counter++).append(". Item ID: ").append(item.getItemId())
                            .append(", Name: ").append(product.getProductName())
                            .append(", Category: ").append(product.getCategory())
                            .append(", Sub-Category: ").append(product.getSubCategory())
                            .append(", Size: ").append(product.getSize())
                            .append(", Location: ").append(item.getStorageLocation())
                            .append(", Section: ").append(item.getSectionInStore())
                            .append("\n");
                }
            }
        }

        if (!hasDefects) {
            report.append("No defective items found in Branch ").append(current_branch_id).append(".\n");
        }

        report.append("\nExpired Items in Branch ").append(current_branch_id).append(":\n");

        // Use the new optimized DB method
        List<ItemDTO> expiredItems;
        try {
            expiredItems = itemRepository.getExpiredItemsByBranchId(current_branch_id, today);
        } catch (SQLException e) {
            return "❌ Failed to retrieve expired items: " + e.getMessage();
        }

        if (expiredItems.isEmpty()) {
            report.append("No expired items found in Branch ").append(current_branch_id).append(".\n");
        } else {
            counter = 1;
            for (ItemDTO item : expiredItems) {
                int catalogNumber = item.getCatalogNumber();
                ProductDTO product = productCache.computeIfAbsent(catalogNumber, id -> {
                    try {
                        return productRepository.getProductByCatalogNumber(id);
                    } catch (SQLException e) {
                        System.err.println("❌ Failed to fetch product " + id + ": " + e.getMessage());
                        return null;
                    }
                });

                if (product != null) {
                    report.append(counter++).append(". Item ID: ").append(item.getItemId())
                            .append(", Name: ").append(product.getProductName())
                            .append(", Expired on: ").append(item.getItemExpiringDate())
                            .append(", Category: ").append(product.getCategory())
                            .append(", Sub-Category: ").append(product.getSubCategory())
                            .append(", Size: ").append(product.getSize())
                            .append(", Location: ").append(item.getStorageLocation())
                            .append("\n");
                }
            }
        }

        return report.toString();
    }





    public String inventoryReportByCategories(String[] categories, int branchId, List<Integer> sizeFilters) {
        try {        // Get all items from current branch
            List<ItemDTO> items = itemRepository.getItemsByBranch(branchId);

            // Get all products relevant to requested size
            List<ProductDTO> productList = productRepository.getProductsBySizes(sizeFilters);
            Map<Integer, ProductDTO> productMap = new HashMap<>();
            for (ProductDTO p : productList) {
                productMap.put(p.getCatalogNumber(), p);
            }

            Map<String, List<ItemDTO>> categoryMap = new HashMap<>();

            for (ItemDTO item : items) {
                if (item.IsDefective()) continue;

                ProductDTO product = productMap.get(item.getCatalogNumber());
                if (product == null) continue;

                for (String cat : categories) {
                    if (product.getCategory().trim().equalsIgnoreCase(cat.trim())) {
                        categoryMap.computeIfAbsent(cat.trim(), k -> new ArrayList<>()).add(item);
                    }
                }
            }

            if (categoryMap.isEmpty()) {
                return "No matching categories found in Branch " + branchId + ".";
            }

            StringBuilder report = new StringBuilder();
            for (String category : categoryMap.keySet()) {
                report.append("Category: ").append(category).append("\n");
                for (ItemDTO item : categoryMap.get(category)) {
                    ProductDTO product = productMap.get(item.getCatalogNumber());
                    report.append("  Item ID: ").append(item.getItemId())
                            .append(", Catalog: ").append(item.getCatalogNumber())
                            .append(", Product Name: ").append(product.getProductName())
                            .append(", Size: ").append(product.getSize()).append("\n");
                }
            }

            return report.toString();

        } catch (SQLException e) {
            System.err.println("❌ DB error while generating report: " + e.getMessage());
            return "Error generating report.";
        }
    }


    public String inventoryReportBySubCategories(String[] subCategories, int branchId, List<Integer> sizeFilters) {
        try {
            // שליפת כל הפריטים מהסניף
            List<ItemDTO> items = itemRepository.getItemsByBranch(branchId);

            // שליפת כל המוצרים הרלוונטיים לפי גודל
            List<ProductDTO> productList = productRepository.getProductsBySizes(sizeFilters);
            Map<Integer, ProductDTO> productMap = new HashMap<>();
            for (ProductDTO p : productList) {
                productMap.put(p.getCatalogNumber(), p);
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            StringBuilder report = new StringBuilder();
            boolean anyMatch = false;

            for (String subCategory : subCategories) {
                List<ItemDTO> matchedItems = new ArrayList<>();

                for (ItemDTO item : items) {
                    if (item.IsDefective()) continue;
                    ProductDTO product = productMap.get(item.getCatalogNumber());
                    if (product == null) continue;

                    if (product.getSubCategory().trim().equalsIgnoreCase(subCategory.trim())) {
                        matchedItems.add(item);
                    }
                }

                report.append("Sub-Category: ").append(subCategory.trim()).append("\n");

                if (matchedItems.isEmpty()) {
                    report.append("  No matching items found for sub-category.\n");
                } else {
                    anyMatch = true;
                    matchedItems.sort(Comparator.comparing(i -> {
                        try {
                            return LocalDate.parse(i.getItemExpiringDate(), formatter);
                        } catch (Exception e) {
                            return LocalDate.MAX;
                        }
                    }));

                    int count = 1;
                    for (ItemDTO item : matchedItems) {
                        ProductDTO product = productMap.get(item.getCatalogNumber());
                        report.append("    ").append(count++).append(". ")
                                .append("Item ID: ").append(item.getItemId())
                                .append(", Name: ").append(product.getProductName())
                                .append(", Size: ").append(product.getSize())
                                .append(", Expiring date: ").append(item.getItemExpiringDate())
                                .append("\n");
                    }
                }

                report.append("------------------------------------------------------------\n");
            }

            if (!anyMatch) {
                return "No valid sub-categories found or no items matching size filters.";
            }

            return report.toString();

        } catch (SQLException e) {
            return "Error accessing database: " + e.getMessage();
        }
    }



    public String inventoryReportByCatalogNumbers(String[] catalogNumbers, int branchId, List<Integer> sizeFilters) {
        try {            // Step 1: Convert input to a Set of catalog numbers
            Set<Integer> catalogSet = new HashSet<>();
            for (String s : catalogNumbers) {
                try {
                    catalogSet.add(Integer.parseInt(s.trim()));
                } catch (NumberFormatException ignored) {}
            }

            if (catalogSet.isEmpty()) {
                return "No valid catalog numbers entered.";
            }

            // שלב 2: שליפת פריטים מהסניף
            List<ItemDTO> items = itemRepository.getItemsByBranch(branchId);

            // שלב 3: שליפת מוצרים מתאימים לפי sizeFilters
            List<ProductDTO> productList = productRepository.getProductsBySizes(sizeFilters);
            Map<Integer, ProductDTO> productMap = new HashMap<>();
            for (ProductDTO p : productList) {
                if (catalogSet.contains(p.getCatalogNumber())) {
                    productMap.put(p.getCatalogNumber(), p);
                }
            }

            // שלב 4: יצירת הדוח
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            StringBuilder report = new StringBuilder();
            boolean anyMatch = false;

            for (Integer catalogNumber : catalogSet) {
                report.append("Catalog Number: ").append(catalogNumber).append("\n");

                ProductDTO product = productMap.get(catalogNumber);
                if (product == null) {
                    report.append("  No matching items found for catalog number.\n");
                    report.append("------------------------------------------------------------\n");
                    continue;
                }

                List<ItemDTO> matchedItems = new ArrayList<>();
                for (ItemDTO item : items) {
                    if (item.IsDefective()) continue;
                    if (item.getCatalogNumber() == catalogNumber) {
                        matchedItems.add(item);
                    }
                }

                if (matchedItems.isEmpty()) {
                    report.append("  No matching items found for catalog number.\n");
                } else {
                    anyMatch = true;
                    matchedItems.sort(Comparator.comparing(i -> {
                        try {
                            return LocalDate.parse(i.getItemExpiringDate(), formatter);
                        } catch (Exception e) {
                            return LocalDate.MAX;
                        }
                    }));

                    int count = 1;
                    for (ItemDTO item : matchedItems) {
                        report.append("    ").append(count++).append(". ")
                                .append("Item ID: ").append(item.getItemId())
                                .append(", Name: ").append(product.getProductName())
                                .append(", Location: ").append(item.getStorageLocation())
                                .append(", Expiring date: ").append(item.getItemExpiringDate())
                                .append("\n");
                    }
                }

                report.append("------------------------------------------------------------\n");
            }

            if (!anyMatch) {
                return "No matching catalog numbers found or no items matching size filters.";
            }

            return report.toString();

        } catch (SQLException e) {
            return "Error generating catalog report from DB: " + e.getMessage();
        }
    }


    public String generateShortageInventoryReport(int branch_id) {
        StringBuilder report = new StringBuilder();
        boolean found = false;        // Use database data instead of in-memory data to get current stock counts
        Map<Integer, Integer> stockCountMap = new HashMap<>();
        List<ItemDTO> branchItems = itemRepository.getItemsByBranchId(branch_id);
        for (ItemDTO item : branchItems) {
            if (!item.IsDefective()) {
                int catalog_number = item.getCatalogNumber();
                stockCountMap.put(catalog_number, stockCountMap.getOrDefault(catalog_number, 0) + 1);
            }
        }

        report.append("Reorder Alert Report for Branch ").append(branch_id).append(":\n");

        // Load products from the database using DTOs
        JdbcProductDAO productDAO = new JdbcProductDAO();
        List<ProductDTO> dbProducts = productDAO.getAllProducts();

        for (ProductDTO productDTO : dbProducts) {
            int catalog_number = productDTO.getCatalogNumber();
            Product product = products.get(catalog_number);

            if (product == null) {
                System.out.println("⚠️ Warning: product not found in memory for catalog " + catalog_number);
                continue;
            }            int inStock = stockCountMap.getOrDefault(catalog_number, 0);

            String days = product.getSupplyDaysInTheWeek();
            if (days == null || days.trim().isEmpty()) {
                continue;
            }

            int supplyTime = DateUtils.calculateNextSupplyDayOffset(days);
            int min_required = Math.max(1, (int) (0.5 * product.getProductDemandLevel() + 0.5 * supplyTime));

            if (inStock < min_required) {
                found = true;
                int missing = min_required - inStock;

                report.append("Product Catalog Number: ").append(catalog_number)
                        .append(", Name: ").append(product.getProductName())
                        .append(", Total in stock: ").append(inStock)
                        .append(", Minimum required: ").append(min_required)
                        .append(", Missing: ").append(missing);

                Discount discount = product.getDiscount();
                if (discount != null && discount.isActive()) {
                    report.append(", Active Discount: ").append(discount.getDiscountRate()).append("%")
                            .append(" (").append(discount.getStartDate()).append(" to ").append(discount.getEndDate()).append(")");
                }                report.append("\n");
            }
        }

        if (!found) {
            return "All the products in Branch " + branch_id + " are above their minimum required amount.";
        }

        return report.toString();
    }



    /**
     * Checks if a specific product in a branch has fallen below the minimum quantity
     * and needs a reorder alert after removal.
     *
     * @param branch_id the ID of the branch
     * @param catalog_number the catalog number of the product
     * @return true if a reorder alert should be triggered, false otherwise
     */
    public boolean shouldTriggerAlertAfterRemoval(int branch_id, int catalog_number) {
        Branch branch = branches.get(branch_id);
        if (branch == null) return false;

        long count = branch.getItems().values().stream()
                .filter(item -> item.getCatalogNumber() == catalog_number && !item.IsDefective())
                .count();

        Product product = products.get(catalog_number);
        if (product == null) return false;

        int min_required = product.getMinimumQuantityForAlert();

        return count < min_required;
    }    public Map<Integer, Integer> getShortageProductsMap(int branchId) {
        Map<Integer, Integer> shortages = new HashMap<>();        // Use database data instead of in-memory data to get current stock counts
        Map<Integer, Integer> stockCount = new HashMap<>();
        List<ItemDTO> branchItems = itemRepository.getItemsByBranchId(branchId);
        for (ItemDTO item : branchItems) {
            if (!item.IsDefective()) {
                int catalog = item.getCatalogNumber();
                stockCount.put(catalog, stockCount.getOrDefault(catalog, 0) + 1);
            }
        }

        // בדיקת דרישות מינימום לכל מוצר
        for (Map.Entry<Integer, Product> entry : products.entrySet()) {
            int catalogNumber = entry.getKey();
            Product product = entry.getValue();

            String supplyDays = product.getSupplyDaysInTheWeek();
            if (supplyDays == null || supplyDays.isEmpty()) continue;

            int supplyOffset = DateUtils.calculateNextSupplyDayOffset(supplyDays);
            int requiredMin = Math.max(1, (int) (0.5 * product.getProductDemandLevel() + 0.5 * supplyOffset));
            int inStock = stockCount.getOrDefault(catalogNumber, 0);

            if (inStock < requiredMin) {
                int shortage = requiredMin - inStock;
                shortages.put(catalogNumber, shortage);
            }
        }

        return shortages;
    }






}
