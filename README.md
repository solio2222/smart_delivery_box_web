# 📦 Smart Delivery Box System

An IoT-enabled automated parcel locker system designed to solve last-mile delivery challenges. This system provides a secure, self-service, and asynchronous delivery experience using a robust client-server architecture combined with edge computing.

## 🚀 About The Project

In the fast-growing e-commerce landscape, missed deliveries and parcel theft are major issues. The **Smart Delivery Box System** bridges the gap between shippers and receivers. It allows delivery personnel to securely deposit parcels into automated lockers, while generating a one-time password (OTP) for receivers to retrieve their items at their convenience. 

## ✨ Key Features

* **Secure OTP Authentication:** 6-digit OTP verification for parcel retrieval.
* **Real-time Telemetry:** Instant status updates of the physical doors using MQTT.
* **Deadlock Prevention (Auto-Reclaim):** An automated Spring Boot scheduler that reclaims "WAITING" boxes back to "FREE" if the user fails to physically close the door within 1 minute, preventing resource lockups.
* **Order Reconciliation:** Background processes to automatically finalize multi-box drop-offs once all physical sensors confirm closure.
* **Encrypted Communication:** Data is routed through an HTTPS Ngrok tunnel and MQTT over TLS (port 8883) for maximum security.

## 🏗️ System Architecture

The project follows a 3-tier IoT architecture:
1. **Application Layer:** A mobile-responsive Web Kiosk for shippers to initiate drop-offs and receivers to input OTPs.
2. **Data Processing Layer:** A centralized backend managing business logic, state transitions, and database interactions.
3. **Perception & Edge Control Layer:** Microcontrollers handling sensor data and actuating physical locking mechanisms.

## 💻 Tech Stack & Hardware

**Software Backend:**
* Java Spring Boot (REST APIs, Schedulers, Business Logic)
* Spring Data JPA (Hibernate)
* MySQL (Relational Database)
* MQTT (HiveMQ Cloud Broker)
* Ngrok (Secure Localhost Tunneling)

**Hardware Components:**
* **Arduino Wemos D1 R2:** Primary edge microcontroller with built-in Wi-Fi.
* **SG90 Micro Servo (180°):** Actuators used as the locking mechanism for the compartments.
* **Magnetic Reed Switches:** Proximity sensors to detect the physical open/closed state of the doors.
* **5V External Power Supply:** Dedicated power module to isolate servo peak currents from the logic board.

## 👥 Authors

* **Nguyễn Minh Khang** - *Computer Networks and Data Communications, Eastern International University*
* **Nguyễn Thị Ngọc Mỹ** - *Computer Networks and Data Communications, Eastern International University*

---
*Developed as a project for the School of Computing and Information Technology.*
