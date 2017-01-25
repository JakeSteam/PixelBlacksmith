package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

public class Visitor_Log extends SugarRecord {
    private long visitorId;
    private long itemId;
    private long itemState;

    public Visitor_Log() {
    }

    public Visitor_Log(long visitorId, long itemId, long itemState) {
        this.visitorId = visitorId;
        this.itemId = itemId;
        this.itemState = itemState;
    }

    public long getVisitorId() {
        return visitorId;
    }

    public void setVisitorId(long visitorId) {
        this.visitorId = visitorId;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public long getItemState() {
        return itemState;
    }

    public void setItemState(long itemState) {
        this.itemState = itemState;
    }

    public static boolean hasBeenTried(long visitorId, long itemId, long itemState) {
        return Select.from(Visitor_Log.class).where(
                Condition.prop("visitor_id").eq(visitorId),
                Condition.prop("item_id").eq(itemId),
                Condition.prop("item_state").eq(itemState)).count() > 0;
    }
}
