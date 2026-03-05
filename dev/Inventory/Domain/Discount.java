package Inventory.Domain;

import java.time.LocalDate;

/**
 * Represents a discount applied to a product, defined by a percentage rate
 * and a validity period (start and end dates).
 */
public class Discount {
    private double discount_rate;  // Discount rate as a percentage (e.g., 10 for 10%)
    private LocalDate start_date;  // Start date of the discount
    private LocalDate end_date;  // End date of the discount


    /**
     * Constructs a new Discount object with the given rate and date range.
     *
     * @param discount_rate The percentage value of the discount (0–100).
     * @param start_date    The starting date of the discount period.
     * @param end_date      The ending date of the discount period.
     */
    public Discount(double discount_rate, LocalDate start_date, LocalDate end_date) {
        this.setDiscountRate(discount_rate);
        this.setStartDate(start_date);
        this.setEndDate(end_date);
    }

    /**
     * @return The discount rate as a percentage (e.g., 15 for 15%).
     */
    public double getDiscountRate() {
        return discount_rate;
    }

    /**
     * Sets the discount rate. Must be between 0 and 100.
     *
     * @param discount_rate The new discount rate.
     */
    public void setDiscountRate(double discount_rate) {
        if (discount_rate < 0 || discount_rate > 100) {
            System.out.println("Discount rate must be between 0 and 100");
            return;
        }
        this.discount_rate = discount_rate;
    }

    /**
     * @return The start date of the discount.
     */
    public LocalDate getStartDate() {
        return start_date;
    }

    /**
     * Sets the start date for the discount. Must not be null or after the end date (if already set).
     *
     * @param start_date The starting date of the discount.
     */
    public void setStartDate(LocalDate start_date) {
        if (start_date == null) {
            System.out.println("Error: Start date cannot be null.");
            return;  // Exit the method if start date is null
        }
        if (end_date != null && start_date.isAfter(end_date)) {
            System.out.println("Error: Start date cannot be after end date.");
            return;  // Exit the method if start date is after the end date
        }
        this.start_date = start_date;  // Set the start date only if it's valid
    }

    /**
     * @return The end date of the discount.
     */
    public LocalDate getEndDate() {
        return end_date;
    }

    /**
     * Sets the end date for the discount. Must not be null or before the start date (if already set).
     *
     * @param end_date The ending date of the discount.
     */
    public void setEndDate(LocalDate end_date) {
        if (end_date == null) {
            System.out.println("Error: End date cannot be null.");
            return;  // Exit the method if end date is null
        }
        if (start_date != null && end_date.isBefore(start_date)) {
            System.out.println("Error: End date cannot be before start date.");
            return;  // Exit the method if end date is before the start date
        }
        this.end_date = end_date;  // Set the end date only if it's valid
    }


    /**
     * Checks whether the discount is currently active based on today's date.
     *
     * @return true if today's date is between start date and end date (inclusive); false otherwise.
     */
    public boolean isActive() {
        LocalDate today = LocalDate.now();
        return (start_date != null && end_date != null)
                && (!today.isBefore(start_date) && !today.isAfter(end_date));
    }

    /**
     * Returns a string representation of the discount, including its rate and date range.
     *
     * @return A string describing the discount.
     */
    @Override
    public String toString() {
        return "Discount{" +
                "discount_rate=" + discount_rate +
                "%, start_date=" + start_date +
                ", end_date=" + end_date +
                '}';
    }
}


