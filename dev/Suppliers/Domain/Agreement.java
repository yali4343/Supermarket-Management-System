package Suppliers.Domain;



/**
 * Represents a commercial agreement between a supplier and the supermarket.
 * Each agreement includes a supplier ID, delivery days, a self-pickup flag, and
 * a list of products provided by the supplier under this agreement.
 */
public class Agreement {
    private final int agreement_ID; // Unique ID of the agreement
    private final int supplier_ID; // Unique ID of the supplier
    private String[] deliveryDays; // Days on which the supplier can deliver
    private boolean selfPickup; // Whether delivery is self-handled by the supermarket


    /**
     * Constructs an Agreement object with specified attributes.
     *
     * @param agreement_ID  the unique ID of the agreement
     * @param supplier_ID   the unique ID of the supplier
     * @param deliveryDays  array of fixed delivery days (e.g., Mon, Wed)
     * @param selfPickup    true if delivery is self-handled by the supermarket
     */
    public Agreement(int agreement_ID, int supplier_ID, String[] deliveryDays, boolean selfPickup) {
        this.agreement_ID = agreement_ID;
        this.supplier_ID = supplier_ID;
        this.deliveryDays = deliveryDays;
        this.selfPickup = selfPickup;
    }


    /**
     * @return the supplier ID associated with this agreement
     */
    public int getSupplier_ID() {
        return supplier_ID;
    }

    /**
     * @return the agreement ID
     */
    public int getAgreementID() {
        return agreement_ID;
    }

    /**
     * Updates the delivery days for this agreement.
     *
     * @param newDeliveryDays new array of delivery days
     */
    public void updateDeliveryDays(String[] newDeliveryDays) {
        this.deliveryDays = newDeliveryDays;
    }

    /**
     * Toggles the self-pickup status for the agreement.
     *
     * @return the updated self-pickup status
     */
    public boolean updateSelfDeliveryOption() {
        this.selfPickup = !this.selfPickup;
        return this.selfPickup;
    }



    public void setDeliveryDays(String[] deliveryDays) {
        this.deliveryDays = deliveryDays;
    }

    public void setSelfPickup(boolean selfPickup) {
        this.selfPickup = selfPickup;
    }

    public int getAgreement_ID() {
        return agreement_ID;
    }

    public String[] getDeliveryDays() {
        return deliveryDays;
    }

    public boolean isSelfPickup() {
        return selfPickup;
    }
}

