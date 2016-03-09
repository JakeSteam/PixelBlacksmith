package uk.co.jakelee.blacksmith.helper;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.orm.query.Condition;
import com.orm.query.Select;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.main.MainActivity;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.Upgrade;

public class NotificationHelper extends BroadcastReceiver{
    private static boolean useSounds = false;
    private static String NOTIFICATION_TYPE = "uk.co.jakelee.notification_type";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent notificationIntent = new Intent(context, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        int notificationType = intent.getExtras().getInt(NOTIFICATION_TYPE);
        String notificationText = "";
        if (notificationType == Constants.NOTIFICATION_RESTOCK) {
            notificationText = "All traders have been restocked!";
        } else if (notificationType == Constants.NOTIFICATION_VISITOR) {
            notificationText = "A new visitor has arrived!";
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Notification notification = builder.setContentTitle("Blacksmith")
                .setContentText(notificationText)
                .setSmallIcon(R.drawable.item35)
                .setContentIntent(pendingIntent)
                .build();

        if (useSounds) {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            notification.sound = alarmSound;
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }

    public static void addRestockNotification(Context context, boolean useSoundsSetting) {
        useSounds = useSoundsSetting;

        long restockTime = Select.from(Player_Info.class).where(Condition.prop("name").eq("DateRestocked")).first().getLongValue() + DateHelper.hoursToMilliseconds(Upgrade.getValue("Shop Restock Time"));
        NotificationHelper.addNotification(context, restockTime, Constants.NOTIFICATION_RESTOCK);
    }

    public static void addVisitorNotification(Context context, boolean useSoundsSetting) {
        useSounds = useSoundsSetting;

        long restockTime = Select.from(Player_Info.class).where(Condition.prop("name").eq("DateVisitorSpawned")).first().getLongValue() + DateHelper.minutesToMilliseconds(Upgrade.getValue("Visitor Spawn Time"));
        NotificationHelper.addNotification(context, restockTime, Constants.NOTIFICATION_VISITOR);
    }

    private static void addNotification(Context context, long notificationTime, int notificationType) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
        notificationIntent.addCategory("android.intent.category.DEFAULT");
        notificationIntent.putExtra(NOTIFICATION_TYPE, notificationType);

        PendingIntent broadcast = PendingIntent.getBroadcast(context, 1234, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.set(AlarmManager.RTC_WAKEUP, notificationTime, broadcast);
    }

    public static void clearNotifications(final Context context) {
            NotificationManager nMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            nMgr.cancelAll();
    }
}