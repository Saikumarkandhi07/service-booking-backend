package com.service_booking_backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.razorpay.RazorpayClient;

@Configuration
public class RazorpayConfig {

	@Bean
    public RazorpayClient razorpayClient() throws Exception {
        return new RazorpayClient(
            "rzp_test_Roy5sDssjmHqD5",     // key_id
            "mdQodrIYA3mA9ky980AI53Le"    // key_secret
        );
    }
}
