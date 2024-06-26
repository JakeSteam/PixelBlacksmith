package uk.co.jakelee.blacksmith.helper;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;

import androidx.core.app.NotificationCompat;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.main.MainActivity;
import uk.co.jakelee.blacksmith.model.Assistant;
import uk.co.jakelee.blacksmith.model.Hero;
import uk.co.jakelee.blacksmith.model.Pending_Inventory;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.Trader_Stock;
import uk.co.jakelee.blacksmith.model.Upgrade;
import uk.co.jakelee.blacksmith.model.Worker;

public class NotificationHelper extends BroadcastReceiver {
    private static final String NOTIFICATION_TYPE = "uk.co.jakelee.notification_type";
    private static boolean useSounds = false;

    public static void addRestockNotification(Context context, boolean useSoundsSetting) {
        useSounds = useSoundsSetting;

        long restockTime = System.currentTimeMillis() + Trader_Stock.getMillisecondsUntilRestock();
        NotificationHelper.addNotification(context, restockTime, Constants.NOTIFICATION_RESTOCK);
    }

    public static void addVisitorNotification(Context context, boolean useSoundsSetting) {
        useSounds = useSoundsSetting;

        long restockTime = Select.from(Player_Info.class).where(Condition.prop("name").eq("DateVisitorSpawned")).first().getLongValue() + DateHelper.minutesToMilliseconds(Upgrade.getValue("Visitor Spawn Time"));
        NotificationHelper.addNotification(context, restockTime, Constants.NOTIFICATION_VISITOR);
    }

    public static void addHelperNotification(Context context, boolean useSoundsSetting) {
        useSounds = useSoundsSetting;

        List<Worker> workers = Worker.listAll(Worker.class);
        for (Worker worker : workers) {
            if (worker.isPurchased() && !WorkerHelper.isReady(worker)) {
                long restockTime = System.currentTimeMillis() + WorkerHelper.getTimeRemaining(worker.getTimeStarted());
                NotificationHelper.addNotification(context, restockTime, Constants.NOTIFICATION_WORKER);
            }
        }
    }

    public static void addAssistantNotification(Context context, boolean useSoundsSetting) {
        useSounds = useSoundsSetting;

        Assistant assistant = Assistant.get(Select.from(Player_Info.class).where(Condition.prop("name").eq("ActiveAssistant")).first().getIntValue());
        long lastClaimTime = Select.from(Player_Info.class).where(Condition.prop("name").eq("LastAssistantClaim")).first().getLongValue();

        if (assistant != null && assistant.getObtained() > 0) {
            long nextClaimTime = lastClaimTime + assistant.getRewardFrequency();
            if (nextClaimTime > System.currentTimeMillis()) {
                NotificationHelper.addNotification(context, nextClaimTime, Constants.NOTIFICATION_ASSISTANT);
            }
        }
    }

    public static void addHeroNotification(Context context, boolean useSoundsSetting) {
        useSounds = useSoundsSetting;

        List<Hero> heroes = Hero.listAll(Hero.class);
        for (Hero hero : heroes) {
            if (hero.isPurchased() && !WorkerHelper.isReady(hero) && hero.getTimeStarted() > 0) {
                long restockTime = System.currentTimeMillis() + WorkerHelper.getTimeRemaining(hero.getTimeStarted());
                NotificationHelper.addNotification(context, restockTime, Constants.NOTIFICATION_WORKER);
            }
        }
    }

    public static void addBonusNotification(Context context, boolean useSoundsSetting) {
        useSounds = useSoundsSetting;

        long bonusTime = System.currentTimeMillis() + Player_Info.timeUntilBonusReady();
        NotificationHelper.addNotification(context, bonusTime, Constants.NOTIFICATION_BONUS);
    }

    public static void addFinishedNotification(Context context, boolean useSoundsSetting) {
        useSounds = useSoundsSetting;

        Pending_Inventory finalItem = Select.from(Pending_Inventory.class).orderBy("(time_created + craft_time) DESC").first();
        if (finalItem != null) {
            NotificationHelper.addNotification(context, finalItem.getTimeCreated() + finalItem.getCraftTime(), Constants.NOTIFICATION_FINISHED);
        }
    }

    private static void addNotification(Context context, long notificationTime, int notificationType) {
        try {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
            notificationIntent.addCategory("android.intent.category.DEFAULT");
            notificationIntent.putExtra(NOTIFICATION_TYPE, notificationType);

            PendingIntent broadcast = PendingIntent.getBroadcast(context, 9000 + notificationType, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            alarmManager.set(AlarmManager.RTC_WAKEUP, notificationTime, broadcast);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public static void clearNotifications(final Context context) {
        NotificationManager nMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
    }

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
            notificationText = context.getString(R.string.restockTextNoTax);
        } else if (notificationType == Constants.NOTIFICATION_VISITOR) {
            notificationText = context.getString(R.string.notificationVisitor);
        } else if (notificationType == Constants.NOTIFICATION_WORKER) {
            notificationText = context.getString(R.string.notificationWorker);
        } else if (notificationType == Constants.NOTIFICATION_BONUS) {
            notificationText = context.getString(R.string.notificationBonus);
        } else if (notificationType == Constants.NOTIFICATION_FINISHED) {
            notificationText = context.getString(R.string.notificationFinished);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Notification notification = builder.setContentTitle(context.getString(R.string.app_name))
                .setContentText(notificationText)
                .setSmallIcon(R.drawable.notification)
                .setContentIntent(pendingIntent)
                .build();

        if (useSounds) {
            notification.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }
}