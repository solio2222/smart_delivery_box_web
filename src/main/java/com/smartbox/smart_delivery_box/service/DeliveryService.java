package com.smartbox.smart_delivery_box.service;

import com.smartbox.smart_delivery_box.entity.DeliveryOrder;
import com.smartbox.smart_delivery_box.entity.SmartBox;
import com.smartbox.smart_delivery_box.Enum.OrderStatus;
import com.smartbox.smart_delivery_box.Enum.SmartBoxStatus;
import com.smartbox.smart_delivery_box.repository.DeliveryOrderRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryOrderRepository orderRepository;
    private final SmartBoxService smartBoxService;
    private final UserService userService;
    private final MQTTService mqttService;

    /**
     * TẠO ĐƠN HÀNG MỚI (Dành cho Shipper)
     * - Check các boxes free
     * - Tạo mã OTP
     * - Lưu đơn hàng vào Database
     * - Gọi gửi lệnh xuống esp32
     */
    @Transactional
    public DeliveryOrder createNewOrder(String phoneNumber, List<Long> boxIDs) {
        if (!userService.checkPhoneNumberExist(phoneNumber)) {
            throw new RuntimeException("User not found");
        }

        List<SmartBox> boxes = new ArrayList<>();
        for (Long boxID : boxIDs) {
            SmartBox box = smartBoxService.getSmartBoxById(boxID);
            if (box.getStatus() != SmartBoxStatus.FREE) {
                throw new RuntimeException("Tủ số " + boxID + " không trống, vui lòng chọn tủ khác!");
            }
            boxes.add(box);
        }

        String otp;
        Random random = new Random();
        do {
            otp = String.format("%06d", random.nextInt(1000000));
        } while (orderRepository.existsByOtp(otp));

        DeliveryOrder order = new DeliveryOrder();
        order.setUser(userService.getUserByPhoneNumber(phoneNumber));
        order.setOtp(otp);
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setTotalItems(boxes.size());

        DeliveryOrder savedOrder = orderRepository.save(order);

        for (SmartBox box : boxes) {
            box.setOrder(savedOrder);
            box.setStatus(SmartBoxStatus.WAITING);
        }
        smartBoxService.updateSmartBox(boxes);

        // GỌI HÀM ĐÁNH DẤU TỪ MQTT SERVICE ĐỂ ESP32 BIẾT SHIPPER ĐÃ ĐẶT HÀNG VÀ
        mqttService.markBoxes(boxIDs);

        return savedOrder;
    }

    /**
     * KHÁCH LẤY HÀNG (Nhập OTP)
     * - Xác thực OTP
     * - THay đổi trạng thái đơn hàng
     * - Gọi lệnh mở tủ xuống ESP32
     * - Dọn dẹp tủ sau khi khách lấy hàng
     */
    @Transactional
    public DeliveryOrder verifyOtp(String inputOtp) {
        DeliveryOrder order = orderRepository.findByOtp(inputOtp);

        if (order == null || order.getStatus() != OrderStatus.HOLDING) {
            return null;
        }

        order.setStatus(OrderStatus.PROCESSED);
        order.setCompletedAt(LocalDateTime.now());
        order.setOtp(null);
        orderRepository.save(order);

        List<SmartBox> boxesToClear = smartBoxService.getSmartBoxByOrderId(order.getId());

        // MỞ CỬA CHO KHÁCH LẤY ĐỒ
        mqttService.openBoxes(boxesToClear.stream().map(SmartBox::getId).toList());

        for (SmartBox box : boxesToClear) {
            box.setOrder(null);
            box.setStatus(SmartBoxStatus.FREE);
        }
        smartBoxService.updateSmartBox(boxesToClear);

        return order;
    }

    /*
     * Dùng để cancel các tủ thuộc order
    * - Nếu đơn hàng không còn tủ thì cancel
     */
    @Transactional
    public void minusBoxesFromOrder(Long numOfBoxes, DeliveryOrder order) {
        long totalItems = order.getTotalItems();
        if (numOfBoxes > totalItems) {
            throw new RuntimeException("Số lượng tủ cần hủy vượt quá tổng số tủ trong đơn hàng!");
        }
        if (numOfBoxes == totalItems) {
            cancelOrder(order);
        } else {
            order.setTotalItems(totalItems - numOfBoxes);
            orderRepository.save(order);
        }
    }

    // Dùng để cancel đơn hàng
    @Transactional
    public void cancelOrder(DeliveryOrder order) {
        order.setStatus(OrderStatus.CANCELED);
        order.setOtp(null);
        orderRepository.save(order);
    }
}