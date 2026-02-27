package com.smartbox.smart_delivery_box.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.smartbox.smart_delivery_box.Enum.*;

@Entity
@Table(name = "delivery_orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Trạng thái đơn hàng: PENDING, PROCESSED
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "otp", nullable = true, length = 6)
    private String otp;

    // Khóa ngoại liên kết với bảng User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Tự động gán thời gian tạo khi insert vào database
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Column(name = "total_items", nullable = false)
    private long totalItems;
}
