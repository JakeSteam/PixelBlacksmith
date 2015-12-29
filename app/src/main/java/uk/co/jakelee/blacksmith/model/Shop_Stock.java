package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;

public class Shop_Stock extends SugarRecord {
    Long shopID;
    Long itemID;
    int price;
    int discovered;
    int stock;

    public Shop_Stock() {

    }

    public Shop_Stock(Long shopID, Long itemID, int price, int discovered, int stock) {
        this.shopID = shopID;
        this.itemID = itemID;
        this.price = price;
        this.discovered = discovered;
        this.stock = stock;
        this.save();
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getDiscovered() {
        return discovered;
    }

    public void setDiscovered(int discovered) {
        this.discovered = discovered;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
