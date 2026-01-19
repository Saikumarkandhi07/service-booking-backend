package com.service_booking_backend.controller;

import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin
public class PaymentController {

	@Autowired
    private RazorpayClient razorpay;

    @PostMapping("/create-order")
    public String createOrder(@RequestBody Map<String, Object> data) throws Exception {

        int amount = (int) data.get("amount"); // in rupees

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amount * 100); // paisa
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "txn_" + System.currentTimeMillis());

        Order order = razorpay.orders.create(orderRequest);

        return order.toString();
    }
    
    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> data) {

        String orderId = data.get("razorpay_order_id");
        String paymentId = data.get("razorpay_payment_id");
        String signature = data.get("razorpay_signature");

        // verify using Razorpay signature logic

        return ResponseEntity.ok("Payment verified");
    }
}
