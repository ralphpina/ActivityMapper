package net.ralphpina.activitymapper.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.views.MapView;
import com.parse.ParseGeoPoint;

import net.ralphpina.activitymapper.Account;
import net.ralphpina.activitymapper.R;
import net.ralphpina.activitymapper.events.EventBusMethod;
import net.ralphpina.activitymapper.events.navigation.LocationChangedEvent;
import net.ralphpina.activitymapper.location.Record;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

import static android.graphics.Color.BLUE;
import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;
import static android.graphics.Color.YELLOW;
import static com.google.android.gms.location.DetectedActivity.IN_VEHICLE;
import static com.google.android.gms.location.DetectedActivity.ON_BICYCLE;
import static com.google.android.gms.location.DetectedActivity.RUNNING;
import static com.google.android.gms.location.DetectedActivity.WALKING;

public class MapFragment extends Fragment {

    public final static String TAG = "MapFragment";

    @Bind(R.id.map_view)
    MapView mMapView;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, root);

        mMapView.setStyleUrl(Style.MAPBOX_STREETS);
        mMapView.setZoomLevel(12);
        mMapView.setMyLocationEnabled(true);
        mMapView.onCreate(savedInstanceState);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        mMapView.onResume();
        if (mMapView.getMyLocation() != null) {
            final double lat = mMapView.getMyLocation()
                                       .getLatitude();
            final double lng = mMapView.getMyLocation()
                                       .getLongitude();
            LatLng latLng = new LatLng(lat, lng);
            mMapView.setCenterCoordinate(latLng);
        }
        Account.get()
               .broadcastRecordsForLast24Hours();
    }


    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        mMapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mMapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    // ===== EVENTS ================================================================================

    @EventBusMethod
    public void onEventMainThread(LocationChangedEvent event) {
        int lastType = -1;
        PolylineOptions polylineOptions = null;
        ParseGeoPoint geoPoint;
        for (Record record : event.getRecords()) {
            if (record.getActivityType() != lastType) {
                if (polylineOptions != null) {
                    setPolylineColor(polylineOptions, lastType);
                    mMapView.addPolyline(polylineOptions);
                }
                lastType = record.getActivityType();
                polylineOptions = new PolylineOptions();
            }
            assert polylineOptions != null;
            geoPoint = record.getLocation();
            polylineOptions.add(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()));
        }
        if (polylineOptions != null) {
            setPolylineColor(polylineOptions, lastType);
            mMapView.addPolyline(polylineOptions);
        }
    }

    private void setPolylineColor(PolylineOptions options, int lastType) {
        switch (lastType) {
            case IN_VEHICLE:
                options.color(RED);
                break;
            case ON_BICYCLE:
                options.color(BLUE);
                break;
            case WALKING:
                options.color(YELLOW);
                break;
            case RUNNING:
                options.color(GREEN);
                break;
        }
    }
}
