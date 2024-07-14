package com.example.trading_system.DBTests;
import com.example.trading_system.TradingSystemApplication;
import com.example.trading_system.TradingSystemRestController;
import com.example.trading_system.domain.stores.StoreRepository;
import com.example.trading_system.domain.users.UserRepository;;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = TradingSystemApplication.class
)
@TestPropertySource(
        locations = "classpath:application.properties"
)
public class ImmunityTest {
    @Autowired
    private TradingSystemRestController tradingSystemRestController;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StoreRepository storeRepository;

    @BeforeEach
    void setUp() {
        if (TradingSystemApplication.getContext() == null) {
            TradingSystemApplication.main(new String[]{});
        }
    }

    @Test
    void reopen_successUser() {
        tradingSystemRestController.getTradingSystem().register("admin", "Admin123", LocalDate.now());
        tradingSystemRestController.openSystem();
        tradingSystemRestController.getTradingSystem().register("user1", "User111", LocalDate.now());
        tradingSystemRestController.getTradingSystem().register("user2", "User222", LocalDate.now());
        TradingSystemApplication.restart();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ConfigurableApplicationContext context = TradingSystemApplication.getContext();
        tradingSystemRestController = context.getBean(TradingSystemRestController.class);
        userRepository = context.getBean(UserRepository.class);
        storeRepository = context.getBean(StoreRepository.class);
        tradingSystemRestController.getTradingSystem().register("user3", "User333", LocalDate.now());
    }
}
