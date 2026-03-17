package com.smartbox.smart_delivery_box.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.smartbox.smart_delivery_box.Enum.SmartBoxStatus;
import com.smartbox.smart_delivery_box.entity.SmartBox;
import com.smartbox.smart_delivery_box.repository.SmartBoxRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SmartBoxService {

    private final SmartBoxRepository smartBoxRepository;

    public List<SmartBox> getAvailableSmartBoxes() {
        return smartBoxRepository.findByOrderIsNull();
    }

    public SmartBox getSmartBoxById(Long id) {
        return smartBoxRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tủ số: " + id));
    }

    public List<SmartBox> getSmartBoxByOrderId(Long orderId) {
        return smartBoxRepository.findByOrder_Id(orderId);
    }

    public ArrayList<Long> getAvailableBoxIDs() {
        List<SmartBox> availableBoxes = getAvailableSmartBoxes();
        ArrayList<Long> boxIDs = new ArrayList<>();
        for (SmartBox box : availableBoxes) {
            boxIDs.add(box.getId());
        }
        return boxIDs;
    }

    public void updateSmartBox(List<SmartBox> boxes) {
        smartBoxRepository.saveAll(boxes);
    }

    public void updateSmartBox(SmartBox box) {
        smartBoxRepository.save(box);
    }

    @Transactional
    public void closeBox(Long boxId) {
        SmartBox box = getSmartBoxById(boxId);
        if (box.getStatus() != SmartBoxStatus.WAITING) {
            throw new RuntimeException("Tủ số " + boxId + " không ở trạng thái WAITING, không thể xác nhận đóng cửa!");
        }
        box.setStatus(SmartBoxStatus.OCCUPIED); 
        smartBoxRepository.save(box);
    }

    public List<SmartBox> getWaitingSmartBoxes() {
        return smartBoxRepository.findByStatus(SmartBoxStatus.WAITING);
    }

    // Dừng để cancel từng tủ trong lúc quét timeout
    public void cancelBox(Long boxId) {
        SmartBox box = getSmartBoxById(boxId);
        box.setStatus(SmartBoxStatus.FREE);
        box.setOrder(null);
        smartBoxRepository.save(box);
    }
}