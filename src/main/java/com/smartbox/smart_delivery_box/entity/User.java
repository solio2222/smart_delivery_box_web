package com.smartbox.smart_delivery_box.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class User {
    private String id;
    private String name;
    private String email;
    private String phoneNumber;
}
