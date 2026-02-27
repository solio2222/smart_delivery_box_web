package com.smartbox.smart_delivery_box.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import com.smartbox.smart_delivery_box.entity.*;
import com.smartbox.smart_delivery_box.Enum.*;
@Repository
public interface DeliveryOrderRepository extends JpaRepository<DeliveryOrder, Long> {
    
    // Tìm tất cả các đơn hàng của một user cụ thể
    List<DeliveryOrder> findByUserId(Long userId);
    
    // Tìm các đơn hàng theo trạng thái (ví dụ: lấy ra các đơn đang PENDING)
    List<DeliveryOrder> findByStatus(OrderStatus status);

    // Tìm đơn hàng theo OTP (dùng để kiểm tra khi mở tủ)
    DeliveryOrder findByOtp(String otp);

    // Kiểm tra xem OTP đã tồn tại chưa (khi tạo đơn mới)
    boolean existsByOtp(String otp);
}