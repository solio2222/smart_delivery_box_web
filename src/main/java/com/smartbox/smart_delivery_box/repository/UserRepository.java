package com.smartbox.smart_delivery_box.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import com.smartbox.smart_delivery_box.entity.*;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Spring Boot sẽ tự động dịch tên hàm này thành câu SQL: 
    // SELECT * FROM users WHERE phone_number = ?
    // Dùng Optional để tránh lỗi NullPointerException nếu không tìm thấy user
    Optional<User> findByPhoneNumber(String phoneNumber);
    
    // Kiểm tra xem số điện thoại đã tồn tại chưa (khi đăng ký)
    boolean existsByPhoneNumber(String phoneNumber);
}