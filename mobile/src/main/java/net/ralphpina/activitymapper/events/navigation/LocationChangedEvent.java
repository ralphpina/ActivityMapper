package net.ralphpina.activitymapper.events.navigation;

import net.ralphpina.activitymapper.location.Record;

import java.util.List;

public class LocationChangedEvent {

    private final List<Record> mRecords;

    public LocationChangedEvent(List<Record> records) {
        mRecords = records;
    }

    public List<Record> getRecords() {
        return mRecords;
    }
}
