package com.zar.weupdateyou.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by KJS on 12/1/2016.
 */
public class NotificationServiceStarterReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmManagerBroadcastReceiver.setupAlarm(context);
    }
}
