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

public class RecyclerViewFragment extends Fragment implements IClickListener {

    private View view;
    private EditText editTextFindTask;
    private Button sortButton;
    private List<Task> taskData;
    private TaskRecyclerViewAdapter adapter;
    private DatabaseTaskHandler dbHandle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler_tasks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.view = view;
        this.sortButton = view.findViewById(R.id.sortButton);
        taskData = ((MainActivity)requireActivity()).getAllTasks();
        editTextFindTask = view.findViewById(R.id.task_filter);
        this.dbHandle = ((MainActivity)requireActivity()).getDb();
        editTextFindTask.addTextChangedListener(new TextWatcher() {
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
            //todo: sort by most urgent task
        });

        startRecyclerView();
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
        Fragment fragment = new MoreDetailsFragment();
        fragment.setArguments(bundle);
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.mainActivity, fragment)
                .setReorderingAllowed(true)
                .commit();
    }

    //todo: see how it really works xd
    @Override
    public void onClickButtonFinish(int position) {
        Task updatedTask = new Task(taskData.get(position));
        updatedTask.setTaskStatus(updatedTask.getTaskStatus() == Status.COMPLETE ? Status.PENDING : Status.COMPLETE);
        System.out.println(updatedTask.getTaskStatus().toString());
        this.dbHandle.updateTask(updatedTask);
        taskData.set(position, updatedTask); //po bozemu ale nie dziala bo nie refreshuje filtru :( (moze i dobrze?)
        adapter.notifyItemChanged(position);

        //to nizej dziala, ale czy my chcemy zeby tak to dzialalo?
//        this.taskData = ((MainActivity) requireActivity()).getAllTasks();
//        startRecyclerView();
    }

    //todo: lol
    @Override
    public void onClickButtonDelete(int position) {
        ((MainActivity) requireActivity()).getDb().deleteTask(taskData.get(position));
        taskData.remove(position);
        adapter.notifyItemRemoved(position);
    }
}
