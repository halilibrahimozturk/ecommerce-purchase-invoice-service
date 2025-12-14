package com.emlakjet.purchaseinvoiceservice.repository;

import com.emlakjet.purchaseinvoiceservice.model.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, String> {

}