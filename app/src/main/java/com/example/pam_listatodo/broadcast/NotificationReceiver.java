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
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        String taskName = "";
        if(intent.getExtras() != null) {
            taskName = (String) intent.getExtras().get("taskName");
            resultIntent.putExtra("task_id", (Integer) intent.getExtras().get("taskId"));
        }

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, "TODO_NOTIFICATIONS")
                .setSmallIcon(R.drawable.pencil)
                .setContentTitle("Task due!: " + taskName)
                .setContentText("Click to see more")
                .setContentIntent(resultPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        try {
            notificationManagerCompat.notify((int) Math.floor(Math.random() * (1000000)), notification.build());
        }
        catch (SecurityException e) {
            System.out.println("Insufficient permissions");
        }
    }
}
