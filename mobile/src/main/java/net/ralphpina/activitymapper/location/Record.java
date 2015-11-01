package net.ralphpina.activitymapper.location;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("Record")
public class Record extends ParseObject {

    // ==== USER ===================================================================================

    public ParseUser getUser() {
        return getParseUser("user");
    }

    public void setUser(ParseUser value) {
        put("user", value);
    }

    // ==== LOCATION ===============================================================================

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint("location");
    }

    public void setLocation(ParseGeoPoint value) {
        put("location", value);
    }

    // ==== ACTIVITY ===============================================================================

    public int getActivityType() {
        return getInt("activity_type");
    }

    public void setActivityType(int activityType) {
        put("activity_type", activityType);
    }

    // ==== QUERY ==================================================================================

    public static ParseQuery<Record> getQuery() {
        return ParseQuery.getQuery(Record.class);
    }
}
