package com.example.trading_system;

import lombok.Getter;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
//@EnableTransactionManagement
//@EntityScan(basePackages = {"com.example.trading_system", "com.example.trading_system.domain"})
//@EnableJpaRepositories(basePackages = {"com.example.trading_system", "com.example.trading_system.domain"})
public class TradingSystemApplication {
    private static ConfigurableApplicationContext context;
    public static void main(String[] args) {
        context = SpringApplication.run(TradingSystemApplication.class, args);
    }
    public static void restart() {
        ApplicationArguments args = context.getBean(ApplicationArguments.class);

        Thread thread = new Thread(() -> {
            context.close();
            context = SpringApplication.run(TradingSystemApplication.class, args.getSourceArgs());
        });

        thread.setDaemon(false);
        thread.start();
    }

    public static ConfigurableApplicationContext getContext() {
        return context;
    }
}
