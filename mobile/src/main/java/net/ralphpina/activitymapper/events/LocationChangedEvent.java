package net.ralphpina.activitymapper.events;

import android.location.Location;

public class LocationChangedEvent {

    private final Location _location;

    public LocationChangedEvent(Location location) {
        _location = location;
    }

    public Location getLocation() {
        return _location;
    }
}
