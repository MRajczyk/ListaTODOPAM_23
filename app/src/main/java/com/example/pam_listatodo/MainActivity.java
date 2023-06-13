package com.example.pam_listatodo;

import static android.Manifest.permission.POST_NOTIFICATIONS;
import static android.app.PendingIntent.FLAG_MUTABLE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.pam_listatodo.broadcast.NotificationReceiver;
import com.example.pam_listatodo.database.DatabaseTaskHandler;
import com.example.pam_listatodo.fragments.NewTaskFragment;
import com.example.pam_listatodo.fragments.RecyclerViewFragment;
import com.example.pam_listatodo.models.Category;
import com.example.pam_listatodo.models.Status;
import com.example.pam_listatodo.models.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    private String filterCategory;
    private Boolean showDoneTasks;
    private int minutesBeforeDueTimeAlarm;
    private boolean sendNotifications;
    private DatabaseTaskHandler db;
    private FloatingActionButton floatingButton;

    private Fragment savedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE, POST_NOTIFICATIONS}, PackageManager.PERMISSION_GRANTED);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            //Restore the fragment's instance
            savedFragment = getSupportFragmentManager().getFragment(savedInstanceState, "savedFragment");
            System.out.println(savedFragment);
        }

        //create notification channel
        NotificationManager myNotificationManager;
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel("TODO_NOTIFICATIONS", "todo_notif", importance);
        myNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        myNotificationManager.createNotificationChannel(channel);

        db = new DatabaseTaskHandler(this, this);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        this.showDoneTasks = prefs.getBoolean("showCompletedTasks",true);
        this.filterCategory = prefs.getString("categoryFilter","NONE");
        this.minutesBeforeDueTimeAlarm = Integer.parseInt(prefs.getString("minutesToNotification", "0"));
        this.sendNotifications = prefs.getBoolean("notifications", true);

        List<Task> tasks = getAllTasks();

        if(this.sendNotifications) {
            tasks.forEach(this::scheduleNotification);
        } else {
            tasks.forEach(this::cancelNotification);
        }

        floatingButton = findViewById(R.id.floatingActionButton);
        floatingButton.setOnClickListener(view -> getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainActivity, new NewTaskFragment())
                .setReorderingAllowed(true)
                .commit());



        if(this.savedFragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainActivity, this.savedFragment)
                    .setReorderingAllowed(true)
                    .commit();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainActivity, new RecyclerViewFragment())
                    .setReorderingAllowed(true)
                    .commit();
        }
    }

    public void scheduleNotification(Task task) {
        if(!this.sendNotifications) {
            Toast.makeText(this, "Could not set alarm, notifications are turned off in app settings", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(MainActivity.this, NotificationReceiver.class);
        intent.setData(Uri.parse("custom://" + task.getTaskTitle()));
        intent.setAction(String.valueOf(task.getTaskTitle()));
        intent.putExtra("taskName", task.getTaskTitle());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, FLAG_MUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        long temp = task.getTaskDueTime() - this.minutesBeforeDueTimeAlarm * 60L;
        System.out.println(temp);
        alarmManager.set(AlarmManager.RTC_WAKEUP, temp * 1000, pendingIntent);
    }

    public void cancelNotification(Task task) {
        Intent intent = new Intent(MainActivity.this, NotificationReceiver.class);
        intent.setData(Uri.parse("custom://" + task.getTaskTitle()));
        intent.setAction(String.valueOf(task.getTaskTitle()));
        intent.putExtra("taskName", task.getTaskTitle());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, FLAG_MUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    public List<Task> getAllTasks() {
        List<Task> returnTaskData = db.getAllTasks();
        returnTaskData.sort(Comparator.comparing(Task::getTaskDueTime));
        if(showDoneTasks.equals(false)) {
            returnTaskData = returnTaskData.stream()
                    .filter(taskData1 -> taskData1.getTaskStatus()
                            .equals(Status.PENDING))
                    .collect(Collectors.toList());
        }
        if(!filterCategory.equals("NONE")){
            returnTaskData = returnTaskData.stream()
                    .filter(taskData1 -> taskData1.getTaskCategory()
                            .equals(Category.valueOf(filterCategory)))
                    .collect(Collectors.toList());
        }

        return returnTaskData;
    }

    public List<Task> getTaskByTitle(String title) {
        List<Task> returnTaskData = getAllTasks();
        returnTaskData = returnTaskData.stream()
                .filter(taskData1 -> taskData1
                        .getTaskTitle()
                        .contains(title))
                .collect(Collectors.toList());

        returnTaskData.sort(Comparator.comparing(Task::getTaskDueTime));

        return returnTaskData;
    }

    public FloatingActionButton getFloatingActionButton() {
        return floatingButton;
    }

    public DatabaseTaskHandler getDb() {
        return db;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            this.startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        FragmentManager fm = getSupportFragmentManager();
        fm.putFragment(outState, "savedFragment", Objects.requireNonNull(fm.findFragmentById(R.id.mainActivity)));
    }
}

