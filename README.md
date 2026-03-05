# 🛒 Supermarket Management System

> A robust **Inventory & Supplier Management System** designed to streamline supermarket operations. This Java-based application manages inventory across multiple branches, automates supplier ordering, handles pricing strategies, and ensures seamless supply chain integration.

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Key Features](#-key-features)
- [System Architecture](#-system-architecture)
- [Modules](#-modules)
- [Technologies](#-technologies)
- [Getting Started](#-getting-started)
- [Project Structure](#-project-structure)

---

## 🚀 Overview

The **Supermarket Management System** is an enterprise-grade solution for managing large-scale retail operations. It bridges the gap between **Inventory Control** and **Supplier Logic**, ensuring that shelves are never empty and products are sourced efficiently.

By automating periodic orders and shortage detection, the system minimizes manual intervention and optimizes stock levels.

---

## ✨ Key Features

### 📦 Inventory Management
*   **Multi-Branch Support:** Track items across varying store branches and warehouse locations.
*   **Defect Tracking:** Manage and report defective or expired items.
*   **Reporting:** Generate comprehensive inventory reports (stock levels, category distribution, defective items).
*   **Item Lifecycle:** Full CRUD operations for products, categories, and specific items.

### 🚚 Supplier Management
*   **Supplier Directory:** Maintain detailed records of suppliers, contacts, and agreements.
*   **Order Automation:** Automatically trigger orders when stock is low or based on a periodic schedule.
*   **Discount Logic:** Handle complex supplier discounts (bulk, quantitative) and store-specific promotions.
*   **Delivery Tracking:** Monitor order history and upcoming deliveries.

### 🔄 Integration
*   **Shortage Detection:** The inventory module communicates with the supplier module to order items instantly when shortages are detected.
*   **Periodic Ordering:** Scheduled automatic restocking based on predefined intervals (e.g., weekly dairy orders).

---

## 🏗 System Architecture

The project is built using a **Layered Architecture** to ensure separation of concerns and maintainability.

1.  **Presentation Layer:** Console-based UI (CLI) handling user input and displaying menus.
2.  **Service/Business Layer:** Contains the core logic (Controllers) for Inventory and Supplier operations.
3.  **Domain Layer:** Entities representing the core business objects (Product, Item, Order, Supplier).
4.  **Data Access Layer (DAO/Repository):** Handles database interactions using **JDBC** and **SQLite**.

### Design Patterns Used
*   **DAO & Repository:** For abstracting data persistence.
*   **DTO (Data Transfer Object):** For passing data between layers without exposing internal entities.
*   **Controller:** For managing business flow and user requests.
*   **Singleton:** For Database connection management.

---

## 📂 Modules

### `Inventory`
This module handles everything related to the physical stock.
-   **DAO:** `JdbcItemDAO`, `JdbcProductDAO`
-   **Domain:** `Item`, `Product`, `Category`
-   **Controllers:** `InventoryController`, `ReportController`

### `Suppliers`
This module manages external relationships and procurement.
-   **DAO:** Supplier-related data access.
-   **Domain:** `Supplier`, `Contract`, `Order`
-   **Controllers:** Supplier Logic.

---

## 🛠 Technologies

*   **Language:** Java 17
*   **Build Tool:** Maven
*   **Database:** SQLite
*   **Testing:** JUnit 5, Mockito
*   **Logging:** SLF4J
*   **Utilities:** SnakeYAML

---

## 🏁 Getting Started

### Prerequisites
*   [Java JDK 17](https://www.oracle.com/java/technologies/downloads/) or higher
*   [Maven](https://maven.apache.org/install.html)

### Installation

1.  **Clone the repository**
    ```bash
    git clone https://github.com/yali4343/supermarket-management-system.git
    cd Supermarket-Management-System
    ```

2.  **Build the project**
    ```bash
    mvn clean install
    ```

3.  **Run the Application**
    The main entry point acts as a unified menu for both Inventory and Supplier systems.
    ```bash
    mvn exec:java -Dexec.mainClass="InventorySupplier.Presentation.InventorySupplierMainMenu"
    ```

### Database
The system uses SQLite databases which are automatically initialized based on the configuration or default setup.
- **Inventory Database:** Stores products, items, orders, discounts, alerts.
- **Supplier Database:** Stores suppliers, agreements, orders.

---

## 📁 Project Structure

```plaintext
dev/
├── Inventory/                  # Inventory Module
│   ├── DAO/                    # Data Access Objects
│   ├── Domain/                 # Business Entities & Logic
│   ├── DTO/                    # Data Transfer Objects
│   ├── Init/                   # DB Initializers
│   ├── Presentation/           # CLI Menus
│   └── Tests/                  # Unit Tests
│
├── Suppliers/                  # Suppliers Module
│   ├── DAO/
│   ├── Domain/
│   ├── DTO/
│   └── Presentation/
│
├── InventorySupplier/          # Integration Module
│   ├── Presentation/           # Main System Menu
│   └── SystemService/          # Cross-module Services
│
└── Integration_And_Unit_Tests/ # System-wide Tests
```

---

## 🧪 Testing

The project includes unit, DAO, and integration tests.
To run tests:
```bash
mvn test
```

---

## 🎓 Learning Goals

This project demonstrates:
- Building a modular backend system in Java.
- Applying layered architecture (Service, Domain, DAO).
- Working with relational databases using DAO/Repository patterns.
- Writing effective unit and integration tests.


