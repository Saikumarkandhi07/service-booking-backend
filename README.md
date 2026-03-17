# 🚀 Service Booking Backend API

## 📌 Project Overview

The **Service Booking Application** is a backend system built using **Spring Boot** that allows users to browse services, make bookings, and manage their activities.

This project simulates real-world service platforms where users can book services efficiently with secure authentication and scalable APIs.

---

## 🛠️ Tech Stack

* **Java 17**
* **Spring Boot**
* **Spring Data JPA (Hibernate)**
* **MySQL**
* **Spring Security (JWT)**
* **Maven**
* **Git & GitHub**

---

## ✨ Key Features

* 🔐 Secure User Authentication & Authorization (JWT)
* 👤 User Registration & Login
* 🛎️ Service Management (Add / Update / Delete)
* 📅 Booking System (Create / Cancel / View)
* 📊 Booking History Tracking
* 🔄 RESTful API Architecture
* ⚡ Clean & Scalable Code Structure

---

## 📂 Project Structure

```
com.servicebooking
│
├── controller      # API endpoints
├── service         # Business logic
├── repository      # Database operations
├── entity          # Database models
├── dto             # Request/Response objects
├── config          # Security & configuration
```

---

## 🔗 API Endpoints

### 🔐 Authentication

* `POST /api/auth/register`
* `POST /api/auth/login`

### 🛎️ Services

* `GET /api/services`
* `POST /api/services`
* `PUT /api/services/{id}`
* `DELETE /api/services/{id}`

### 📅 Bookings

* `POST /api/bookings`
* `GET /api/bookings/user/{userId}`
* `DELETE /api/bookings/{id}`

---

## 🗄️ Database Design

* MySQL Database
* Tables:

  * Users
  * Services
  * Bookings

---

## ▶️ How to Run the Project

### 1️⃣ Clone Repository

```bash
git clone https://github.com/Saikumarkandhi07/service-booking-backend.git
```

### 2️⃣ Configure Database

Update `application.properties`:

```
spring.datasource.url=jdbc:mysql://localhost:3306/service_booking
spring.datasource.username=root
spring.datasource.password=yourpassword
```

### 3️⃣ Run Application

```bash
mvn spring-boot:run
```

### 4️⃣ Access APIs

```
http://localhost:8080
```

---

## 🧪 API Testing

Use tools like:

* Postman

---

## 📌 Future Enhancements

* 💳 Payment Integration
* 🔔 Notification System
* 📱 Mobile App Integration (React Native)
* 🌐 Cloud Deployment (Render)
* ⭐ Ratings & Reviews

---

## 👨‍💻 Author

**Sai Kumar Kandhi**

* Full Stack Java Developer
* Spring Boot | React Native | MySQL
* 📧 [saikumarkandhi07@gmail.com](mailto:saikumarkandhi07@gmail.com)

---

## ⭐ Support

If you like this project, give it a ⭐ on GitHub!
