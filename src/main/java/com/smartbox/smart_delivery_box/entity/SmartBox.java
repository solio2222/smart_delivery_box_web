package com.smartbox.smart_delivery_box.entity;

import com.smartbox.smart_delivery_box.Enum.SmartBoxStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "smart_boxes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmartBox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", referencedColumnName = "id", nullable = true)
    private DeliveryOrder order;

    @Column(name = "mac_address", nullable = false, unique = true)
    private String MACAddress;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private SmartBoxStatus status = SmartBoxStatus.FREE; // Mặc định là FREE khi tạo mới
}