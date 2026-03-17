package com.smartbox.smart_delivery_box.Enum;

public enum SmartBoxStatus {
    WAITING,   // Chờ shipper cất đồ vào đóng cửa
    OCCUPIED, // Sau khi đã nhận tính hiệu từ esp
    FREE      // Tủ đang trống, chưa có đơn hàng nào gán vào
}
