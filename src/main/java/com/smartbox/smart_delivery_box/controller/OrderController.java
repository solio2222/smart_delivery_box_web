package com.smartbox.smart_delivery_box.controller;

import com.smartbox.smart_delivery_box.entity.DeliveryOrder;
import com.smartbox.smart_delivery_box.entity.SmartBox;
import com.smartbox.smart_delivery_box.service.DeliveryService;
import com.smartbox.smart_delivery_box.service.SmartBoxService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor // Khởi tạo Constructor tự động cho private final
public class OrderController {

    // CHỈ CẦN TIÊM 2 ÔNG SERVICE NÀY LÀ ĐỦ (Bỏ @Autowired đi)
    private final DeliveryService orderService; 
    private final SmartBoxService smartBoxService;

    // ==========================================
    // API 0: LẤY DANH SÁCH TỦ TRỐNG CHO MÀN HÌNH CHÍNH
    // Front-end sẽ gọi API này đầu tiên để vẽ các ô tủ lên màn hình
    // ==========================================
    @GetMapping("/available-boxes")
    public ResponseEntity<?> getAvailableBoxes() {
        try {
            // Gọi hàm getAvailableBoxIDs() mà bạn đã viết sẵn cực xịn trong SmartBoxService
            List<Long> availableBoxIds = smartBoxService.getAvailableBoxIDs();
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "available_boxes", availableBoxIds
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi lấy danh sách tủ: " + e.getMessage());
        }
    }

    // ==========================================
    // API 1: DÀNH CHO SHIPPER (TẠO ĐƠN & GỬI HÀNG)
    // ==========================================
    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> request) {
        try {
            // 1. Lấy dữ liệu từ Frontend gửi lên
            String phoneNumber = (String) request.get("phoneNumber");
            
            // Xử lý an toàn kiểu dữ liệu (tránh lỗi ClassCastException)
            List<Integer> rawBoxIds = (List<Integer>) request.get("boxIds");
            List<Long> boxIds = rawBoxIds.stream().map(Integer::longValue).toList();

            // 2. Tạo đơn hàng mới & Tự động gọi ESP32 (Đã được lo liệu trong Service)
            DeliveryOrder newOrder = orderService.createNewOrder(phoneNumber, boxIds);

            // 3. Trả kết quả về cho Web hiển thị
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "order_id", newOrder.getId(),
                    "message", "Đã lưu đơn hàng. Các tủ " + boxIds + " đã mở khóa, mời Shipper cất hàng!"
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Lỗi tạo đơn: " + e.getMessage()
            ));
        }
    }

    // ==========================================
    // API 2: DÀNH CHO KHÁCH HÀNG (NHẬP OTP LẤY HÀNG)
    // ==========================================
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, Object> request) {
        try {
            // 1. Lấy mã OTP khách nhập trên màn hình
            String otpInput = (String) request.get("otp");

            // 2. Xác thực OTP và tự động gọi ESP32 mở tủ (Đã lo liệu trong Service)
            DeliveryOrder order = orderService.verifyOtp(otpInput);
            
            if (order == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "error",
                        "message", "Mã OTP không hợp lệ hoặc đơn hàng chưa sẵn sàng!"
                ));
            }

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Xác thực thành công! Tủ đã mở, mời bạn nhận hàng."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Lỗi xác thực: " + e.getMessage()
            ));
        }
    }
}