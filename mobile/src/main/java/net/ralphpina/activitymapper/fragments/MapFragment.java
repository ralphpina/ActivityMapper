package net.ralphpina.activitymapper.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.views.MapView;

import net.ralphpina.activitymapper.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MapFragment extends Fragment {

    public final static String TAG = "MapFragment";

    @Bind(R.id.map_view)
    MapView _mapView;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, root);

        _mapView.setStyleUrl(Style.MAPBOX_STREETS);
        _mapView.setZoomLevel(12);
        _mapView.setMyLocationEnabled(true);
        _mapView.onCreate(savedInstanceState);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        _mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        _mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        _mapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        _mapView.onResume();
        if (_mapView.getMyLocation() != null) {
            final double lat = _mapView.getMyLocation()
                                       .getLatitude();
            final double lng = _mapView.getMyLocation()
                                       .getLongitude();
            LatLng latLng = new LatLng(lat, lng);
            _mapView.setCenterCoordinate(latLng);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        _mapView.onSaveInstanceState(outState);
    }
}
