package Inventory.Repository;

import Inventory.DAO.IProductDAO;
import Inventory.DAO.JdbcProductDAO;
import Inventory.DTO.ItemDTO;
import Inventory.DTO.ProductDTO;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductRepositoryImpl implements IProductRepository {
    private final IProductDAO productdao;

    public ProductRepositoryImpl() {
        this.productdao = new JdbcProductDAO();
    }

    @Override
    public void addProduct(ProductDTO product) throws SQLException {
        productdao.Insert(product);
    }

    @Override
    public void updateProduct(ProductDTO product) throws SQLException {
        productdao.Update(product);
    }

    @Override
    public void deleteProduct(int catalogNumber) throws SQLException {
        productdao.DeleteByCatalogNumber(catalogNumber);
    }

    @Override
    public ProductDTO getProductByCatalogNumber(int catalogNumber) throws SQLException {
        return productdao.GetProductByCatalogNumber(catalogNumber);
    }

    @Override
    public List<ProductDTO> getAllProducts() throws SQLException {
        return productdao.getAllProducts();
    }

    @Override
    public boolean productExists(int catalogNumber) throws SQLException {
        ProductDTO product = productdao.GetProductByCatalogNumber(catalogNumber);
        return product != null;
    }

    @Override
    public void updateQuantityInStore(int catalogNumber, int quantity) throws SQLException {
        ProductDTO product = productdao.GetProductByCatalogNumber(catalogNumber);
        if (product != null) {
            product.setQuantityInStore(quantity);
            productdao.Update(product);
        }
    }

    @Override
    public void UpdateCostPrice(int catalogNumber, double newCostPrice) throws SQLException {
        productdao.UpdateCostPrice(catalogNumber, newCostPrice);
    }


    @Override
    public void updateQuantityInWarehouse(int catalogNumber, int quantity) throws SQLException {
        ProductDTO product = productdao.GetProductByCatalogNumber(catalogNumber);
        if (product != null) {
            product.setQuantityInWarehouse(quantity);
            productdao.Update(product);
        }
    }

    @Override
    public void updateQuantities(int catalogNumber, int storeQuantity, int warehouseQuantity) throws SQLException {
        ProductDTO product = productdao.GetProductByCatalogNumber(catalogNumber);
        if (product != null) {
            product.setQuantityInStore(storeQuantity);
            product.setQuantityInWarehouse(warehouseQuantity);
            productdao.Update(product);
        }
    }

    public void updateQuantitiesFromItems(List<ItemDTO> items) throws SQLException {
        Map<Integer, Integer> storeQuantities = new HashMap<>();
        Map<Integer, Integer> warehouseQuantities = new HashMap<>();

        for (ItemDTO item : items) {
            if (item.IsDefective()) continue;
            int catalog = item.getCatalogNumber();
            String location = item.getStorageLocation();

            if ("Warehouse".equalsIgnoreCase(location)) {
                warehouseQuantities.put(catalog, warehouseQuantities.getOrDefault(catalog, 0) + 1);
            } else if ("InteriorStore".equalsIgnoreCase(location) || "Store".equalsIgnoreCase(location)) {
                storeQuantities.put(catalog, storeQuantities.getOrDefault(catalog, 0) + 1);
            }
        }

        for (Integer catalogNumber : storeQuantities.keySet()) {
            int storeQty = storeQuantities.getOrDefault(catalogNumber, 0);
            int warehouseQty = warehouseQuantities.getOrDefault(catalogNumber, 0);

            ProductDTO product = productdao.GetProductByCatalogNumber(catalogNumber);
            if (product != null) {
                System.out.printf("🔁 Updating Product [%s] (Catalog #%d)\n", product.getProductName(), catalogNumber);
                System.out.printf("    📦 Before -> Store: %d | Warehouse: %d\n", product.getQuantityInStore(), product.getQuantityInWarehouse());
                System.out.printf("    🆕 After  -> Store: %d | Warehouse: %d\n", storeQty, warehouseQty);
            }

            updateQuantities(catalogNumber, storeQty, warehouseQty);
        }

        for (Integer catalogNumber : warehouseQuantities.keySet()) {
            if (!storeQuantities.containsKey(catalogNumber)) {
                int warehouseQty = warehouseQuantities.get(catalogNumber);

                ProductDTO product = productdao.GetProductByCatalogNumber(catalogNumber);
                if (product != null) {
                    System.out.printf("🔁 Updating Product [%s] (Catalog #%d)\n", product.getProductName(), catalogNumber);
                    System.out.printf("    📦 Before -> Store: %d | Warehouse: %d\n", product.getQuantityInStore(), product.getQuantityInWarehouse());
                    System.out.printf("    🆕 After  -> Store: 0 | Warehouse: %d\n", warehouseQty);
                }

                updateQuantities(catalogNumber, 0, warehouseQty);
            }
        }
    }

    @Override
    public void UpdateCalculatedPrices(ProductDTO product) throws SQLException {
        productdao.UpdateCalculatedPrices(product);
    }

    @Override
    public List<ProductDTO> getProductsBySizes(List<Integer> sizes) throws SQLException {
        return productdao.getProductsBySizes(sizes);
    }
}
