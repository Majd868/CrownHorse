package com.crownhorse.app.models;

public class Booking {
    private String bookingId;
    private String ownerId;
    private String providerId;
    private String horseId;
    private String serviceId;
    private String trainerId;
    private long datetime;
    private String status;
    private long createdAt;

    public Booking() {}

    public Booking(String bookingId, String ownerId, String providerId, String horseId,
                   String serviceId, String trainerId, long datetime, String status, long createdAt) {
        this.bookingId = bookingId;
        this.ownerId = ownerId;
        this.providerId = providerId;
        this.horseId = horseId;
        this.serviceId = serviceId;
        this.trainerId = trainerId;
        this.datetime = datetime;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    public String getProviderId() { return providerId; }
    public void setProviderId(String providerId) { this.providerId = providerId; }

    public String getHorseId() { return horseId; }
    public void setHorseId(String horseId) { this.horseId = horseId; }

    public String getServiceId() { return serviceId; }
    public void setServiceId(String serviceId) { this.serviceId = serviceId; }

    public String getTrainerId() { return trainerId; }
    public void setTrainerId(String trainerId) { this.trainerId = trainerId; }

    public long getDatetime() { return datetime; }
    public void setDatetime(long datetime) { this.datetime = datetime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
