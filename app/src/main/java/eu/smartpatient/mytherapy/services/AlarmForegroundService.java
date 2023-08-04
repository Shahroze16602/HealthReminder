package eu.smartpatient.mytherapy.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import eu.smartpatient.mytherapy.R;
import eu.smartpatient.mytherapy.RemindersActivity;
import eu.smartpatient.mytherapy.database.DatabaseHelper;
import eu.smartpatient.mytherapy.models.MedicineModel;
import eu.smartpatient.mytherapy.receivers.WakeLockManager;

public class AlarmForegroundService extends Service {

    private static final int NOTIFICATION_ID = 99999;
    private static final String CHANNEL_ID = "ManagingReminder";
    Notification notification;
    DatabaseHelper databaseHelper = new DatabaseHelper(this);
    ArrayList<MedicineModel> arrayList;
    AlarmManager alarmManager;

    @Override
    public void onCreate() {
        super.onCreate();
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Log.d("TAG", "onCreate: Method called");
        Intent notificationIntent = new Intent(this, RemindersActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Management",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Health Reminder")
                .setContentText("Your reminders are being managed.")
                .setSmallIcon(R.drawable.clock_icon)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(NOTIFICATION_ID, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        stopAlarms();
        startAlarms();
        Log.d("TAG", "onStartCommand: Foreground service is running...");
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startAlarms() {
        arrayList = databaseHelper.getAllMedicines();
        for (MedicineModel reminder : arrayList) {
            WakeLockManager.getInstance().acquireWakeLock(this);
            if (alarmManager != null) {
                Log.d("TAG", "startAlarms: Alarm set "+reminder.getId());
                Intent intent = new Intent(this, NotificationService.class);
                intent.putExtra("id", reminder.getId());
                intent.putExtra("name", reminder.getName());
                intent.putExtra("unit", reminder.getUnit());
                intent.putExtra("time_hours", String.valueOf(reminder.getTimeHours()));
                intent.putExtra("time_minutes", String.valueOf(reminder.getTimeMinutes()));
                intent.putExtra("dose", String.valueOf(reminder.getDose()));
                intent.putExtra("isSkipped", reminder.getIsSkipped());
                intent.putExtra("title", reminder.getName().substring(0, 1).toUpperCase(Locale.ROOT) + reminder.getName().substring(1).toLowerCase(Locale.ROOT));
                intent.putExtra("description", reminder.getDose() + " " + reminder.getUnit());

                PendingIntent pendingIntent = PendingIntent.getService(this, Integer.parseInt(reminder.getId()), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, reminder.getTimeHours());
                calendar.set(Calendar.MINUTE, reminder.getTimeMinutes());
                calendar.set(Calendar.SECOND, 0);

                if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                    calendar.add(Calendar.DAY_OF_YEAR, 1);
                }

                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            } else {
                Toast.makeText(this, "No Alarm Manager", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void stopAlarms() {
        arrayList = databaseHelper.getAllMedicines();
        for (MedicineModel reminder : arrayList) {
            if (alarmManager != null) {
                Intent intent = new Intent(this, NotificationService.class);
                PendingIntent pendingIntent = PendingIntent.getService(this, Integer.parseInt(reminder.getId()), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                alarmManager.cancel(pendingIntent);
                WakeLockManager.getInstance().releaseWakeLock();
            }
        }
    }
}

