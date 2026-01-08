package com.smartbox.smart_delivery_box.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity

//Khi giá trị cột role là ADMIN
@DiscriminatorValue("ADMIN")
@Data
@EqualsAndHashCode(callSuper = true)

public class AdminAccount extends Account {

}
