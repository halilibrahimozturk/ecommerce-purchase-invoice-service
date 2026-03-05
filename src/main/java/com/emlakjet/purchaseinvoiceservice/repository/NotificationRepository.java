package com.ozturk.purchaseinvoiceservice.repository;

import com.ozturk.purchaseinvoiceservice.model.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, String> {

}