package com.crownhorse.app.models;

public class Trainer {
    private String trainerId;
    private String name;
    private String specialization;
    private int yearsOfExperience;
    private String description;
    private String photoUrl;

    public Trainer() {}

    public Trainer(String trainerId, String name, String specialization,
                   int yearsOfExperience, String description, String photoUrl) {
        this.trainerId = trainerId;
        this.name = name;
        this.specialization = specialization;
        this.yearsOfExperience = yearsOfExperience;
        this.description = description;
        this.photoUrl = photoUrl;
    }

    public String getTrainerId() { return trainerId; }
    public void setTrainerId(String trainerId) { this.trainerId = trainerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public int getYearsOfExperience() { return yearsOfExperience; }
    public void setYearsOfExperience(int yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
}
