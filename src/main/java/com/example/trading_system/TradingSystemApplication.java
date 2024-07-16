package com.example.trading_system;

import com.example.trading_system.service.TradingSystemImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class })

//@EnableTransactionManagement
//@EntityScan(basePackages = {"com.example.trading_system", "com.example.trading_system.domain"})
//@EnableJpaRepositories(basePackages = {"com.example.trading_system", "com.example.trading_system.domain"})
public class TradingSystemApplication {
    @Autowired
    private TradingSystemInitializer initializationService;
    public static void main(String[] args) {
        SpringApplication.run(TradingSystemApplication.class, args);
    }

    @Bean
    public void initializeSystem() {
        initializationService.initializeSystem();
    }
}


