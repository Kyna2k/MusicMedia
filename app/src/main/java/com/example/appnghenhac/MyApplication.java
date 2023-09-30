package com.example.appnghenhac;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

public class MyApplication extends Application {
    public static final String CHANNEL_ID = "MyNotification";
    @Override
    public void onCreate() {
        super.onCreate();
        createpushNotification();

    }
    private void createpushNotification()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Tên của NotificationChannel cần đăng ký
            CharSequence name = "R.string.channel_name";
            //Mô tả của NotificationChannel
            String description = "R.string.channel_name";
            //Độ ưu tiên của Notification
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            //Ta sẽ sử dụng RingtoneManager để lấy uri của âm thanh notification theo máy
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            //Tạo thêm một audioAttributes
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            //Đăng ký NotificationChannel
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            //Set sound cho notifcation
            channel.setSound(null,null);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
    }
}
