package com.crownhorse.app.models;

public class Horse {
    private String horseId;
    private String ownerId;
    private String name;
    private int age;
    private String type;
    private String description;
    private String photoUrl;
    private long createdAt;

    public Horse() {}

    public Horse(String horseId, String ownerId, String name, int age, String type,
                 String description, String photoUrl, long createdAt) {
        this.horseId = horseId;
        this.ownerId = ownerId;
        this.name = name;
        this.age = age;
        this.type = type;
        this.description = description;
        this.photoUrl = photoUrl;
        this.createdAt = createdAt;
    }

    public String getHorseId() { return horseId; }
    public void setHorseId(String horseId) { this.horseId = horseId; }

    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
