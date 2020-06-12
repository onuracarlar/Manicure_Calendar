package com.lanislaru.manicure_loop;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ReminderBroadcast extends BroadcastReceiver {

    private static final String CHANNEL_ID = "notificationID";

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.delete_button_image)
                .setContentTitle("My Manicure Calendar")
                .setContentText("Hey, don't forget to have your manicure!")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Hey, don't forget to have your manicure!"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat=NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(31, builder.build());
    }
}
