package com.example.pam_listatodo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.pam_listatodo.database.DatabaseTaskHandler;
import com.example.pam_listatodo.fragments.NewTaskFragment;
import com.example.pam_listatodo.fragments.RecyclerViewFragment;
import com.example.pam_listatodo.models.Category;
import com.example.pam_listatodo.models.Status;
import com.example.pam_listatodo.models.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    private String filterCategory;
    private Boolean showDoneTasks;
    private DatabaseTaskHandler db;
    private FloatingActionButton floatingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        setContentView(R.layout.activity_main);

        db = new DatabaseTaskHandler(this);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        showDoneTasks = prefs.getBoolean("showCompletedTasks",true);
        filterCategory = prefs.getString("categoryFilter","NONE");

        getAllTasks();
        floatingButton = findViewById(R.id.floatingActionButton);
        floatingButton.setOnClickListener(view -> getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainActivity, new NewTaskFragment())
                .setReorderingAllowed(true)
                .commit());

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainActivity, new RecyclerViewFragment())
                .setReorderingAllowed(true)
                .commit();
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
}

