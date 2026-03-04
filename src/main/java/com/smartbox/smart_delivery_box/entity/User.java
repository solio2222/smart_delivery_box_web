package com.smartbox.smart_delivery_box.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    private String password;

    // Quan hệ 1 User có thể có nhiều DeliveryOrders
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL /* Xóa user thì xóa các DeliveryOrders liên quan */, fetch = FetchType.LAZY)
    private List<DeliveryOrder> deliveryOrders;

    @Column(name = "email", nullable = false, unique = true)
    private String email;
}