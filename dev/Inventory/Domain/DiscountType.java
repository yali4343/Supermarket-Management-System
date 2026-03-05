package Inventory.Domain;

/**
 * Represents the type of discount applied to a product.
 *
 * <ul>
 *   <li>{@code SUPPLIER} – A discount provided by the supplier, reducing the product's cost price.</li>
 *   <li>{@code STORE} – A discount applied by the store, reducing the product's sale price.</li>
 * </ul>
 */
public enum DiscountType {
    SUPPLIER,
    STORE
}
