package com.smartbox.smart_delivery_box.service;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.smartbox.smart_delivery_box.entity.SmartBox;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${mail.sender}")
    private String senderEmail;

    // Dùng để gửi email khi order được hoàn thành (không có tủ nào đang waiting)
    public void sendOrderConfirmEmail(String toEmail, List<SmartBox> boxes, String otp) {
        if (boxes.isEmpty()) {
            System.out.println("⚠️ Không có tủ nào được sử dụng, không gửi email!");
            return;
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(toEmail);
        message.setSubject("📦 Thông báo: Kiện hàng đã được giao đến Smart Box");
        
        String content = "Chào bạn,\n\n"
                + "Kiện hàng của bạn đã được đặt tại Tủ số ";
                for (SmartBox box : boxes) {
                    content += box.getId() + ", ";
                }
                content = content.substring(0, content.length() - 2);
                content += ".\n\n"
                        + "Mã OTP để lấy hàng là: " + otp + "\n"
                        + "Vui lòng đến Kiosk và nhập mã để nhận hàng.\n\n"
                        + "Trân trọng,\nSmart Box Service.";

        message.setText(content);
        mailSender.send(message);
        System.out.println("📧 Email thông báo đã được gửi đến: " + toEmail);
    }
}