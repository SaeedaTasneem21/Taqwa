package com.example.taqwa.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.example.taqwa.helpers.UserSettings;
import timber.log.Timber;

public class DeviceLocaleChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Timber.i("=============== " + action);
        String prefLanguage = UserSettings.getPrefLanguage(context);
        if (UserSettings.languageUsesDeviceSettings(context, prefLanguage)) {
            UserSettings.setLocale(context, prefLanguage, null);
        }
    }
}