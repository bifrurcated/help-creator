package ru.vvsu.helpcreator.service;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class TaskService extends Service<Void> {

    private Task<Void> task;

    public TaskService() {
    }

    public void setTask(Task<Void> task) {
        this.task = task;
    }

    @Override
    protected Task<Void> createTask() {
        return task;
    }
}
