package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;

public class Shop_Stock extends SugarRecord {
    Long shopID;
    Long itemID;
    int state;
    int discovered;
    int stock;
    int defaultStock;

    public Shop_Stock() {

    }

    public Shop_Stock(Long shopID, Long itemID, int state, int discovered, int stock, int defaultStock) {
        this.shopID = shopID;
        this.itemID = itemID;
        this.state = state;
        this.discovered = discovered;
        this.stock = stock;
        this.defaultStock = defaultStock;
    }

    public Long getItemID() {
        return itemID;
    }

    public void setItemID(Long itemID) {
        this.itemID = itemID;
    }

    public Long getShopID() {
        return shopID;
    }

    public void setShopID(Long shopID) {
        this.shopID = shopID;
    }

    public int getDiscovered() {
        return discovered;
    }

    public void setDiscovered(int discovered) {
        this.discovered = discovered;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getDefaultStock() {
        return defaultStock;
    }

    public void setDefaultStock(int defaultStock) {
        this.defaultStock = defaultStock;
    }
}
