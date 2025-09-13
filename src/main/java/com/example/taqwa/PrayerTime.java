package com.example.taqwa;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.View;
import android.widget.TextView;

import com.example.taqwa.helpers.UserSettings;
import com.example.taqwa.helpers.PrayerTimes;


import org.arabeyes.prayertime.*;

import java.text.DateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;

import timber.log.Timber;

public class PrayerTime extends AppCompatActivity {

    public static final String UPDATE_VIEWS = "com.example.taqwa.UPDATE";
    private static final int BLINK_DURATION = 500;

    private static final int REQUEST_SEARCH_CITY = 2;
    private Boolean mUVReceiverRegistered = false;
    private BroadcastReceiver mUpdateViewsReceiver = null;
    private int mImportant = -1;
    private TextView mTextViewCity;
    private TextView mTextViewDate;
    private TextView mTextViewToNext;
    private TextView[][] mTextViewPrayers;
    private static final int COUNT_INTERVAL_SECOND = 1000;
    private static final int COUNT_INTERVAL_MINUTE = 60 * 1000;
    private Handler mCountHandler;
    private Runnable mUpdateCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.d("onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_prayer_time);
        Toolbar toolbar =findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.app_name);
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        PreferenceManager.setDefaultValues(this, R.xml.pref_locations, false);
        PreferenceManager.setDefaultValues(this, R.xml.pref_notifications, false);

