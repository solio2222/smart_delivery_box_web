package com.smartbox.smart_delivery_box.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MQTTService {
    
    private final SmartBoxService smartBoxService; 
    
    private MqttClient client;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String brokerUrl = "tcp://broker.hivemq.com:1883";
    private final String clientId = "SpringBootServer_" + UUID.randomUUID().toString();
    private final String topicCommand = "csn302/smartbox/command";
    private final String topicStatus = "csn302/smartbox/status";

    // ========================================================
    // 1. HÀM KHỞI TẠO: Chỉ lo việc kết nối và đăng ký kênh
    // ========================================================
    @PostConstruct
    public void initMQTT() {
        try {
            client = new MqttClient(brokerUrl, clientId);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true); 
            options.setCleanSession(true);

            client.connect(options);
            System.out.println("🚀 Spring Boot đã kết nối MQTT thành công!");

            // Chỉ định rõ ràng: "Khi có tin nhắn tới, hãy giao cho hàm handleIncomingMessage xử lý!"
            client.subscribe(topicStatus, this::handleIncomingMessage);

        } catch (MqttException e) {
            System.err.println("Lỗi kết nối MQTT: " + e.getMessage());
        }
    }

    // ========================================================
    // 2. HÀM LẮNG NGHE: Chuyên gia mổ xẻ JSON và xử lý logic
    // ========================================================
    private void handleIncomingMessage(String topic, MqttMessage message) {
        String payload = new String(message.getPayload());
        System.out.println("📥 ESP32 báo cáo lên kênh [" + topic + "]: " + payload);

        try {
            JsonNode jsonNode = objectMapper.readTree(payload);
            String event = jsonNode.get("event").asText();

            // Nếu sau này có nhiều event, bạn có thể dùng switch-case ở đây cho đẹp
            if ("door_locked".equals(event)) {
                Long boxId = jsonNode.get("box").asLong();
                smartBoxService.closeBox(boxId);
                System.out.println("✅ Đã cập nhật Tủ " + boxId + " thành OCCUPIED.");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Lỗi parse JSON từ ESP32: " + e.getMessage());
        }
    }

    // ========================================================
    // 3. CÁC HÀM PHÁT LỆNH: Chuyên gia gửi tín hiệu xuống ESP32
    // ========================================================
    public void sendCommand(String cmd, List<Long> boxIds) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("cmd", cmd);
            data.put("boxes", boxIds);
            String jsonPayload = objectMapper.writeValueAsString(data);

            MqttMessage message = new MqttMessage(jsonPayload.getBytes());
            message.setQos(1); // Không cần 2 vì code arduino không bị lỗi bởi tin nhắn trùng.
            client.publish(topicCommand, message);
            
            System.out.println("📤 Đã gửi lệnh xuống ESP32: " + jsonPayload);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void markBoxes(List<Long> boxIDs) { sendCommand("mark", boxIDs); }
    public void openBoxes(List<Long> boxIDs) { sendCommand("open", boxIDs); }
}