# 📦 Inventory & Supplier Management System

A unified Java-based system for managing both inventory operations across multiple branches and supplier relationships.

---

## 👤 Developers

- **Yali Katz** – 211381009
- **Gal Fuerst** – 318256492
- **Maor Livni** – 316117365
- **Ran Erez** – 207285362

---

## 🚀 How to Run the System

### ✅ Prerequisites

Make sure the following are installed on your system:

- Java 17 or higher (Java 21+ recommended)
- Maven (for dependency & build management)
- SQLite JDBC Driver (auto-downloaded via Maven)

---

### ▶️ Launching the System

1. **Clone or download** this project.
2. Open the project in your Java IDE (e.g., IntelliJ).
3. Run the main class: `InventorySupplier.Presentation.InventorySupplierMainMenu`

---

## 🧭 Main Menu Navigation

When the system starts, you'll first be prompted:

**"Do you want to load data to database?"**

- `1` → Load sample data into the database and memory
- `2` → Start with an empty system (no sample data)

Then you'll see the main options:

    Welcome to the Inventory-Suppliers Menu! What would you like to manage?

    1. Inventory System
    2. Supplier System
    3. Exit the Inventory-Suppliers system

---

## 🏪 Inventory Module

1. Enter your **Branch ID** (1–10).
2. Access the Inventory Menu with two main sections:

### Part 1: Core Inventory Functions

- Show item details
- Add/remove items
- Mark item as defective
- Update cost prices
- Apply supplier/store discounts
- Generate inventory & shortage reports
- Update demand/supply parameters
- Change storage location

### Part 2: Order Management & Tracking Menu:

- **Show Shortage Alerts for this branch** - View current shortages without updating inventory
- Update Periodic Order - Modify recurring supplier orders
- **Place Supplier Order Due to Shortage** - Process existing orders and detect new shortages
- View All Periodic Orders - Display all recurring orders for the branch
- View Pending Shortage Orders - Show orders awaiting delivery

**Note:** The "View Current Periodic Orders In Transit" functionality has been removed for streamlined operation.

Data is retrieved using DAOs and Repository patterns. Branch-specific operations are respected.

---

## 📦 Supplier Module

When selected:

- A sample dataset of **8 suppliers**, **agreements**, **products supplied**, and **discounts** is optionally loaded.
- You can:

  - Search supplier
  - Create supplier + agreement + product
  - View past supplier orders
  - Return to the main menu

All related tables are initialized via DAOs.

---

## ❌ Exit Option

- Selecting `3` will **clear all data**:
  - Inventory: products, items, orders, discounts
  - Supplier: suppliers, agreements, discounts, etc.
- Console confirms deletion.

---

## 🏷️ Project Tags

- `#inventory-management`
- `#supplier-management`
- `#java-application`
- `#sqlite-database`
- `#maven-project`
- `#unit-testing`
- `#integration-testing`
- `#dao-pattern`
- `#repository-pattern`
- `#multi-module`

## 📚 Libraries & Tools Used

| Tool / Library | Purpose                         | Version  |
| -------------- | ------------------------------- | -------- |
| Java           | Core programming language       | 23       |
| Maven          | Project management & build tool | 3.11.0   |
| JUnit          | Unit & integration testing      | 4.13.2   |
| JUnit Jupiter  | Extended testing framework      | 5.8.1    |
| SQLite JDBC    | Database connectivity           | 3.44.1.0 |
| Mockito        | Mocking framework for testing   | 5.12.0   |
| SLF4J          | Logging facade                  | 1.7.36   |
| SnakeYAML      | YAML file processing            | 2.0      |

## 🗄️ Database Structure

The system uses two SQLite databases:

### 1. Inventory.db

- Products
- Items
- Orders
- Discounts
- Sales records
- Periodic orders
- Shortage orders

### 2. suppliers.db

- Suppliers
- Agreements
- Product-supplier relationships
- Supplier discounts
- Orders

## 🏗️ Project Structure

