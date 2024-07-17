package com.example.trading_system.PersistenceTests;

import com.example.trading_system.TradingSystemRestController;
import com.example.trading_system.domain.users.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:applicationTests.properties")
@Transactional
public class SystemResetTests {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ApplicationContext applicationContext;

    private UserRepository userRepository;

    private UserDatabaseRepository anotherUserRepository;

    @BeforeEach
    void setUp() {
        applicationContext.getBean(TradingSystemRestController.class).deleteData();
        userRepository = applicationContext.getBean(UserDatabaseRepository.class);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteData();
    }

    private void initNewRepo() {
        anotherUserRepository = new UserDatabaseRepository();
        anotherUserRepository.setEntityManager(entityManager);
    }

    @Test
    void createRegisteredAndRetrieve() {
        User newUser = new Registered("newUser", "newPassword", LocalDate.now());
        userRepository.saveUser(newUser);

        initNewRepo();
        User retrievedUser = anotherUserRepository.getUser("rnewUser");
        assertNotNull(retrievedUser);
        assertEquals("newUser", retrievedUser.getUsername());
        assertEquals("newPassword", retrievedUser.getPass());
    }

    @Test
    void createVisitorAndRetrieve() {
        User newUser = new Visitor("newUser");
        userRepository.saveUser(newUser);

        initNewRepo();
        User retrievedUser = anotherUserRepository.getUser("rnewUser");
        assertNull(retrievedUser);
    }

    @Test
    void registeredAddToCartRetrieve() {
        User newUser = new Registered("newUser", "newPassword", LocalDate.now());
        newUser.addProductToCart(0,0,"store", 1,1);
        userRepository.saveUser(newUser);

        initNewRepo();
        User retrievedUser = anotherUserRepository.getUser("rnewUser");
        Cart cart = retrievedUser.getCart();
        try{
            assertEquals("{\"id\":1999,\"shoppingBags\":{\"store\":{\"id\":121,\"storeId\":\"store\",\"products_list\":{\"0\":{\"storeId\":\"store\",\"id\":0,\"price\":1.0,\"quantity\":0,\"category\":1,\"shoppingBag\":null}}}}}",cart.toJson());
        }
        catch (Exception e){
            fail(e);
        }
    }
}
