package com.example.pam_listatodo.adapters;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pam_listatodo.R;
import com.example.pam_listatodo.interfaces.IClickListener;
import com.example.pam_listatodo.models.Status;
import com.example.pam_listatodo.models.Task;
import com.google.android.material.chip.Chip;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TaskRecyclerViewAdapter extends RecyclerView.Adapter<TaskRecyclerViewAdapter.ViewHolder> {

    private final IClickListener clickListener;
    private final List<Task> tasks;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public TaskRecyclerViewAdapter(List<Task> tasks, IClickListener clickListener) {
        this.tasks = tasks;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_row, parent, false);
        return new ViewHolder(v, this.clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.taskTitle.setText(tasks.get(position).getTaskTitle());
        holder.taskCategory.setText(tasks.get(position).getTaskCategory().toString());
        holder.taskDeadline.setText(Instant.ofEpochSecond(tasks.get(position).getTaskDueTime()).atZone(ZoneId.systemDefault()).format(formatter));
        if (tasks.get(position).getTaskStatus().equals(Status.COMPLETE)) {
            holder.completeTask.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(holder.completeTask.getContext(), R.color.green)));
            holder.completeTask.setText("Done!");
        }
        else {
            holder.completeTask.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(holder.completeTask.getContext(), R.color.lightgrey)));
            holder.completeTask.setText("Pending");
        }
        if (tasks.get(position).getTaskAttachmentURI() != null && !tasks.get(position).getTaskAttachmentURI().equals(""))
            holder.taskAttachment.setVisibility(View.VISIBLE);
        else
            holder.taskAttachment.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView taskTitle;
        TextView taskDeadline;
        TextView taskCategory;
        Chip completeTask;
        ImageView taskAttachment;
        ImageView deleteTask;

        ViewHolder(View itemView, IClickListener clickListener) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.task_title);
            taskCategory = itemView.findViewById(R.id.task_category);
            taskDeadline = itemView.findViewById(R.id.task_deadline);
            completeTask = itemView.findViewById(R.id.task_done_chip);
            taskAttachment = itemView.findViewById(R.id.task_attachment);
            deleteTask = itemView.findViewById(R.id.task_delete);

            deleteTask.setOnClickListener(view -> clickListener.onClickButtonDelete(getAdapterPosition()));
            completeTask.setOnClickListener(view -> clickListener.onClickButtonFinish(getAdapterPosition()));
            itemView.setOnClickListener(v -> clickListener.onClickItem(getAdapterPosition()));
        }
    }
}
