package com.example.trading_system;

import com.example.trading_system.domain.externalservices.*;
import com.example.trading_system.domain.stores.StoreDatabaseRepository;
import com.example.trading_system.domain.stores.StoreMemoryRepository;
import com.example.trading_system.domain.stores.StoreRepository;
import com.example.trading_system.domain.users.UserDatabaseRepository;
import com.example.trading_system.domain.users.UserMemoryRepository;
import com.example.trading_system.domain.users.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class TradingSystemSpringConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(TradingSystemSpringConfiguration.class);

    static {
        logger.info("Starting TradingSystemSpringConfiguration");
    }

    @Value("${server.host:localhost}")
    private String serverHost;
    @Value("${server.port:8082}")
    private String serverPort;
    @Value("${payment.service.name:wsep}")
    private PaymentServiceEnum paymentServiceName;
    @Value("${repository.type}")
    private String repositoryType;
    private PaymentService paymentService;



     /*
      Here you can: <br>

      1. Customization and Configuration: Many components in a Spring application can be configured and customized.
      When you use @Bean, you can customize the initialization logic, scope, and dependencies of a bean.
      This gives you fine-grained control over how beans are created and managed by Spring.<br><br>
      2. Dependency Injection Beans.
      By annotating a method with @Bean, you tell Spring that it should instantiate and manage a bean of the returned object type.<br><br>
      3. External Libraries Integration: Often, you'll use external libraries or classes that are not managed by Spring by default. To integrate these classes into the Spring application context and enable them to benefit from Spring's features such as DI and transaction management, you can use @Bean to declare them as Spring beans.
      <p>
      4. Annotation-Based Configuration: Spring Boot encourages the use of annotations for configuration. While many beans are automatically detected (like components annotated with @Component, @Service, etc.),
      there are cases where you want to explicitly define a bean. @Bean allows you to do this in a straightforward and declarative manner.<br><br>

      5. Testing: In unit testing and integration testing scenarios, you may want to replace certain beans with mock objects or stubs.
      By explicitly declaring beans with @Bean, you can easily swap out real implementations with mocks or stubs in your test configurations using @Primary, @Qualifier, or profiles.
      <br>
      example<br>
        <code>  @Bean
          public MyService myService() {
              return new MyServiceImpl();
          }</code>
     */

    @Bean
    public PaymentService getPaymentService() {
        if (paymentServiceName == PaymentServiceEnum.wsep) {
            paymentService = new WsepClient();
        } else if (paymentServiceName == PaymentServiceEnum.proxy) {
            paymentService = new PaymentServiceProxy();
        } else {
            paymentService = new PaymentServiceProxy();
        }
        return paymentService;
    }

    @Bean
    @Primary
    public StoreRepository getStoreRepository() {
        if ("database".equalsIgnoreCase(repositoryType)) {
            return new StoreDatabaseRepository();
        } else if ("memory".equalsIgnoreCase(repositoryType)) {
            return new StoreMemoryRepository();
        } else throw new IllegalArgumentException("Invalid repository type in config");
    }

    @Bean
    @Primary
    public UserRepository getUserRepository() {
        if ("database".equalsIgnoreCase(repositoryType)) {
            return new UserDatabaseRepository();
        } else if ("memory".equalsIgnoreCase(repositoryType)) {
            return new UserMemoryRepository();
        } else throw new IllegalArgumentException("Invalid repository type in config");
    }

//    @Bean
//    public TradingSystemImp tradingSystemImp( DeliveryService deliveryService, NotificationSender notificationSender, UserRepository userRepository, StoreRepository storeRepository) {
//        if (paymentServiceName == PaymentServiceEnum.wsep){
//            paymentService = new WsepClient();
//        }
//        TradingSystemImp.instance= new TradingSystemImp(paymentService, deliveryService, notificationSender, userRepository, storeRepository);
//        return TradingSystemImp.instance;
//    }

    // Other @Bean definitions can follow here
}