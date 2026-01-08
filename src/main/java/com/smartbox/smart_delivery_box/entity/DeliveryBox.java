package com.smartbox.smart_delivery_box.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "delivery_boxes")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class DeliveryBox {
    @Id
    private String boxId;
    private boolean isLocked;
    private boolean isOcupied;
    private LocalDate lastStoreDate;
}
