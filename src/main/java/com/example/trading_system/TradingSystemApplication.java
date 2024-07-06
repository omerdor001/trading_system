package com.example.trading_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EntityScan(basePackages = {"com.example.trading_system", "com.example.trading_system.domain"})
@EnableJpaRepositories(basePackages = {"com.example.trading_system", "com.example.trading_system.domain"})
public class TradingSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(TradingSystemApplication.class, args);
    }
}
