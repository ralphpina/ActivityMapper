package net.ralphpina.activitymapper.location;

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.location.ActivityRecognitionResult;

import net.ralphpina.activitymapper.AMApplication;

public class DetectedActivitiesIntentService extends IntentService {

    public DetectedActivitiesIntentService() {
        super("DetectedActivitiesIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
        AMApplication.get().locationManager().onActivitiesDetected(result);
    }
}
