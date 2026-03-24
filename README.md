# CrownHorse - Horse Services Platform

A full-featured Android app (Java) for connecting horse owners with service providers and trainers.

## Features

- 🐴 **Horse Management** – Add, edit, and manage your horses with photos
- 🔧 **Services Marketplace** – Browse and list horse care services  
- 📅 **Booking System** – Schedule appointments with providers and trainers
- 💬 **Real-time Chat** – Messaging with image sharing
- 👤 **Multi-role Profiles** – Horse owner or Service Provider / Trainer
- 🌍 **Multi-language** – English, Arabic (RTL), Hebrew (RTL)
- 🔔 **Push Notifications** – FCM-powered alerts

## Tech Stack

- Java (Android)
- Firebase Auth, Firestore, Storage, Messaging
- Material Design 3
- Glide image loading
- CircleImageView

## Firebase Setup

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project (or select existing)
3. Add an Android app:
   - Package name: `com.crownhorse.app`
   - Download `google-services.json`
   - Place it in the `app/` directory
4. Enable the following Firebase services:
   - **Authentication** → Email/Password
   - **Cloud Firestore** → Start in test mode, then apply `firestore.rules`
   - **Cloud Storage** → Apply `storage.rules`
   - **Cloud Messaging** → No extra setup needed

## How to Run

1. Clone the repository
2. Complete Firebase setup above
3. Open in Android Studio (Hedgehog or newer)
4. Run on emulator (API 24+) or physical device

## Firestore Collections Structure

```
users/{uid}
  name, email, photoUrl, role, country, city, currency, language,
  lastSeen, isOnline, fcmToken, createdAt

horses/{horseId}
  horseId, ownerId, name, age, type, description, photoUrl, createdAt

services/{serviceId}
  serviceId, providerId, name, description, price, category, location, createdAt

bookings/{bookingId}
  bookingId, ownerId, providerId, horseId, serviceId, trainerId,
  datetime, status (pending|confirmed|rejected|completed), createdAt

conversations/{conversationId}
  conversationId, memberIds[], lastMessage, lastMessageAt, unreadCounts{}
  └── messages/{messageId}
        messageId, senderId, type (text|image), text, imageUrl, sentAt, seenBy[]
```

## Cloud Functions (Optional)

For production, deploy Cloud Functions to:
- Send FCM notifications when a new message or booking update occurs
- Clean up stale data

## Security

Security rules are defined in:
- `firestore.rules` – Firestore access control
- `storage.rules` – Firebase Storage access control

Deploy with:
```bash
firebase deploy --only firestore:rules,storage
```

---
*Original README below*

CrownHorse 
int
