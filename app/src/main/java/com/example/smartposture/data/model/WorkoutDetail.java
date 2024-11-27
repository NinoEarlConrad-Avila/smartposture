package com.example.smartposture.data.model;

import java.util.List;

public class WorkoutDetail {
    private String name;
    private String description;
    private String path;
    private List<String> steps;

    public WorkoutDetail(String name, String description, String path, List<String> steps) {
        this.name = name;
        this.description = description;
        this.path = path;
        this.steps = steps;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<String> getSteps() {
        return steps;
    }

    public void setSteps(List<String> steps) {
        this.steps = steps;
    }
}
