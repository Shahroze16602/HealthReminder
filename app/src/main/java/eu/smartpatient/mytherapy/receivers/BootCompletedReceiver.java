package eu.smartpatient.mytherapy.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

import eu.smartpatient.mytherapy.services.AlarmForegroundService;

public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, AlarmForegroundService.class);
        ContextCompat.startForegroundService(context, serviceIntent);
    }
}
