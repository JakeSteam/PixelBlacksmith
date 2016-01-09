package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;

import java.util.List;

public class Visitor_Demand extends SugarRecord{
    Long visitorID;
    Long criteriaType;
    Long criteriaValue;
    int quantityProvided;
    int quantity;
    boolean required;

    public Visitor_Demand() {
    }

    public Visitor_Demand(Long visitorID, Long criteriaType, Long criteriaValue, int quantityProvided, int quantity, boolean required) {
        this.visitorID = visitorID;
        this.criteriaType = criteriaType;
        this.criteriaValue = criteriaValue;
        this.quantityProvided = quantityProvided;
        this.quantity = quantity;
        this.required = required;
    }

    public Long getVisitorID() {
        return visitorID;
    }

    public void setVisitorID(Long visitorID) {
        this.visitorID = visitorID;
    }

    public Long getCriteriaType() {
        return criteriaType;
    }

    public void setCriteriaType(Long criteriaType) {
        this.criteriaType = criteriaType;
    }

    public Long getCriteriaValue() {
        return criteriaValue;
    }

    public void setCriteriaValue(Long criteriaValue) {
        this.criteriaValue = criteriaValue;
    }

    public int getQuantityProvided() {
        return quantityProvided;
    }

    public void setQuantityProvided(int quantityProvided) {
        this.quantityProvided = quantityProvided;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public static String getCriteriaName(Visitor_Demand demand) {
        String demandText = "Unknown";
        if (demand.getCriteriaType() == 1L) {
            State demandState = State.findById(State.class, demand.getCriteriaValue());
            demandText = demandState.getName();
        } else if (demand.getCriteriaType() == 2L) {
            Tier demandTier = Tier.findById(Tier.class, demand.getCriteriaValue());
            demandText = demandTier.getName();
        } else if (demand.getCriteriaType() == 3L) {
            Type demandType = Type.findById(Type.class, demand.getCriteriaValue());
            demandText = demandType.getName();
        }
        return demandText;
    }

    public boolean isDemandFulfilled() {
        return this.getQuantityProvided() >= this.getQuantity();
    }

    public List<Inventory> getMatchingInventory() {
        String searchText = "SELECT * FROM Inventory INNER JOIN Item ON Inventory.item = Item.id WHERE quantity > 0 ";

        if (this.getCriteriaType() == 1L) {
            searchText += "AND state = " + this.getCriteriaValue();
        } else if (this.getCriteriaType() == 2L) {
            searchText += "AND tier = " + this.getCriteriaValue();
        } else if (this.getCriteriaType() == 3L) {
            searchText += "AND type = " + this.getCriteriaValue();
        }

        return Inventory.findWithQuery(Inventory.class, searchText);
    }
}
