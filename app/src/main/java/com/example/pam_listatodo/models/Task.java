package com.example.pam_listatodo.models;

public class Task {
    private Integer id;
    private String taskTitle;
    private String taskDescription;
    private Long taskCreationTime;
    private Long taskDueDate;
    private Status taskStatus;
    private Boolean enabledNotifications;
    private Category taskCategory;
    private String attachmentURI;

    public Task(Integer id, String taskTitle, String taskDescription, Long taskCreationTime, Long taskDueDate, Status taskStatus, Boolean enabledNotifications, Category taskCategory, String attachmentURI) {
        this.id = id;
        this.taskTitle = taskTitle;
        this.taskDescription = taskDescription;
        this.taskCreationTime = taskCreationTime;
        this.taskDueDate = taskDueDate;
        this.taskStatus = taskStatus;
        this.enabledNotifications = enabledNotifications;
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

    public Long getTaskDueDate() {
        return taskDueDate;
    }

    public void setTaskDueDate(Long taskDueDate) {
        this.taskDueDate = taskDueDate;
    }

    public Status getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Status taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Boolean getEnabledNotifications() {
        return enabledNotifications;
    }

    public void setEnabledNotifications(Boolean enabledNotifications) {
        this.enabledNotifications = enabledNotifications;
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
