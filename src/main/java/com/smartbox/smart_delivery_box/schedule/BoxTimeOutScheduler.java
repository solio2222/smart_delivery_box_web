package com.smartbox.smart_delivery_box.schedule;

import com.smartbox.smart_delivery_box.entity.DeliveryOrder;
import com.smartbox.smart_delivery_box.entity.SmartBox;
import com.smartbox.smart_delivery_box.service.DeliveryService;
import com.smartbox.smart_delivery_box.service.MQTTService;
import com.smartbox.smart_delivery_box.service.SmartBoxService;
import lombok.RequiredArgsConstructor;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BoxTimeOutScheduler {
    private final SmartBoxService smartBoxService;
    private final DeliveryService deliveryService;
    private final MQTTService mqttService;

    @Scheduled(fixedRate = 60000) // Chạy mỗi 1 phút
    @Transactional
    public void scanBoxTimeOut() {
        List<SmartBox> waitingBoxes = smartBoxService.getWaitingSmartBoxes();
        LocalDateTime now = LocalDateTime.now();

        for (SmartBox box : waitingBoxes) {
            DeliveryOrder order = box.getOrder();
            if (order != null && order.getCreatedAt().plusMinutes(5).isBefore(now)) {

                smartBoxService.cancelBox(box.getId());
                deliveryService.minusBoxesFromOrder(1L, order);

                //Unmark các tủ bị timeout
                mqttService.openBoxes(List.of(box.getId()));
            }
        }
        System.out.println(waitingBoxes.size() + " tủ đang chờ đã được quét vào " + now);
    }
}
