package com.stabilityos.backend.delivery;

public interface NotificationDeliveryService {

    void send(String title, String message);
}