package com.example.a7laboratorinis;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {

    int batteryLevel;
    Switch switchOnAndOff;

    private IntentFilter mIntentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    private final String CHANNEL_ID = "personal_notification";
    private final int NOTIFICATION_ID = 001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switchOnAndOff = findViewById(R.id.battery);
        switchOnAndOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(switchOnAndOff.isChecked()){
                    switchOnAndOff.setChecked(false);
                }
                switchOnAndOff.setChecked(true);
            }
        });
    }

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            batteryLevel = level * 100 / scale;
            if(batteryLevel <= 15 && switchOnAndOff.isChecked()){
                displayNotification(null);
            }
        }
    };

    public void displayNotification(View view){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            CharSequence name = "Personal Notifications";
            String description = "Include all the personal notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationChannel.setDescription(description);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, getIntent(), PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setSmallIcon(R.drawable.ic_launcher_background);
        builder.setContentTitle("Battery notification");
        builder.setContentText("Battery is getting low");
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mBatInfoReceiver,mIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}

