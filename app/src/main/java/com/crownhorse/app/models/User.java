package com.crownhorse.app.models;

public class User {
    private String uid;
    private String name;
    private String email;
    private String photoUrl;
    private String role;
    private String country;
    private String city;
    private String currency;
    private String language;
    private long lastSeen;
    private boolean isOnline;
    private String fcmToken;
    private long createdAt;

    public User() {}

    public User(String uid, String name, String email, String photoUrl, String role,
                String country, String city, String currency, String language,
                long lastSeen, boolean isOnline, String fcmToken) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.photoUrl = photoUrl;
        this.role = role;
        this.country = country;
        this.city = city;
        this.currency = currency;
        this.language = language;
        this.lastSeen = lastSeen;
        this.isOnline = isOnline;
        this.fcmToken = fcmToken;
    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public long getLastSeen() { return lastSeen; }
    public void setLastSeen(long lastSeen) { this.lastSeen = lastSeen; }

    public boolean isOnline() { return isOnline; }
    public void setOnline(boolean online) { isOnline = online; }

    public String getFcmToken() { return fcmToken; }
    public void setFcmToken(String fcmToken) { this.fcmToken = fcmToken; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
