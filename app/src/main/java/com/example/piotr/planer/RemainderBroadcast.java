package com.example.piotr.planer;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

public class RemainderBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String eventName = intent.getStringExtra("eventName");
        // int id = intent.getIntExtra("id", 0);
        int requestCode = intent.getIntExtra("requestCode", 0);

        int id = 1;


        if (requestCode == 0) {
            Intent remindIntent = new Intent(context, MainActivity.class);
            remindIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            remindIntent.putExtra("eventName", eventName);
            remindIntent.putExtra("requestCode", requestCode);

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            notificationManagerCompat.cancel(id);

            context.startActivity(remindIntent);

        }




    }
}
