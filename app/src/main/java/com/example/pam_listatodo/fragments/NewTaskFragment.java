package com.example.pam_listatodo.fragments;

import static android.app.Activity.RESULT_OK;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.pam_listatodo.MainActivity;
import com.example.pam_listatodo.R;
import com.example.pam_listatodo.models.Category;
import com.example.pam_listatodo.models.Status;
import com.example.pam_listatodo.models.Task;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

public class NewTaskFragment extends Fragment {

    private View view;
    EditText taskTitle;
    EditText dueDate;
    EditText dueTime;
    Spinner taskCategory;
    Spinner taskStatus;
    Spinner taskNotifications;
    EditText taskDescription;
    EditText taskAttachment;
    ImageView deleteAttachment;
    ImageView viewAttachment;

    Button createTaskButton;
    Button returnButton;

    Integer year = 2023;
    Integer month = 5;
    Integer day = 23;
    Integer hour = 0;
    Integer minute = 0;
    private ActivityResultLauncher<Intent> filePickerLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View fview, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(fview, savedInstanceState);
        ((MainActivity)requireActivity()).getFloatingActionButton().hide();
        this.view = fview;

        this.taskTitle = this.view.findViewById(R.id.task_title);
        this.dueDate = this.view.findViewById(R.id.due_date_value);
        this.dueTime = this.view.findViewById(R.id.due_time_value);
        this.taskCategory = this.view.findViewById(R.id.task_category_value);
        this.taskStatus = this.view.findViewById(R.id.task_status_value);
        this.taskNotifications = this.view.findViewById(R.id.task_notification_enabled_value);
        this.taskDescription = this.view.findViewById(R.id.task_description);
        this.taskAttachment = this.view.findViewById(R.id.task_attachment);
        this.deleteAttachment = this.view.findViewById(R.id.delete_attachment);
        this.viewAttachment = this.view.findViewById(R.id.view_attachment);

        this.createTaskButton = this.view.findViewById(R.id.create);
        this.returnButton = this.view.findViewById(R.id.returnb);
        this.returnButton.setOnClickListener(v -> switchFragment());

        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = (view, year, month, dayOfMonth) -> {
            this.year = year;
            this.month = month + 1;
            this.day = dayOfMonth;
            dueDate.setText(getDateString());
        };

        TimePickerDialog.OnTimeSetListener time = (view, hourOfDay, minute) -> {
            this.hour = hourOfDay;
            this.minute = minute;
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            dueTime.setText(getTimeString());
        };

        dueDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this.view.getContext(), date,
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        });

        dueTime.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(this.view.getContext(), time,
                    calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            timePickerDialog.show();
        });

        filePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        saveAttachment(result, this.taskAttachment);
                    }
                });

        taskAttachment.setOnClickListener(v -> {
            Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
            chooseFile.setType("*/*");
            chooseFile = Intent.createChooser(chooseFile, "Choose a file");
            filePickerLauncher.launch(chooseFile);
        });

        deleteAttachment.setOnClickListener(v -> {
            File dir = new File(this.view.getContext().getExternalFilesDir("attachments").getAbsolutePath());
            File file = new File(dir, taskAttachment.getText().toString());
            file.delete();
            taskAttachment.setText("");
        });

        this.viewAttachment.setOnClickListener(v -> {
            openAttachment();
        });

        this.createTaskButton.setOnClickListener(v -> createTask(calendar));
    }

    private String getDateString() {
        StringBuilder sb = new StringBuilder();
        if(day < 10) {
            sb.append("0").append(day);
        } else {
            sb.append(day);
        }
        sb.append("-");
        if(month < 10) {
            sb.append("0").append(month);
        } else {
            sb.append(month);
        }
        sb.append("-");
        sb.append(year);

        return sb.toString();
    }

    private String getTimeString() {
        StringBuilder sb = new StringBuilder();
        sb.append(hour);
        sb.append(":");
        if(minute < 10) {
            sb.append("0").append(minute);
        } else {
            sb.append(minute);
        }

        return sb.toString();
    }

    private void createTask(Calendar calendar) {
        if(calendar == null) {
            return;
        }
        if (validateData()) {
            Task taskData = new Task(taskTitle.getText().toString()
                    , taskDescription.getText().toString()
                    , Instant.now().getEpochSecond()
                    , getUnixTimeFromDate(getDateString() + " " + getTimeString())
                    , Status.PENDING
                    , taskNotifications.getSelectedItem().toString().equals("ON") ? 1 : 0
                    , Category.valueOf(taskCategory.getSelectedItem().toString())
                    , taskAttachment.getText().toString());

            ((MainActivity) requireActivity()).getDb().insertTask(taskData);
            ((MainActivity) requireActivity()).getAllTasks();

            if (taskNotifications.getSelectedItem().toString().equals("ON")) {
                ((MainActivity) requireActivity()).scheduleNotification(taskData);
            }
            switchFragment();
        }
    }

    private void openAttachment() {
        if(this.taskAttachment.getText().toString().equals("")) {
            Toast.makeText(getActivity(), "Attachment url is empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            File attachment = new File(this.taskAttachment.getText().toString());
//            String dir = this.view.getContext().getExternalFilesDir("attachments").getAbsolutePath();
//            File destination = new File(dir, Arrays.asList(this.taskAttachment.getText().toString().split("/")).get(Arrays.asList(this.taskAttachment.getText().toString().split("/")).size() - 1) );

            Uri apkURI = FileProvider.getUriForFile(
                    this.view.getContext(),
                    this.view.getContext().getApplicationContext()
                            .getPackageName() + ".provider", attachment);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(apkURI, "*/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity(), "Could not open file!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void saveAttachment(ActivityResult result, EditText attachmentText) {
        Uri uri = Uri.parse(result.getData().getDataString());

        String filename = getFileName(uri);
        filename = generateUniqueFilename(filename);
        attachmentText.setText(filename);

        File dir = new File(this.view.getContext().getExternalFilesDir("attachments").getAbsolutePath());
        File destination = new File(dir, filename);

        try(InputStream in = this.view.getContext().getContentResolver().openInputStream(uri);) {
            destination.createNewFile();
            copy(in, destination);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.taskAttachment.setText(destination.toPath().toString());
    }

    private String generateUniqueFilename(String filename) {
        String extension = filename.substring(filename.lastIndexOf("."));
        return UUID.randomUUID() + extension;
    }

    private String getFileName(Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DISPLAY_NAME};
        ContentResolver cr = this.view.getContext().getContentResolver();
        Cursor metaCursor = cr.query(uri, projection, null, null, null);
        if (metaCursor != null) {
            try {
                if (metaCursor.moveToFirst()) {
                    return metaCursor.getString(0);
                }
            } finally {
                metaCursor.close();
            }
        }
        throw new IllegalStateException("Could not get file name");
    }

    private void switchFragment() {
        ((MainActivity) requireActivity()).getFloatingActionButton().show();
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.mainActivity, new RecyclerViewFragment())
                .setReorderingAllowed(true)
                .commit();
    }

    private boolean validateData() {
        boolean hasError = false;
        if (this.taskTitle.getText().toString().isEmpty()) {
            this.taskTitle.setError("Title is required");
            hasError = true;
        }
        if (this.taskDescription.getText().toString().isEmpty()) {
            this.taskDescription.setError("Description is required");
            hasError = true;
        }
        if (dueDate.getText().toString().isEmpty()) {
            dueDate.setError("Due date is required");
            hasError = true;
        }
        if (dueTime.getText().toString().isEmpty()) {
            dueTime.setError("Due time is required");
            hasError = true;
        }
        if(getUnixTimeFromDate(getDateString() + " " + getTimeString()) < Instant.now().getEpochSecond()) {
            dueTime.setError("Can't select a datetime that has already passed");
            hasError = true;
        }

        return !hasError;
    }

    public static void copy(InputStream in, File dst) throws IOException {
        try (OutputStream out = Files.newOutputStream(dst.toPath())) {
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        }
    }

    private long getUnixTimeFromDate(String string_date) {
        long seconds = 0;
        SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        f.setTimeZone(TimeZone.getDefault());
        try {
            Date d = f.parse(string_date);
            seconds = d.getTime() / 1000L;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return seconds;
    }
}

//ODPALANIE JAKIEGOKOLWIEK PLICZKU!
//
//try {
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setDataAndType(Uri.fromFile(file), fileMimeType);
//        startActivity(intent);
//        } catch (ActivityNotFoundException e) {
//        // no Activity to handle this kind of files
//        }