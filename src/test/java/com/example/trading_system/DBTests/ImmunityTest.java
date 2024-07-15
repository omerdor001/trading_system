package com.example.trading_system.DBTests;
import com.example.trading_system.TradingSystemApplication;
import com.example.trading_system.TradingSystemRestController;
import com.example.trading_system.domain.stores.StoreRepository;
import com.example.trading_system.domain.users.UserRepository;;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.util.AssertionErrors.assertTrue;

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

    //data
    private String username;
    private String token;

    @BeforeEach
    void setUp() {
        if (TradingSystemApplication.getContext() == null) {
            TradingSystemApplication.main(new String[]{});
        }
    }

    @Test
    void reopen_successUserRegistration() {
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
        assertTrue("Admin is registered",userRepository.isExist("admin"));
        assertTrue("User1 is registered",userRepository.isExist("user1"));
        assertTrue("User2 is registered",userRepository.isExist("user2"));
    }

    @Test
    void reopen_successAppointOwner() {
        tradingSystemRestController.getTradingSystem().register("admin", "Admin123", LocalDate.now());
        tradingSystemRestController.openSystem();
        tradingSystemRestController.getTradingSystem().register("user1", "User111", LocalDate.now());
        String adminToken = tradingSystemRestController.getTradingSystem().enter().getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(adminToken);
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract token from JSON response");
        }
        adminToken = tradingSystemRestController.getTradingSystem().login(token, "v0", "admin", "Admin123").getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(adminToken);
            username = rootNode.get("username").asText();
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        tradingSystemRestController.getTradingSystem().openStore(username, token, "storeName", "");
        tradingSystemRestController.getTradingSystem().suggestOwner(username,token,"user1","storeName");
        String user1Token = tradingSystemRestController.getTradingSystem().enter().getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(user1Token);
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract token from JSON response");
        }
        user1Token = tradingSystemRestController.getTradingSystem().login(token, "v1", "user1", "User111").getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(user1Token);
            username = rootNode.get("username").asText();
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        tradingSystemRestController.getTradingSystem().approveOwner("user1",user1Token,"storeName","admin");
        String exitUser1=tradingSystemRestController.getTradingSystem().logout(user1Token,"user1").getBody();
        tradingSystemRestController.getTradingSystem().exit(exitUser1,"v2");
        String exitAdmin=tradingSystemRestController.getTradingSystem().logout(adminToken,"admin").getBody();
        tradingSystemRestController.getTradingSystem().exit(exitAdmin,"v3");
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
        assertTrue("User1 is owner of storeName",userRepository.getUser("user1").isOwner("storeName"));
    }

    @Test
    void reopen_successAppointManager() {
        tradingSystemRestController.getTradingSystem().register("admin", "Admin123", LocalDate.now());
        tradingSystemRestController.openSystem();
        tradingSystemRestController.getTradingSystem().register("user1", "User111", LocalDate.now());
        String adminToken = tradingSystemRestController.getTradingSystem().enter().getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(adminToken);
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract token from JSON response");
        }
        adminToken = tradingSystemRestController.getTradingSystem().login(token, "v0", "admin", "Admin123").getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(adminToken);
            username = rootNode.get("username").asText();
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        tradingSystemRestController.getTradingSystem().openStore(username, token, "storeName", "");
        tradingSystemRestController.getTradingSystem().suggestManage(username,token,"user1","storeName",true,true,false,true,true,false);
        String user1Token = tradingSystemRestController.getTradingSystem().enter().getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(user1Token);
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract token from JSON response");
        }
        user1Token = tradingSystemRestController.getTradingSystem().login(token, "v1", "user1", "User111").getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(user1Token);
            username = rootNode.get("username").asText();
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        tradingSystemRestController.getTradingSystem().approveManage("user1",user1Token,"storeName","admin",true,true,false,true,true,false);
        String exitUser1=tradingSystemRestController.getTradingSystem().logout(user1Token,"user1").getBody();
        tradingSystemRestController.getTradingSystem().exit(exitUser1,"v2");
        String exitAdmin=tradingSystemRestController.getTradingSystem().logout(adminToken,"admin").getBody();
        tradingSystemRestController.getTradingSystem().exit(exitAdmin,"v3");
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
        assertTrue("User1 is manager of storeName",userRepository.getUser("user1").isManager("storeName"));
    }

    @Test
    void reopen_successPurchaseAsVisitor() {
        tradingSystemRestController.getTradingSystem().register("admin", "Admin123", LocalDate.now());
        tradingSystemRestController.openSystem();
        tradingSystemRestController.getTradingSystem().register("user1", "User111", LocalDate.now());
        //Admin registered for opening stores and products
        String adminToken = tradingSystemRestController.getTradingSystem().enter().getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(adminToken);
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract token from JSON response");
        }
        adminToken = tradingSystemRestController.getTradingSystem().login(token, "v0", "admin", "Admin123").getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(adminToken);
            username = rootNode.get("username").asText();
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        tradingSystemRestController.getTradingSystem().openStore(username, token, "storeName", "");
        tradingSystemRestController.getTradingSystem().addProduct(username,token,1,"storeName","product1","aaa",100.0,100,5,1,"");
        tradingSystemRestController.getTradingSystem().addProduct(username,token,2,"storeName","product2","bbb",100.0,100,5,1,"");
        String exitAdmin=tradingSystemRestController.getTradingSystem().logout(adminToken,"admin").getBody();
        tradingSystemRestController.getTradingSystem().exit(exitAdmin,"v1");

        String user1Token = tradingSystemRestController.getTradingSystem().enter().getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(user1Token);
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract token from JSON response");
        }
        tradingSystemRestController.getTradingSystem().addToCart("v2",user1Token,1,"storeName",5,100.0);
        tradingSystemRestController.getTradingSystem().addToCart("v2",user1Token,2,"storeName",5,100.0);
        tradingSystemRestController.getTradingSystem().approvePurchase("v2",user1Token,"1234 Main Street, Springfield, IL, 62704-1234","100.00","USD","4111111111111111","12","2025","John Doe","123","123456789");
        tradingSystemRestController.getTradingSystem().exit(user1Token,"v2");
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
        assertTrue("Store named storeName included purchase",storeRepository.getStore("storeName").isPurchaseExist());
    }

    @Test
    void reopen_successPurchaseAsRegistered() {
        tradingSystemRestController.getTradingSystem().register("admin", "Admin123", LocalDate.now());
        tradingSystemRestController.openSystem();
        //Admin registered for opening stores and products
        String adminToken = tradingSystemRestController.getTradingSystem().enter().getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(adminToken);
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract token from JSON response");
        }
        adminToken = tradingSystemRestController.getTradingSystem().login(token, "v0", "admin", "Admin123").getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(adminToken);
            username = rootNode.get("username").asText();
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        tradingSystemRestController.getTradingSystem().openStore("r"+username, token, "storeName", "");
        tradingSystemRestController.getTradingSystem().addProduct("r"+username,token,1,"storeName","product1","aaa",100.0,100,5,1,"");
        tradingSystemRestController.getTradingSystem().addProduct("r"+username,token,2,"storeName","product2","bbb",100.0,100,5,1,"");
        String exitAdmin=tradingSystemRestController.getTradingSystem().logout(adminToken,"r"+"admin").getBody();
        tradingSystemRestController.getTradingSystem().exit(exitAdmin,"v1");
        String user1Token = tradingSystemRestController.getTradingSystem().enter().getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(user1Token);
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract token from JSON response");
        }
        user1Token = tradingSystemRestController.getTradingSystem().login(token, "v1", "user1", "User111").getBody();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(user1Token);
            username = rootNode.get("username").asText();
            token = rootNode.get("token").asText();
        } catch (Exception e) {
            fail("Setup failed: Unable to extract username and token from JSON response");
        }
        tradingSystemRestController.getTradingSystem().addToCart("ruser1",user1Token,1,"storeName",5,100.0);
        tradingSystemRestController.getTradingSystem().addToCart("ruser1",user1Token,2,"storeName",5,100.0);
        tradingSystemRestController.getTradingSystem().approvePurchase("ruser1",user1Token,"1234 Main Street, Springfield, IL, 62704-1234","100.00","USD","4111111111111111","12","2025","John Doe","123","123456789");
        tradingSystemRestController.getTradingSystem().exit(user1Token,"v2");
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
        assertTrue("User1 made purchase",storeRepository.getStore("storeName").isPurchaseOfUserExist("user1"));
    }


}
