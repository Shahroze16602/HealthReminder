package eu.smartpatient.mytherapy.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import eu.smartpatient.mytherapy.R;
import eu.smartpatient.mytherapy.RemindersActivity;
import eu.smartpatient.mytherapy.database.DatabaseHelper;
import eu.smartpatient.mytherapy.models.MedicineModel;
import eu.smartpatient.mytherapy.receivers.WakeLockManager;

public class NotificationService extends Service {
    private static final String CHANNEL_ID = "Reminder";
//    private static final int NOTIFICATION_ID = 101;
    MedicineModel medicineModel;

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Reminders",
                    NotificationManager.IMPORTANCE_HIGH
            );

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        String id = intent.getStringExtra("id");
        if (databaseHelper.getIsSkipped(id).equals("false")) {
            String reminderTitle = "It's time to take your medicine";
            String reminderDescription = intent.getStringExtra("title") + " " + intent.getStringExtra("description");
            Intent notificationIntent = new Intent(this, RemindersActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.clock_icon)
                    .setContentTitle(reminderTitle)
                    .setContentText(reminderDescription)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.notify(Integer.parseInt(id), builder.build());
            }
        } else {
            databaseHelper.skipMedicine(id, "false");
        }
        WakeLockManager.getInstance().releaseWakeLock();
        String name = intent.getStringExtra("name");
        String unit = intent.getStringExtra("unit");
        int hours = Integer.parseInt(intent.getStringExtra("time_hours"));
        int minutes = Integer.parseInt(intent.getStringExtra("time_minutes"));
        int noOfUnits = Integer.parseInt(intent.getStringExtra("dose"));
        String isSkipped = intent.getStringExtra("isSkipped");
        medicineModel = new MedicineModel(name,unit,hours,minutes,noOfUnits,isSkipped);
        medicineModel.setId(id);
        Intent serviceIntent = new Intent(this, AlarmForegroundService.class);
        ContextCompat.startForegroundService(this, serviceIntent);

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
