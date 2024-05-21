package com.example.trading_system.Domain.externalservices;

import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/proxy")
public class PaymentService extends Service{

    public PaymentService(String serviceName,RestTemplate restTemplate) {
        super(serviceName,restTemplate);
    }


}


