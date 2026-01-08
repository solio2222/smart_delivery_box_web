package com.smartbox.smart_delivery_box.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
//Khi giá trị cột role là user
@DiscriminatorValue("USER")
@Data
@EqualsAndHashCode(callSuper = true)
public class UserAccount extends Account {

}
