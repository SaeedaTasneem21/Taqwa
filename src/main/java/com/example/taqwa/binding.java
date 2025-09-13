package com.example.taqwa;

import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public interface binding {
    @Nullable
    IBinder onBind(Intent intent);
}
