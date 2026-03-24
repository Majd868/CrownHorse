package com.crownhorse.app.models;

public class Service {
    private String serviceId;
    private String providerId;
    private String name;
    private String description;
    private double price;
    private String category;
    private String location;
    private long createdAt;

    public Service() {}

    public Service(String serviceId, String providerId, String name, String description,
                   double price, String category, String location, long createdAt) {
        this.serviceId = serviceId;
        this.providerId = providerId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.location = location;
        this.createdAt = createdAt;
    }

    public String getServiceId() { return serviceId; }
    public void setServiceId(String serviceId) { this.serviceId = serviceId; }

    public String getProviderId() { return providerId; }
    public void setProviderId(String providerId) { this.providerId = providerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
