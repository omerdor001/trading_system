package com.example.trading_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@ComponentScan(basePackages = {"com.example.trading_system", "com.example.trading_system.domain.users"})
public class TradingSystemApplication {
	public static void main(String[] args) {
		SpringApplication.run(TradingSystemApplication.class, args);
	}
}
