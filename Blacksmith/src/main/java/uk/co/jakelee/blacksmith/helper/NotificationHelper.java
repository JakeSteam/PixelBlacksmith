package uk.co.jakelee.blacksmith.helper;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
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
    private static final String NOTIFICATION_SOUND = "uk.co.jakelee.notification_sound";
    private static final String CHANNEL_SOUND = "blacksmith_alerts";
    private static final String CHANNEL_SILENT = "blacksmith_alerts_silent";
    private static final int REQUEST_POST_NOTIFICATIONS = 2;
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
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent notificationIntent = new Intent(context, NotificationHelper.class);
        notificationIntent.putExtra(NOTIFICATION_TYPE, notificationType);
        // The alarm can outlive the process, so this can't rely on the static field.
        notificationIntent.putExtra(NOTIFICATION_SOUND, useSounds);

        PendingIntent broadcast = PendingIntent.getBroadcast(context, 9000 + notificationType, notificationIntent, pendingIntentFlags());

        alarmManager.set(AlarmManager.RTC_WAKEUP, notificationTime, broadcast);
    }

    private static int pendingIntentFlags() {
        return PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
    }

    // Two channels, because the sound setting is per notification and a channel's sound can't be
    // changed after creation.
    private static void createChannels(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String name = context.getString(R.string.app_name);
        manager.createNotificationChannel(new NotificationChannel(CHANNEL_SOUND, name, NotificationManager.IMPORTANCE_DEFAULT));
        manager.createNotificationChannel(new NotificationChannel(CHANNEL_SILENT, name, NotificationManager.IMPORTANCE_LOW));
    }

    public static void confirmNotificationPermission(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return;
        }

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_POST_NOTIFICATIONS);
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

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, pendingIntentFlags());

        boolean playSound = intent.getBooleanExtra(NOTIFICATION_SOUND, false);
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

        createChannels(context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, playSound ? CHANNEL_SOUND : CHANNEL_SILENT);
        Notification notification = builder.setContentTitle(context.getString(R.string.app_name))
                .setContentText(notificationText)
                .setSmallIcon(R.drawable.notification)
                .setContentIntent(pendingIntent)
                .build();

        // Pre-Oreo has no channels, so the sound is set on the notification itself.
        if (playSound && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notification.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }
}