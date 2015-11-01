package net.ralphpina.activitymapper.location;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.parse.ParseUser;

import net.ralphpina.activitymapper.AMApplication;
import net.ralphpina.activitymapper.events.LocationChangedEvent;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

import static com.google.android.gms.location.ActivityRecognition.ActivityRecognitionApi;
import static com.google.android.gms.location.LocationServices.FusedLocationApi;

public class LocationManager {

    private static final String TAG = "LocationManager";

    // "The desired time between activity detections. Larger values result in fewer activity
    // detections while improving battery life. A value of 0 results in activity detections at the
    // fastest possible rate. Getting frequent updates negatively impact battery life and a real
    // app may prefer to request less frequent updates."
    // - Gonna set it to the highest since we only track when the app is in the foreground, so hopefully
    // - battery life won't take a huge hit R.Pina 20150911
    public static final long DETECTION_INTERVAL_IN_MILLISECONDS = 0;

    public static final int MESSAGE_NO_LONGER_DRIVING_BUFFER = 0x01;

    public static final int FIFTEEN_SECONDS_MILLIS = 15 * 1000;
    public static final int THIRTY_SECONDS_MILLIS  = 30 * 1000;

    private static final long LOCATION_REQUEST_INTERVAL = 1000 * 60; // ms

    private static final LocationRequest REQUEST = LocationRequest.create()
                                                                  .setInterval(
                                                                          LOCATION_REQUEST_INTERVAL)
                                                                  .setFastestInterval(2000)
                                                                  .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

    private final GoogleApiClient                 _googleApiClient;
    private final FusedLocationListener           _fusedLocationListener;
    private final PendingIntent                   _activityPendingIntent;
    private final List<Location>                  _locationCache;
    private final List<ActivityRecognitionResult> _detectedActivitiesCache;
    private       boolean                         _apisConnected;

    public LocationManager(Context context) {
        _locationCache = new ArrayList<>();
        _detectedActivitiesCache = new ArrayList<>();

        _googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(new GoogleApiConnectionCallbacks())
                .addOnConnectionFailedListener(new GoogleApiConnectionFailedListener())
                .build();

        _fusedLocationListener = new FusedLocationListener();
        Intent intent = new Intent(context, DetectedActivitiesIntentService.class);

        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // requestActivityUpdates() and removeActivityUpdates().
        _activityPendingIntent = PendingIntent.getService(context,
                                                          0,
                                                          intent,
                                                          PendingIntent.FLAG_UPDATE_CURRENT);
        connect();
    }

    // ===== CONNECTIONS ===========================================================================

    // ----- CONNECT -------------------------------------------------------------------------------

    public void connect() {
        if (ParseUser.getCurrentUser() == null) {
            return;
        }
        if (isTestEnvironment()) {
            mIsTestClientConnected = true;
            connectApis();
        } else {
            if (!_googleApiClient.isConnected()) {
                _googleApiClient.connect();
            } else {
                connectApis();
            }
        }
    }

    private void connectApis() {
        if (!_apisConnected) {
            _apisConnected = true;
            connectToFusedLocation();
            connectToActivityRecognition();
        }
    }

    // ----- DISCONNECT ----------------------------------------------------------------------------

    public void disconnect() {
        if (_googleApiClient.isConnected() || mIsTestClientConnected) {
            _apisConnected = false;
            disconnectApis();
        }
    }

    private void disconnectClient() {
        if (isTestEnvironment()) {
            mIsTestClientConnected = false;
        } else {
            _googleApiClient.disconnect();
        }
    }

    private void disconnectApis() {
        if (isTestEnvironment()) {
            mIsTestLocationConnected = false;
        } else {
            disconnectToFusedLocation();
        }
        if (isTestEnvironment()) {
            mIsTestActivityConnected = false;
        } else {
            disconnectToActivityRecognition();
        }
    }

    // ----- LOCATION ------------------------------------------------------------------------------

    private void connectToFusedLocation() {
        if (isTestEnvironment()) {
            mIsTestLocationConnected = true;
        } else {
            FusedLocationApi.requestLocationUpdates(_googleApiClient,
                                                    REQUEST,
                                                    _fusedLocationListener);
        }
    }

    private void disconnectToFusedLocation() {
        if (isTestEnvironment()) {
            mIsTestLocationConnected = false;
        } else {
            FusedLocationApi.removeLocationUpdates(_googleApiClient,
                                                   _fusedLocationListener);
        }
    }

    // ----- ACTIVITY ------------------------------------------------------------------------------

    private void connectToActivityRecognition() {
        if (isTestEnvironment()) {
            mIsTestActivityConnected = true;
        } else {
            ActivityRecognitionApi.requestActivityUpdates(_googleApiClient,
                                                          DETECTION_INTERVAL_IN_MILLISECONDS,
                                                          _activityPendingIntent);
        }
    }

    private void disconnectToActivityRecognition() {
        if (isTestEnvironment()) {
            mIsTestActivityConnected = false;
        } else {
            ActivityRecognitionApi.removeActivityUpdates(_googleApiClient,
                                                         _activityPendingIntent);
        }
    }

    // ----- LOCATION ------------------------------------------------------------------------------

    public void onLocationEvent(Location location) {
        Log.e(TAG, "=== onLocationEvent() === location = " + location);
    }

    // ----- ACTIVITY ------------------------------------------------------------------------------

    public void onActivitiesDetected(ActivityRecognitionResult recognitionResult) {
        Log.e(TAG, "=== onActivitiesDetected() === recognitionResult = " + recognitionResult);
    }

    // ===== CALLBACKS =============================================================================

    private class GoogleApiConnectionCallbacks implements GoogleApiClient.ConnectionCallbacks {

        @Override
        public void onConnected(Bundle bundle) {
            connectApis();
        }

        @Override
        public void onConnectionSuspended(int i) {
            _googleApiClient.connect();
        }
    }

    private class GoogleApiConnectionFailedListener implements GoogleApiClient.OnConnectionFailedListener {

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            try {
                // TODO noop?
            } catch (NullPointerException e) {
                // noop - this will happen if the Google API client is initialized before the
                // Account's constructor returns. Just ignore it.
            }
        }
    }

    /**
     * Only used in Map to send location message
     */
    private class FusedLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            try {
                onLocationEvent(location);
                EventBus.getDefault()
                        .post(new LocationChangedEvent(location));
            } catch (NullPointerException e) {
                // noop - this will happen if the Google API client is initialized before the
                // Account's constructor returns. Just ignore it.
            }
        }
    }

    // ===== TESTING ===============================================================================

    private boolean mIsTestClientConnected;
    private boolean mIsTestLocationConnected;
    private boolean mIsTestActivityConnected;

    private boolean isTestEnvironment() {
        return AMApplication.get().isTestEnvironment();
    }

    @VisibleForTesting
    public boolean isTestClientConnected() {
        return mIsTestClientConnected;
    }

    @VisibleForTesting
    public boolean isTestLocationConnected() {
        return mIsTestLocationConnected;
    }

    @VisibleForTesting
    public boolean isTestActivityConnected() {
        return mIsTestActivityConnected;
    }
}
