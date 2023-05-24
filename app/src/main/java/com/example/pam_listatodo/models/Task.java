package com.example.pam_listatodo.models;

import java.io.Serializable;

public class Task implements Serializable {
    private Integer id;
    private String taskTitle;
    private String taskDescription;
    private Long taskCreationTime;
    private Long taskDueTime;
    private Status taskStatus;
    private Boolean notificationsEnabled;
    private Category taskCategory;
    private String attachmentURI;

    public Task(Integer id, String taskTitle, String taskDescription, Long taskCreationTime, Long taskDueTime, Status taskStatus, Boolean notificationsEnabled, Category taskCategory, String attachmentURI) {
        this.id = id;
        this.taskTitle = taskTitle;
        this.taskDescription = taskDescription;
        this.taskCreationTime = taskCreationTime;
        this.taskDueTime = taskDueTime;
        this.taskStatus = taskStatus;
        this.notificationsEnabled = notificationsEnabled;
        this.taskCategory = taskCategory;
        this.attachmentURI = attachmentURI;
    }

    public Task(String taskTitle, String taskDescription, Long taskCreationTime, Long taskDueTime, Status taskStatus, Boolean notificationsEnabled, Category taskCategory, String attachmentURI) {
        this.taskTitle = taskTitle;
        this.taskDescription = taskDescription;
        this.taskCreationTime = taskCreationTime;
        this.taskDueTime = taskDueTime;
        this.taskStatus = taskStatus;
        this.notificationsEnabled = notificationsEnabled;
        this.taskCategory = taskCategory;
        this.attachmentURI = attachmentURI;
    }

    //todo: look for cloning?
    public Task(Task task) {
        this.id = task.id;
        this.taskTitle = task.taskTitle;
        this.taskDescription = task.taskDescription;
        this.taskCreationTime = task.taskCreationTime;
        this.taskDueTime = task.taskDueTime;
        this.taskStatus = task.taskStatus;
        this.notificationsEnabled = task.notificationsEnabled;
        this.taskCategory = task.taskCategory;
        this.attachmentURI = task.attachmentURI;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public Long getTaskCreationTime() {
        return taskCreationTime;
    }

    public void setTaskCreationTime(Long taskCreationTime) {
        this.taskCreationTime = taskCreationTime;
    }

    public Long getTaskDueTime() {
        return taskDueTime;
    }

    public void setTaskDueTime(Long taskDueTime) {
        this.taskDueTime = taskDueTime;
    }

    public Status getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Status taskStatus) {
        this.taskStatus = taskStatus;
    }

    public void setTaskStatus(String stringStatus) {
        if(stringStatus.equals("PENDING")) {
            this.taskStatus = Status.PENDING;
        } else {
            this.taskStatus = Status.COMPLETE;
        }
    }

    public Boolean getNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(Boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    public void setNotificationsEnabled(String stringNotificationsEnabled) {
        this.notificationsEnabled = stringNotificationsEnabled.equals("ON");
    }

    public Category getTaskCategory() {
        return taskCategory;
    }

    public void setTaskCategory(Category taskCategory) {
        this.taskCategory = taskCategory;
    }

    public void setTaskCategory(String stringCategory) {
        Category category = Category.NONE;
        switch(stringCategory) {
            case "NONE":
                category = Category.NONE;
                break;
            case "CHORES":
                category = Category.CHORES;
                break;
            case "UNIVERSITY":
                category = Category.UNIVERSITY;
                break;
            case "JOB":
                category = Category.JOB;
                break;
            case "OTHER":
                category = Category.OTHER;
                break;
        }

        this.taskCategory = category;
    }

    public String getTaskAttachmentURI() {
        return attachmentURI;
    }

    public void setTaskAttachmentURI(String attachmentURI) {
        this.attachmentURI = attachmentURI;
    }
}