        initReceiver();
        loadViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initReceiver() {
        mUVReceiverRegistered = false;
        mUpdateViewsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Timber.d("Receiver kicked by action: " + intent.getAction());
                updatePrayerViews();
            }
        };
    }

    protected void onResume() {
        Timber.d("OnResume");
        super.onResume();
        if (UserSettings.isNotificationEnabled(this) && !mUVReceiverRegistered) {
            registerReceiver(mUpdateViewsReceiver, new IntentFilter(UPDATE_VIEWS));
            mUVReceiverRegistered = true;
        }
        PrayerTimesManager.updatePrayerTimes(this, false);
        updatePrayerViews();
    }

    protected void onPause() {
        Timber.d("OnPause");
        super.onPause();
        if (mUVReceiverRegistered) {
            unregisterReceiver(mUpdateViewsReceiver);
            mUVReceiverRegistered = false;
        }
        stopCount();
    }

    private void loadViews() {
        mTextViewCity = findViewById(R.id.textViewCity);
        mTextViewDate = findViewById(R.id.textViewDate);
        mTextViewToNext = findViewById(R.id.textViewToNext);
        mTextViewPrayers = new TextView[][]{{findViewById(R.id.textViewFajrName), findViewById(R.id.btnStopFajr), findViewById(R.id.textViewFajrTime)}, {findViewById(R.id.textViewSunriseName), findViewById(R.id.btnStopSunrise), findViewById(R.id.textViewSunriseTime)}, {findViewById(R.id.textViewDhuhrName), findViewById(R.id.btnStopDhuhr), findViewById(R.id.textViewDhuhrTime)}, {findViewById(R.id.textViewAsrName), findViewById(R.id.btnStopAsr), findViewById(R.id.textViewAsrTime)}, {findViewById(R.id.textViewMaghribName), findViewById(R.id.btnStopMaghrib), findViewById(R.id.textViewMaghribTime)}, {findViewById(R.id.textViewIshaName), findViewById(R.id.btnStopIsha), findViewById(R.id.textViewIshaTime)}, {findViewById(R.id.textViewNextFajrName), findViewById(R.id.btnStopNextFajr), findViewById(R.id.textViewNextFajrTime)}};

        View.OnClickListener cityListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SearchCityActivity.class);
                startActivityForResult(intent, REQUEST_SEARCH_CITY);
            }
        };
        mTextViewCity.setOnClickListener(cityListener);
        mTextViewDate.setOnClickListener(cityListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Timber.d("onActivityResult");
        if (requestCode == REQUEST_SEARCH_CITY) {
            if (resultCode == Activity.RESULT_OK) {
                PrayerTimesManager.handleSettingsChange(this, -1, -1, -1);
            }
        }
    }

    private void updatePrayerViews() {
        if (PrayerTimesManager.prayerTimesNotAvailable()) {
            Timber.w("prayerTimesNotAvailable");
            return;
        }

        int i, j;

        GregorianCalendar now = new GregorianCalendar();
        mTextViewCity.setText(UserSettings.getCityName(this));

        mTextViewDate.setText(DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault()).format(now.getTime()));

        for (i = 0; i < Prayer.NB_PRAYERS + 1; i++) {
            mTextViewPrayers[i][1].setVisibility(View.INVISIBLE);
            mTextViewPrayers[i][2].setText(PrayerTimesManager.formatPrayerTime(i));
        }

        // change Dhuhr to Jumuaa if needed.
        mTextViewPrayers[2][0].setText(PrayerTimes.getName(this, 2, now));

        // Reset old important prayer to normal
        if (mImportant != -1) {
            for (j = 0; j < mTextViewPrayers[0].length; j++) {
                mTextViewPrayers[mImportant][j].setTypeface(null, Typeface.NORMAL);
                mTextViewPrayers[mImportant][j].clearAnimation();
                mTextViewPrayers[mImportant][j].setOnClickListener(null);
            }
        }

        // signal the new important prayer, which is the current if its time is recent, next otherwise
        GregorianCalendar current = PrayerTimesManager.getCurrentPrayer();
        if (current == null) {
            return;
        }
        long elapsed = now.getTimeInMillis() - current.getTimeInMillis();
        if (elapsed >= 0 ) {
            mTextViewToNext.setText(PrayerTimesManager.formatTimeFromCurrentPrayer(PrayerTime.this, now));
            startCount(false);

            // blink Current Prayers
            mImportant = PrayerTimesManager.getCurrentPrayerIndex();
            Animation anim = new AlphaAnimation(0.0f, 1.0f);
            anim.setDuration(BLINK_DURATION);
            anim.setRepeatMode(Animation.REVERSE);
            for (j = 0; j < mTextViewPrayers[0].length; j++) {
                mTextViewPrayers[mImportant][j].setTypeface(null, Typeface.BOLD);
                mTextViewPrayers[mImportant][j].startAnimation(anim);
            }

        } else {
            mTextViewToNext.setText(PrayerTimesManager.formatTimeToNextPrayer(this, now));
            startCount(true);

            // Bold Next Prayers
            mImportant = PrayerTimesManager.getNextPrayerIndex();
            for (i = 0; i < Prayer.NB_PRAYERS + 1; i++) {
                for (j = 0; j < mTextViewPrayers[0].length; j++) {
                    mTextViewPrayers[mImportant][j].setTypeface(null, Typeface.BOLD);
                }
            }
        }
    }

    private void stopCount() {
        if (mCountHandler != null) {
            mCountHandler.removeCallbacks(mUpdateCount);
            mCountHandler = null;
        }

        if (mUpdateCount != null) {
            mUpdateCount = null;
        }
    }

    private void startCount(final boolean down) {
        stopCount();

        mCountHandler = new Handler();
        mUpdateCount = new Runnable() {
            @Override
            public void run() {
                try {
                    GregorianCalendar now = new GregorianCalendar();
                    mTextViewToNext.setText(down ? PrayerTimesManager.formatTimeToNextPrayer(PrayerTime.this, now) : PrayerTimesManager.formatTimeFromCurrentPrayer(PrayerTime.this, now));
                } finally {
                    mCountHandler.postDelayed(mUpdateCount, UserSettings.getRounding(PrayerTime.this) == 1 ? COUNT_INTERVAL_MINUTE : COUNT_INTERVAL_SECOND);
                }
            }
        };
        mUpdateCount.run();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(PrayerTimesApp.updateLocale(newBase));
    }
}