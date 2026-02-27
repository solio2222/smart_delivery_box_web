package com.smartbox.smart_delivery_box.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

import com.smartbox.smart_delivery_box.Enum.SmartBoxStatus;
import com.smartbox.smart_delivery_box.entity.*;

@Repository
public interface SmartBoxRepository extends JpaRepository<SmartBox, Long> {

    // Tìm tất cả các tủ ĐANG TRỐNG (nghĩa là cột order_id trong database mang giá
    // trị null)
    List<SmartBox> findByOrderIsNull();

    // Tìm xem đơn hàng cụ thể này đang nằm ở cái tủ nào

    Optional<SmartBox> findByMACAddress(String MACAddress);

    // Nếu ID của đơn hàng là kiểu Long
    List<SmartBox> findByOrder_Id(Long orderId);

    List<SmartBox> findByStatus(SmartBoxStatus status);
}
