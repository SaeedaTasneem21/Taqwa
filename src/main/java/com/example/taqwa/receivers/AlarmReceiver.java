package com.example.taqwa.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.taqwa.PrayerTimesManager;
import com.example.taqwa.helpers.UserSettings;

import timber.log.Timber;

public class AlarmReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {

        if (UserSettings.isNotificationEnabled(context)) {

        }
        else {
            Timber.e("Alarm received when set off by user!");
        }


        // Re-arm alarm.
        PrayerTimesManager.updatePrayerTimes(context, false);
    }
}
