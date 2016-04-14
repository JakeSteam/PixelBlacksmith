package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;

import com.orm.query.Select;

import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.controls.TextViewPixel;
import uk.co.jakelee.blacksmith.helper.DateHelper;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.model.Message;

public class MessagesActivity extends Activity {
    public static DisplayHelper dh;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        dh = DisplayHelper.getInstance(getApplicationContext());

        populateMessages();
    }

    private void populateMessages() {
        TableLayout messageScroller = (TableLayout) findViewById(R.id.messagesHolder);

        List<Message> messages = Select.from(Message.class).orderBy("added DESC").list();
        for (Message message : messages) {
            LinearLayout messageRow = new LinearLayout(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 10);

            String timeText = DateHelper.displayTime(message.getAdded(), DateHelper.time);
            TextViewPixel timeTextView = dh.createTextView(timeText + " ", 22, Color.DKGRAY);
            TextViewPixel messageTextView = dh.createTextView(message.getMessage(), 18);

            messageRow.addView(timeTextView);
            messageRow.addView(messageTextView, params);
            messageScroller.addView(messageRow);
        }
    }

    public void openHelp(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.INTENT_ID, HelpActivity.TOPICS.Messages);
        startActivity(intent);
    }

    public void closePopup(View view) {
        finish();
    }
}
