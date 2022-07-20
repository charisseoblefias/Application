package com.example.application.pomodoroactivities;

import static com.example.application.PomodoroUtils.PomodoroConstants.CHANNEL_ID;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.example.application.R;

import java.util.Objects;

public class PomodoroApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID,
                    getResources().getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_LOW);

            NotificationManager notificationManager = Objects.requireNonNull(
                    getSystemService(NotificationManager.class));

            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