```plaintext
dev/
├── Integration_And_Unit_Tests/  # Integration & comprehensive tests
├── Inventory/                   # Inventory management module
│   ├── DAO/                    # Data Access Objects
│   ├── Domain/                 # Business logic
│   ├── DTO/                    # Data Transfer Objects
│   ├── Init/                   # Initialization code
│   ├── Repository/             # Repository implementations
│   └── Tests/                  # Unit tests
├── InventorySupplier/          # Integration layer
└── Suppliers/                  # Supplier management module
    ├── DAO/
    ├── Domain/
    ├── DTO/
    └── Repository/

## 🔍 Key Features

1. **Multi-Branch Inventory Management**
   - Track items across 10 branches
   - Warehouse and store location tracking
   - Defective item marking
   - Stock level monitoring

2. **Dynamic Pricing System**
   - Supplier price tracking
   - Multiple discount types
   - Automatic price calculations
   - Bulk purchase discounts

3. **Supplier Management**
   - Multiple suppliers per product
   - Supplier agreements
   - Delivery schedules
   - Payment terms

5. **Enhanced Order Processing**
   - Periodic orders (recurring supplier orders)
   - Shortage-triggered orders (automatic detection and creation)
   - Improved shortage order processing that handles both existing pending orders and new shortage detection
   - Order history tracking
   - Delivery scheduling
   - Streamlined order management with removed redundant transit tracking

6. **Enhanced Reporting System**
   - Branch-specific shortage alerts (without inventory updates)
   - Sales tracking
   - Price history
   - Inventory reports by category/subcategory/catalog
   - Defective and expired items reports
   - Simplified alert system for better performance
   - Sales tracking
   - Price history
   - Inventory reports by category/subcategory/catalog
   - Defective and expired items reports

## 🧪 Testing

The project includes extensive testing:

1. **Unit Tests**
   - Domain logic testing
   - DAO testing
   - Controller testing

2. **Integration Tests**
   - Cross-module functionality
   - Database operations
   - End-to-end workflows

3. **Price Comparison Tests**
   - Multiple supplier scenarios
   - Discount calculations
   - Bulk order optimizations
|-----------------------|------------------------------------------|
| Java (17+)            | Core language                            |
| SQLite                | Embedded database                        |
| sqlite-jdbc           | JDBC bridge                              |
| Maven                 | Dependency/build tool                    |

---

## 🗃️ Database Schema Overview

All tables are created **dynamically** at runtime using the DAO layer.
Data is inserted through DTO-based preloaders if chosen during startup.

---

### 🏪 Inventory Tables

| Table Name             | Description                                                          |
|------------------------|----------------------------------------------------------------------|
| `products`             | All products across the system                                       |
| `items`                | Instances of products in branches (Warehouse/Store)                  |
| `sold_items`           | Tracks when items were removed/sold                                  |
| `discounts`            | Store discounts per product per branch                               |
| `product_discounts`    | Mapping table for product → discount                                 |
| `branches`             | Represents 10 branch IDs                                             |
| `periodic_orders`      | Scheduled supplier orders (weekly)                                   |
| `shortage_orders`      | Orders triggered by shortages (enhanced processing)                 |

---

### 📦 Supplier Tables

| Table Name             | Description                                                            |
|------------------------|------------------------------------------------------------------------|
| `suppliers`            | Name, contact, payment method, etc.                                   |
| `agreements`           | Days of delivery & standing order status                              |
| `products_supplied`    | Supplier's products, sizes, prices, agreements                        |
| `discounts`            | Tiered discounts from suppliers based on quantity                     |
| `orders`               | Actual purchase orders made to suppliers                              |
| `order_items`          | Items within each supplier order                                      |
| `delivery_terms`       | Derived from `agreements` – not physically stored                     |

---

## 🔁 Preloaded Sample Data (Optional)

### Products (via `preloadProducts()`)

10+ sample products inserted if not already present, each with:

- Unique catalog number
- Name, category, subcategory
- Supplier name
- Prices (base, retail, discount)
- Delivery days
- Associated agreement ID

### Items (via `preloadItems()`)

Each product has instances in various branches:

- Branch ID (1–10)
- Storage location: `Warehouse` or `Store`
- Expiration dates vary per item
- Unique shelf & location identifiers

### Periodic Orders (via `preloadPeriodicOrders()`)

- 3 sample orders created using top 3 supplier-agreement pairs
- Repeats on specific weekdays
- Tracked by DAO in `periodic_orders` table

### Suppliers & Agreements

Loaded using:

- `insertSampleData()` from `SuppliersInitializer`
- 8 sample suppliers
- 8 linked agreements (with different delivery schedules)
- Mapping `supplier_id → agreement_id` stored via `LinkedHashMap`

### Discounts & Product Mapping

Each product is:

- Supplied by one supplier
- Has a size, price, and discount tier
- Linked via `products_supplied` and `discounts` tables

---
Example Supplier Module:
==============================================
Lists all current suppliers
Lets you manage agreements for a selected supplier
Example (with run example):
1. Search Supplier
   List of suppliers:
1. Supplier ID: 10
   Supplier Name: Prigat
   Company ID: 83
--------------------------------------------------
2. Supplier ID: 11
   Supplier Name: Tnuva
   Company ID: 84
--------------------------------------------------
3. Supplier ID: 12
   Supplier Name: Osem
   Company ID: 85
--------------------------------------------------
...

Choose the number of the supplier (1 to 8): 8

What would you like to do next?
1. Manage agreements for this supplier
2. Delete this supplier
0. Return to main menu
   Enter your choice: 1
   Manage agreements...

What would you like to do in the agreement menu?
1. Delete agreement
2. Add new agreement
3. Edit existing agreement
0. return to previous menu

Enter your choice: 2
Let's create a new agreement...
Select delivery days (enter numbers separated by space, then press ENTER):
1. Mon
2. Tue
3. Wed
4. Thu
5. Fri
6. Sat
7. Sun
   Your choices (e.g., 1 3 5): 6
   Self Pickup? (Y/N): N
   Agreement created successfully!.
   Would you like to add products to the agreement? (Y/N): Y

Let's add a product to the agreement:

Let's add a new product...
Enter Catalog Number: 88
Enter Product ID: 88
Enter Product Price: 88
Enter Unit of Measure:
K
Do you want to add new discount rule?
1. Yes
2. No
   Enter your choice: 2
   No discount rules will be added.

Product added successfully.
Would you like to add another product? (Y/N): N
Finished adding products.


What would you like to do in the agreement menu?
1. Delete agreement
2. Add new agreement
3. Edit existing agreement
0. return to previous menu

Enter your choice: 3
Let's edit agreement...
List of agreements for supplier #17:
1. Agreement ID: 13
   Supplier ID: 17
   Self Pickup: Yes
   Delivery Days: FRIDAY
--------------------------------------------------
2. Agreement ID: 14
   Supplier ID: 17
   Self Pickup: No
   Delivery Days: Sat
--------------------------------------------------
Choose the number of the agreement (1 to 2): 2

What would you like to do next?
1. Add new product to this agreement
2. remove product from this agreement
3. Edit product supply terms
4. Edit the delivery days
5. Change selfPickup status
   0.Return to main menu: Enter your choice:
   5
   Toggling self-pickup status...
   Self-pickup status updated. New status: Enabled

What would you like to do next?
1. Add new product to this agreement
2. remove product from this agreement
3. Edit product supply terms
4. Edit the delivery days
5. Change selfPickup status
   0.Return to main menu: Enter your choice:
   4
   Let's edit the delivery days...
   Select delivery days (enter numbers separated by space, then press ENTER):
1. Mon
2. Tue
3. Wed
4. Thu
5. Fri
6. Sat
7. Sun
   Your choices (e.g., 1 3 5): 2
   Delivery days updated to: Tue
   Delivery days updated successfully.


What would you like to do next?
1. Add new product to this agreement
2. remove product from this agreement
3. Edit product supply terms
4. Edit the delivery days
5. Change selfPickup status
   0.Return to main menu: Enter your choice:
   3
   Let's edit the product's supply terms...

Edit Product Supply Terms:
Products in the agreement:
1) Catalog #88 | Product ID: 88 | Unit: K | Price: 88.0
   Enter the number of the product (1-1): 1

Choose what you want to update:
1. Update Product Price
2. Update Unit of Measure
3. Add or Update Discount Rule
0. Return to previous menu
   Enter your choice: 2
   Enter new unit of measure: G
   Product unit updated.

Choose what you want to update:
1. Update Product Price
2. Update Unit of Measure
3. Add or Update Discount Rule
0. Return to previous menu
   Enter your choice: 1
   Enter new price: 99
   Product price updated.

Choose what you want to update:
1. Update Product Price
2. Update Unit of Measure
3. Add or Update Discount Rule
0. Return to previous menu
   Enter your choice: 0
   Returning to previous menu...
   Supply terms updated successfully.


What would you like to do next?
1. Add new product to this agreement
2. remove product from this agreement
3. Edit product supply terms
4. Edit the delivery days
5. Change selfPickup status
   0.Return to main menu: Enter your choice:
   0
   Returning to main menu...

What would you like to do in the agreement menu?
1. Delete agreement
2. Add new agreement
3. Edit existing agreement
0. return to previous menu

Enter your choice: 0
Returning to previous menu...

What would you like to do next?
1. Manage agreements for this supplier
2. Delete this supplier
0. Return to main menu
   Enter your choice: 0
   Return to Main Menu.

========== Suppliers Module Main Menu ==========
1. Search supplier
2. Create new supplier
3. Search for a past order
4. Back to Inventory-Suppliers Main Menu
   Enter your choice: 4
   Returning to the main Inventory-Supplier menu...


==============================================
Welcome to the Inventory-Suppliers Menu! What would you like to manage?
1. Inventory-Suppliers System
2. Inventory System
3. Supplier System
4. Exit the Inventory-Suppliers system
   Enter your choice (1-4): (come back to the same stage)


---

Example Inventory:
==============================================
Welcome to the Inventory-Suppliers Menu! What would you like to manage?
1. Inventory System
2. Supplier System
3. Exit the Inventory-Suppliers system
Enter your choice (1-3): 1
You have selected Inventory.
Enter your Branch ID (1-10): 2
=============================================
|                                            |
|   Welcome to the Inventory Module!     |
|                                            |
=============================================
        You are currently in Branch #2
=============================================

Main Menu:
1. Inventory Functions (Part 1)
2. Order Management & Tracking Menu: (Part 2)
3. Exit

1
Inventory Module Menu:
1. Show item details
2. Add item(s) to inventory (new or existing product)
3. Remove an item
4. Show the purchase prices of a product
5. Mark an item as defective
6. Generate inventory report
7. Generate a defective and expired items report
8. Apply supplier/store discount to a product group
9. Show product quantity in warehouse and store
10. Generate a shortage inventory report
11. Update product demand level
12. Update item storage location
13. Exit

13
Enter item ID:
5
What would you like to change?
(1) Location
(2) Section
(3) Both
1
Enter new location (Warehouse or InteriorStore):
Warehouse
The item was updated successfully.

-----------------------------------------
Item with ID 5 updated successfully in Branch 2.
New location: Warehouse
-----------------------------------------

Inventory Module Menu:
1. Show item details
...
14. Exit

10
Enter Product Catalog Number: 2
🔁 Updating Product [Yellow Cheese 200g] (Catalog #1008)
    📦 Before -> Store: 2 | Warehouse: 1
    🆕 After  -> Store: 5 | Warehouse: 4
The product was updated successfully
🔁 Updating Product [Toilet Paper 12-pack] (Catalog #1009)
    📦 Before -> Store: 0 | Warehouse: 1
    🆕 After  -> Store: 6 | Warehouse: 3
The product was updated successfully
.
.
.
✅ Product quantities updated in database.
The product with Catalog Number 2 does not exist in Branch 2.
Inventory Module Menu:
1. Show item details
...
14. Exit

1
Enter item ID: 5

----------- Item Details -----------
Item ID: 5
Product name: Yellow Cheese 200g
Expiring Date: 2025-06-20
Location: Warehouse, Section: null
Product Catalog Number: 1008, Category: Dairy, Sub-Category: Cheese
Size: 1
Supplier Discount: 12.0%
Cost price before supplier discount: 9.50
Cost price after supplier discount: 8.36
Store Discount: 5.0%
Sale price before store discount: 16.72
Sale price after store discount: 15.88
Product demand: 4
Supply time: TUESDAY, THURSDAY days
supplierName: Tnuva
Defective: No

Inventory Module Menu:
1. Show item details
...
14. Exit

14
Exiting the Inventory menu.
Main Menu:
1. Inventory Functions (Part 1)
2. Supplier & Periodic Orders (Part 2)
3. Exit

2
Order Management & Tracking Menu:
1. Show Shortage Alerts for this branch
2. Update Periodic Order
3. Place Supplier Order Due to Shortage
4. View All Periodic Orders
5. View Pending Shortage Orders
6. Back to Main Menu

1
----------- Shortage Alert Report for Branch #2 -----------
Reorder Alert Report for Branch 2:
Product Catalog Number: 1004, Name: Orange Juice 1L, Total in stock: 0, Minimum required: 1, Missing: 1
Product Catalog Number: 1005, Name: Butter 200g, Total in stock: 0, Minimum required: 2, Missing: 2
Product Catalog Number: 1006, Name: White Rice 1kg, Total in stock: 0, Minimum required: 2, Missing: 2
Product Catalog Number: 1009, Name: Toilet Paper 12-pack, Total in stock: 3, Minimum required: 4, Missing: 1
Product Catalog Number: 1010, Name: Chocolate Bar 100g, Total in stock: 0, Minimum required: 4, Missing: 4
Product Catalog Number: 1011, Name: Mineral Water 1.5L, Total in stock: 0, Minimum required: 1, Missing: 1
Product Catalog Number: 1012, Name: Dish Soap 750ml, Total in stock: 0, Minimum required: 2, Missing: 2
Product Catalog Number: 1013, Name: Cornflakes 750g, Total in stock: 0, Minimum required: 4, Missing: 4
----------------------------------------

Order Management & Tracking Menu:
1. Show Shortage Alerts for this branch
2. Update Periodic Order
3. Place Supplier Order Due to Shortage
4. View All Periodic Orders
5. View Pending Shortage Orders
6. Back to Main Menu

----------------------------------------

## 💡 Notes

- DAO classes are responsible for table creation (`createTableIfNotExists`) and insertion.
- Data preloaders avoid duplication: check if product/item already exists before inserting.
- Periodic orders and shortage orders work alongside inventory & supplier systems.
- **Enhanced shortage processing**: Function 3 now always processes existing orders AND detects new shortages.
- **Simplified alerts**: Function 1 now focuses solely on displaying shortage alerts without inventory updates.
- **Streamlined menu**: Removed redundant "View Current Periodic Orders In Transit" functionality.
- When exiting, all tables are cleared in proper dependency order.

```
