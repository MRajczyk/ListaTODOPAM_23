package com.example.pam_listatodo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.pam_listatodo.models.Category;
import com.example.pam_listatodo.models.Status;
import com.example.pam_listatodo.models.Task;

import java.util.ArrayList;
import java.util.List;

public class DatabaseTaskHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "todo.db";
    private static final String TABLE_NAME = "tasks";
    private static final String ID = "ID";
    private static final String TASK_TITLE = "TASK_TITLE";
    private static final String TASK_CREATION_TIME = "TASK_CREATION_TIME";
    private static final String TASK_DUE_TIME = "TASK_DUE_DATE_TIME";
    private static final String TASK_DESCRIPTION = "TASK_DESCRIPTION";
    private static final String TASK_STATUS = "TASK_STATUS";
    private static final String TASK_NOTIFICATION_ENABLED = "TASK_NOTIFICATION_ENABLED";
    private static final String TASK_CATEGORY = "TASK_CATEGORY";
    private static final String TASK_ATTACHMENT_URI = "TASK_ATTACHMENT_URI";


    public DatabaseTaskHandler(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DatabaseTaskHandler(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version, @Nullable DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    public DatabaseTaskHandler(@Nullable Context context, @Nullable String name, int version, @NonNull SQLiteDatabase.OpenParams openParams) {
        super(context, name, version, openParams);
    }

    public DatabaseTaskHandler(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TASKS_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + ID + " INTEGER PRIMARY KEY,"
                + TASK_TITLE + " TEXT,"
                + TASK_DESCRIPTION + " TEXT,"
                + TASK_CREATION_TIME + " TEXT,"
                + TASK_DUE_TIME + " TEXT,"
                + TASK_STATUS + " TEXT,"
                + TASK_NOTIFICATION_ENABLED + " BOOLEAN,"
                + TASK_CATEGORY + " TEXT,"
                + TASK_ATTACHMENT_URI + " BLOB)";

        sqLiteDatabase.execSQL(CREATE_TASKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void insertTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(TASK_TITLE, task.getTaskTitle());
        values.put(TASK_DESCRIPTION, task.getTaskDescription());
        values.put(TASK_CREATION_TIME, task.getTaskCreationTime().toString());
        values.put(TASK_DUE_TIME, task.getTaskDueTime().toString());
        values.put(TASK_STATUS, task.getTaskStatus().toString());
        values.put(TASK_NOTIFICATION_ENABLED, task.getNotificationsEnabled());
        values.put(TASK_CATEGORY, task.getTaskCategory().toString());
        values.put(TASK_ATTACHMENT_URI, task.getAttachmentURI());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public List<Task> getAllTasks() {
        List<Task> contactList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Task contact = new Task(
                        Integer.valueOf(cursor.getString(0))
                        , cursor.getString(1)
                        , cursor.getString(2)
                        , Long.valueOf(cursor.getString(3))
                        , Long.valueOf(cursor.getString(4))
                        , Status.valueOf(cursor.getString(5))
                        , Boolean.valueOf(cursor.getString(6))
                        , Category.valueOf(cursor.getString(7))
                        , cursor.getString(8)
                );
                contactList.add(contact);
            } while (cursor.moveToNext());
        }

        return contactList;
    }

    public int updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TASK_TITLE, task.getTaskTitle());
        values.put(TASK_DESCRIPTION, task.getTaskDescription());
        values.put(TASK_CREATION_TIME, task.getTaskCreationTime().toString());
        values.put(TASK_DUE_TIME, task.getTaskDueTime().toString());
        values.put(TASK_STATUS, task.getTaskStatus().toString());
        values.put(TASK_NOTIFICATION_ENABLED, task.getNotificationsEnabled());
        values.put(TASK_CATEGORY, task.getTaskCategory().toString());
        values.put(TASK_ATTACHMENT_URI, task.getAttachmentURI());

        System.out.println(task.getId());

        return db.update(TABLE_NAME, values, ID + " = ?",
                new String[]{String.valueOf(task.getId())});
    }
}
