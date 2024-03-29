package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;

public class Hero_Resource extends SugarRecord {
    private int adventureId;
    private int resourceID;
    private int resourceState;
    private int resourceQuantity;

    public Hero_Resource() {
    }

    public Hero_Resource(int adventureId, int resourceID, int resourceState, int resourceQuantity) {
        this.adventureId = adventureId;
        this.resourceID = resourceID;
        this.resourceState = resourceState;
        this.resourceQuantity = resourceQuantity;
    }

    public int getAdventureId() {
        return adventureId;
    }

    public void setAdventureId(int adventureId) {
        this.adventureId = adventureId;
    }

    public int getResourceID() {
        return resourceID;
    }

    public void setResourceID(int resourceID) {
        this.resourceID = resourceID;
    }

    public int getResourceState() {
        return resourceState;
    }

    public void setResourceState(int resourceState) {
        this.resourceState = resourceState;
    }

    public int getResourceQuantity() {
        return resourceQuantity;
    }

    public void setResourceQuantity(int resourceQuantity) {
        this.resourceQuantity = resourceQuantity;
    }
}
