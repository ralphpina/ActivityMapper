package net.ralphpina.activitymapper.location;

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.location.ActivityRecognitionResult;

public class DetectedActivitiesIntentService extends IntentService {

    public DetectedActivitiesIntentService() {
        super("DetectedActivitiesIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
        if (LocationTrackingService.isRunning()) {
            LocationTrackingService.get().locationManager().onActivitiesDetected(result);
        }
    }
}
