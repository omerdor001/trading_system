package com.example.trading_system;

import com.example.trading_system.service.TradingSystemImp;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
public class TradingSystemApplication {
    @Autowired
    private TradingSystemInitializer initializationService;
    public static void main(String[] args) {
        SpringApplication.run(TradingSystemApplication.class, args);
    }

    @PostConstruct
    public void initializeSystem() {
        initializationService.initializeSystem();
    }
}


