package com.example.smartposture.data.model;

import java.io.Serializable;

public class Workout implements Serializable {
    public int id;
    public String name;
    public String description;
    public String path;
    public String category;

    public Workout(int id, String name, String description, String path, String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.path = path;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
