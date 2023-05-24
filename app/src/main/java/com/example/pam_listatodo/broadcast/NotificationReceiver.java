package com.example.pam_listatodo.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.pam_listatodo.R;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, "TODO_NOTIFICATIONS")
                .setSmallIcon(R.drawable.bin)
                .setContentTitle("Przypomnienie o zblizajacym sie zadaniu")
                .setContentText("Do roboty")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        System.out.println("!!!!!!!!!!!!!!otrzymano powiadomienie");

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        try {
            notificationManagerCompat.notify((int) Math.floor(Math.random() * (200 + 1) + 0), notification.build());
        }
        catch (SecurityException e) {
            System.out.println("Insufficient permissions");
        }
    }
}
