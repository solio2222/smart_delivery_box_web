package com.smartbox.smart_delivery_box.entity;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//Khai báo các phương thức getter, setter, toString, hashCode, equals
@Data

//Khai báo constructor
@AllArgsConstructor
@NoArgsConstructor

//db tự tạo table
@Table(name = "user_accounts")
//Biểu thị đây là class ánh xạ với bảng trong db

@Entity

//Strategy kế thừa là single table
//Sử dụng một bảng duy nhất cho tất cả các lớp con
//Chọn column role, để phân biệt các loại tài khoản
//Dạng String
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name ="role",discriminatorType = jakarta.persistence.DiscriminatorType.STRING)

public class Account {
    @Id
    private String accountId;
    private String phoneNumber;
    private String passWord;
    private String userName;
    private String email;
}
