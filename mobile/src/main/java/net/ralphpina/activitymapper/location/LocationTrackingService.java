package net.ralphpina.activitymapper.location;

import android.app.AlarmManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import net.ralphpina.activitymapper.AMApplication;
import net.ralphpina.activitymapper.Account;

public class LocationTrackingService extends Service {

    private LocationManager mLocationManager;
    private AlarmManager    mAlarmManager;

    private static LocationTrackingService _thisService;

    public static LocationTrackingService get() {
        return _thisService;
    }

    public static boolean isRunning() {
        return _thisService != null;
    }

    // ===== CONTROL METHODS =======================================================================

    public static synchronized void start() {
        if (!isRunning()) {
            AMApplication.get()
                         .startService(new Intent(AMApplication.get(),
                                                  LocationTrackingService.class));
        }
    }

    public static synchronized void stop() {
        if (isRunning()) {
            _thisService.stopSelf();
        }
    }

    // ===== SERVICE ===============================================================================

    public LocationTrackingService() {
    }

    @Override
    public void onCreate() {
        _thisService = this;
        mAlarmManager = (AlarmManager) getSystemService(Service.ALARM_SERVICE);
        if (Account.get().isUserVerified()) {
            mLocationManager = new LocationManager(this);
            mLocationManager.connect();
        } else {
            stopSelf();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mLocationManager != null) {
            mLocationManager.disconnect();
        }
        _thisService = null;
    }

    // ===== LOCATION MANAGER ======================================================================

    public LocationManager locationManager() {
        return mLocationManager;
    }
}
