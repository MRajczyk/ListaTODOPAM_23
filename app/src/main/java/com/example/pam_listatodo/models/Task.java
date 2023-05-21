package com.example.pam_listatodo.models;

public class Task {
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

    public Boolean getNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(Boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    public Category getTaskCategory() {
        return taskCategory;
    }

    public void setTaskCategory(Category taskCategory) {
        this.taskCategory = taskCategory;
    }

    public String getAttachmentURI() {
        return attachmentURI;
    }

    public void setAttachmentURI(String attachmentURI) {
        this.attachmentURI = attachmentURI;
    }
}
