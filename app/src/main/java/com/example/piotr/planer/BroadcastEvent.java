package com.example.piotr.planer;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

public class BroadcastEvent extends BroadcastReceiver {

    public static final String CHANNEL_ID = "channelID";

    @Override
    public void onReceive(Context context, Intent intent) {
        String eventName = intent.getStringExtra("eventName");
        int id = intent.getIntExtra("id", 1);
        final String ACTION_REMIND = "remind";

        Intent i = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, 0);

        Intent remindIntent = new Intent(context, RemainderBroadcast.class);
        remindIntent.setAction(ACTION_REMIND);
        remindIntent.putExtra("eventName", eventName);
        remindIntent.putExtra("id", id);
        remindIntent.putExtra("requestCode", 0);
        PendingIntent remindPIntent = PendingIntent.getBroadcast(context, 0, remindIntent, 0);



        id = 1;

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Zaplanowane")
                .setContentText(eventName)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(sound)
                .setContentIntent(PendingIntent.getActivity(MainActivity.mContext, 0, new Intent(), 0))
                .addAction(0, "Przypomnij później", remindPIntent);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(id, builder.build());
    }
}
