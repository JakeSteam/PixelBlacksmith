package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;

public class Worker_Resource extends SugarRecord {
    private int toolID;
    private int resourceID;
    private int resourceState;
    private int resourceQuantity;

    public Worker_Resource() {
    }

    public Worker_Resource(int toolID, int resourceID, int resourceState, int resourceQuantity) {
        this.toolID = toolID;
        this.resourceID = resourceID;
        this.resourceState = resourceState;
        this.resourceQuantity = resourceQuantity;
    }

    public int getToolID() {
        return toolID;
    }

    public void setToolID(int toolID) {
        this.toolID = toolID;
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
