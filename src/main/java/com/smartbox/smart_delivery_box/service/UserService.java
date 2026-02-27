package com.smartbox.smart_delivery_box.service;

import com.smartbox.smart_delivery_box.entity.User;
import com.smartbox.smart_delivery_box.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {


        private final UserRepository userRepository;
        
        
        public boolean checkPhoneNumberExist(String  phoneNumber) {
            return userRepository.existsByPhoneNumber(phoneNumber);
        } 


        public User getUserByPhoneNumber(String phoneNumber) {
            return userRepository.findByPhoneNumber(phoneNumber)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }
}
