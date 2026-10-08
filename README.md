# 👕 WhatWeWear

A modern Android digital wardrobe application that helps users organize their clothing, manage their wardrobe, and build personalized outfits effortlessly.

Built with **Kotlin**, **Jetpack Compose**, **Firebase**, **Hilt**, **MVVM**, and **Clean Architecture**.

---

## ✨ Features

### 🔐 Authentication

- Splash Screen
- Onboarding
- Email & Password Login
- Email & Password Signup
- Forgot Password
- Google Sign-In
- Auto Login
- Firebase Authentication

### 👕 Digital Wardrobe

- Add Clothing
- Clothing Image Selection
- Clothing Categories
- Color & Size Selection
- Wardrobe Category Filtering
- Clothing Details
- Edit Clothing
- Replace Clothing Image
- Delete Clothing
- Persistent Clothing Data with Firestore

### 🏠 Home

- Home Dashboard
- Weather Section
- Curated Looks
- Sustainability Score
- Pro Styling Tip
- AI Style Assistant Entry Point

---

## 🏗 Architecture

The project follows a clean and scalable Android architecture:

```text
Jetpack Compose UI
        ↓
    ViewModel
        ↓
   Repository
        ↓
 Firebase / Local Data
