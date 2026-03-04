package com.smartbox.smart_delivery_box.schedule;

import com.smartbox.smart_delivery_box.entity.DeliveryOrder;
import com.smartbox.smart_delivery_box.entity.SmartBox;
import com.smartbox.smart_delivery_box.Enum.OrderStatus;
import com.smartbox.smart_delivery_box.Enum.SmartBoxStatus;
import com.smartbox.smart_delivery_box.repository.DeliveryOrderRepository;
import com.smartbox.smart_delivery_box.service.MailService;
import com.smartbox.smart_delivery_box.service.SmartBoxService;
import lombok.RequiredArgsConstructor;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderScheduler {

    private final DeliveryOrderRepository orderRepository;
    private final SmartBoxService smartBoxService;
    private final MailService mailService;

    /*
     * Method này được dùng để quét các order
     * Nếu
     */
    @Scheduled(fixedRate = 15000)
    @Transactional
    public void reconcileOrderStatus() {

        // 1. Lấy tất cả các đơn hàng đang chờ Shipper cất đồ (PENDING)
        // Lưu ý: Cần có hàm findByStatus trong DeliveryOrderRepository
        List<DeliveryOrder> pendingOrders = orderRepository.findByStatus(OrderStatus.PENDING);

        if (pendingOrders.isEmpty())
            return;

        for (DeliveryOrder order : pendingOrders) {
            // Lấy tất cả các tủ thuộc đơn hàng này
            List<SmartBox> boxes = smartBoxService.getSmartBoxByOrderId(order.getId());

            // Đếm xem có bao nhiêu tủ đã được đóng an toàn (OCCUPIED)
            long occupiedBoxesCount = boxes.stream()
                    .filter(b -> b.getStatus() == SmartBoxStatus.OCCUPIED)
                    .count();

            // Nếu số tủ an toàn bằng với tổng số tủ cần thiết -> CHỐT ĐƠN!
            Long totalItems = order.getTotalItems();
            if (occupiedBoxesCount == totalItems && totalItems > 0) {

                order.setStatus(OrderStatus.HOLDING);
                orderRepository.save(order);

                System.out.println("🔄 [Scheduler] Đơn hàng " + order.getId()
                        + " đã đủ số lượng tủ đóng. Chuyển trạng thái sang HOLDING!");

                mailService.sendOrderConfirmEmail(order.getUser().getEmail(), boxes, order.getOtp());
            }
        }
        System.out.println(pendingOrders.size() + " đơn hàng đã được quét vào " + java.time.LocalDateTime.now());
    }
}