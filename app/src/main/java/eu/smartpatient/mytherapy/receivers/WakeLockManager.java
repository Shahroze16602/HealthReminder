package eu.smartpatient.mytherapy.receivers;

import android.content.Context;
import android.os.PowerManager;

public class WakeLockManager {
    private static WakeLockManager instance;
    private PowerManager.WakeLock wakeLock;

    private WakeLockManager() {
    }

    public static synchronized WakeLockManager getInstance() {
        if (instance == null) {
            instance = new WakeLockManager();
        }
        return instance;
    }

    public void acquireWakeLock(Context context) {
        if (wakeLock == null) {
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (powerManager != null) {
                wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp::MyWakeLockTag");
                wakeLock.acquire();
            }
        }
    }

    public void releaseWakeLock() {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
            wakeLock = null;
        }
    }
}

