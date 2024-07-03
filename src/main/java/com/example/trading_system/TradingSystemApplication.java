package com.example.trading_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class TradingSystemApplication {
	public static void main(String[] args) {
		SpringApplication.run(TradingSystemApplication.class, args);
	}
}
