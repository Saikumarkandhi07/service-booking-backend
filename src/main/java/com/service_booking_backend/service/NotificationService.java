package com.service_booking_backend.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.service_booking_backend.entity.Notification;
import com.service_booking_backend.entity.User;
import com.service_booking_backend.repository.NotificationRepository;

@Service
public class NotificationService {

    private static final String EXPO_URL =
            "https://exp.host/--/api/v2/push/send";

    @Autowired
    private NotificationRepository notificationRepository;

    /* ================= EXISTING METHOD (KEEP AS IS) ================= */

    public void sendNotification(String pushToken, String title, String body) {

        if (pushToken == null || pushToken.isEmpty()) {
            return;
        }

        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> payload = new HashMap<>();
        payload.put("to", pushToken);
        payload.put("title", title);
        payload.put("body", body);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(payload, headers);

        restTemplate.postForEntity(EXPO_URL, entity, String.class);
    }

    /* ================= NEW METHOD (ADD THIS) ================= */

    public void notifyUser(User user, String title, String message) {

        // 1️⃣ Save notification in DB
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notificationRepository.save(notification);

        // 2️⃣ Send push notification
        sendNotification(user.getPushToken(), title, message);
    }
}
