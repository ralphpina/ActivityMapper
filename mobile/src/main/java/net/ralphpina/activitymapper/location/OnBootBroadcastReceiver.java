package net.ralphpina.activitymapper.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import net.ralphpina.activitymapper.Account;

public class OnBootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Account.get().isUserVerified()) {
            LocationTrackingService.start();
        }
    }
}
