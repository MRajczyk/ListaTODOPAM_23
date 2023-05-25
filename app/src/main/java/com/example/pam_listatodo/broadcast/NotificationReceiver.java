package com.example.pam_listatodo.broadcast;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.pam_listatodo.MainActivity;
import com.example.pam_listatodo.R;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent resultIntent = new Intent(context, MainActivity.class);
        String test = "";
        if(intent.getExtras() != null) {
            test = (String) intent.getExtras().get("taskName");
            resultIntent.putExtra("task_title", test);
        }

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_MUTABLE);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, "TODO_NOTIFICATIONS")
                .setSmallIcon(R.drawable.pencil)
                .setContentTitle("Task due!: " + test)
                .setContentText("Click to see more")
                .setContentIntent(resultPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        try {
            notificationManagerCompat.notify((int) Math.floor(Math.random() * (200 + 1) + 0), notification.build());
        }
        catch (SecurityException e) {
            System.out.println("Insufficient permissions");
        }
    }
}
