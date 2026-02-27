package com.smartbox.smart_delivery_box.Enum;

public enum OrderStatus {
    PENDING,   // Chờ shipper cất đồ vào đóng cửa
    HOLDING,   // Đang ngậm đồ, chờ khách đến lấy
    PROCESSED,  // Đã lấy xong, đơn hàng kết thúc
    CANCELED  // Đơn hàng bị hủy
}
