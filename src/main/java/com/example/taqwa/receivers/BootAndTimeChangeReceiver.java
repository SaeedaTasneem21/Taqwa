package com.example.taqwa.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.taqwa.PrayerTime;
import com.example.taqwa.PrayerTimesManager;


import timber.log.Timber;

public class BootAndTimeChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Timber.i("=============== " + action);

        if (null != action) {
            if (action.equals(Intent.ACTION_BOOT_COMPLETED))
            {
                PrayerTimesManager.handleBootComplete(context);
            }
            else // TIME_SET
            {
                PrayerTimesManager.handleTimeChange(context);

                // Broadcast to MainActivity so it updates its screen if on
                Intent updateIntent = new Intent(PrayerTime.UPDATE_VIEWS);
                context.sendBroadcast(updateIntent);
            }
        }
    }
}