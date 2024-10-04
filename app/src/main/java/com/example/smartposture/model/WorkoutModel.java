package com.example.smartposture.model;

import java.util.List;

public class WorkoutModel {
    private int id;
    private String title;
    private String description;
    private String path;
    private List<String> steps;

    public WorkoutModel() {
    }

    public WorkoutModel(int id, String title, String description, String path, List<String> steps) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.path = path;
        this.steps = steps;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPath() {
        return path;
    }

    public List<String> getSteps() {
        return steps;
    }
}
