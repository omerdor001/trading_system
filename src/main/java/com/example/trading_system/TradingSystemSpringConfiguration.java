package com.example.trading_system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TradingSystemSpringConfiguration {
    private static final Logger logger = LoggerFactory.getLogger( TradingSystemSpringConfiguration.class );
    static {
        logger.trace("Starting TradingSystemSpringConfiguration");
//        OpenCV.loadShared();
        logger.info("OpenCV is Loaded.");
    }

    @Value("${server.host:localhost}")
    private String serverHost;
    @Value("${server.port:8082}")
    private String serverPort;



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



//    @Bean
//    public MyServiceOrClass myService() {
//        return new MyServiceOrClassImpl();
//    }

    // Other @Bean definitions can follow here


}
