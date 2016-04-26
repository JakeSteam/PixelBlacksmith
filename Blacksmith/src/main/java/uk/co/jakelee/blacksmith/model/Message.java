package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;
import com.orm.query.Select;

import uk.co.jakelee.blacksmith.helper.Constants;

public class Message extends SugarRecord {
    private long added;
    private String message;

    public Message() {
    }

    public Message(long added, String message) {
        this.added = added;
        this.message = message;
    }

    public static void add(String text) {
        Message message = new Message(System.currentTimeMillis(), text);
        message.save();

        if (Message.count(Message.class) >= Constants.MESSAGE_LOG_LIMIT) {
            Message oldestMessage = Select.from(Message.class).orderBy("added ASC").first();
            oldestMessage.delete();
        }
    }

    public long getAdded() {
        return added;
    }

    public void setAdded(long added) {
        this.added = added;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
