package com.smartbox.smart_delivery_box.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import com.smartbox.smart_delivery_box.entity.*;
import com.smartbox.smart_delivery_box.Enum.*;
@Repository
public interface DeliveryOrderRepository extends JpaRepository<DeliveryOrder, Long> {
    
    List<DeliveryOrder> findByUserId(Long userId);
    
    List<DeliveryOrder> findByStatus(OrderStatus status);

    DeliveryOrder findByOtp(String otp);

    boolean existsByOtp(String otp);
}