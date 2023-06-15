package com.example.pam_listatodo.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pam_listatodo.MainActivity;
import com.example.pam_listatodo.R;
import com.example.pam_listatodo.adapters.TaskRecyclerViewAdapter;
import com.example.pam_listatodo.database.DatabaseTaskHandler;
import com.example.pam_listatodo.interfaces.IClickListener;
import com.example.pam_listatodo.models.Status;
import com.example.pam_listatodo.models.Task;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class RecyclerViewFragment extends Fragment implements IClickListener {

    private View view;
    private EditText editTextFindTask;
    private Button sortButton;
    private List<Task> taskData;
    private TaskRecyclerViewAdapter adapter;
    private DatabaseTaskHandler dbHandle;
    private Integer openTaskId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(getArguments() != null) {
            this.openTaskId = getArguments().getInt("task_id");
        }
        return inflater.inflate(R.layout.fragment_recycler_tasks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.view = view;
        this.sortButton = view.findViewById(R.id.sortButton);
        this.taskData = ((MainActivity)requireActivity()).getAllTasks();
        this.editTextFindTask = view.findViewById(R.id.task_filter);
        this.dbHandle = ((MainActivity)requireActivity()).getDb();
        this.editTextFindTask.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                taskData = ((MainActivity)requireActivity()).getTaskByTitle(charSequence.toString());
                startRecyclerView();
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //stub
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //stub
            }
        });

        sortButton.setOnClickListener(v -> {
            taskData.sort((t1, t2) -> {
                if(t1.getTaskStatus() != t2.getTaskStatus()) {
                    return t1.getTaskStatus().compareTo(t2.getTaskStatus());
                }

                return t1.getTaskDueTime().compareTo(t2.getTaskDueTime());
            });
            adapter.notifyDataSetChanged();
        });

        startRecyclerView();
        if(this.openTaskId != null) {

            Optional<Task> optional = taskData.stream()
                    .filter(x -> this.openTaskId.equals(x.getId()))
                    .findFirst();
            if(optional.isPresent()) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", optional.get());
                Fragment fragment = new EditTaskFragment();
                fragment.setArguments(bundle);
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainActivity, fragment)
                        .setReorderingAllowed(true)
                        .commit();
            }
            this.openTaskId = null;
        }
    }

    private void startRecyclerView() {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_tasks);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new TaskRecyclerViewAdapter(taskData, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClickItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", taskData.get(position));
        Fragment fragment = new EditTaskFragment();
        fragment.setArguments(bundle);
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.mainActivity, fragment)
                .setReorderingAllowed(true)
                .commit();
    }

    @Override
    public void onClickButtonFinish(int position) {
        Task updatedTask = new Task(taskData.get(position));
        Status taskNewStatus = updatedTask.getTaskStatus() == Status.COMPLETE ? Status.PENDING : Status.COMPLETE;
        updatedTask.setTaskStatus(taskNewStatus);
        System.out.println(updatedTask.getTaskStatus().toString());
        this.dbHandle.updateTask(updatedTask);
        taskData.set(position, updatedTask);
        adapter.notifyItemChanged(position);
        if(taskNewStatus.equals(Status.COMPLETE)) {
            ((MainActivity) requireActivity()).cancelNotification(taskData.get(position));
        } else {
            if(((MainActivity) requireActivity()).sendNotifications && taskData.get(position).getNotificationsEnabled() == 1) {
                ((MainActivity) requireActivity()).scheduleNotification(taskData.get(position));
            }
        }

        //to nizej dziala, ale czy my chcemy zeby tak to dzialalo?
//        this.taskData = ((MainActivity) requireActivity()).getAllTasks();
//        startRecyclerView();
    }

    @Override
    public void onClickButtonDelete(int position) {
        ((MainActivity) requireActivity()).getDb().deleteTask(taskData.get(position));
        ((MainActivity) requireActivity()).cancelNotification(taskData.get(position));
        taskData.remove(position);
        adapter.notifyItemRemoved(position);
    }
}
