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
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:applicationTests.properties")
@Transactional
public class UserPersistenceTests {

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
        newUser.addProductToCart(0, 0, "store", 1, 1);
        userRepository.saveUser(newUser);

        initNewRepo();
        User retrievedUser = anotherUserRepository.getUser("rnewUser");
        assertEquals(1, retrievedUser.getCart().getShoppingBags().size());
        ShoppingBag bag = retrievedUser.getCart().getShoppingBags().values().iterator().next();
        assertEquals(1, bag.getProducts_list().size());
        ProductInSale product = bag.getProducts_list().values().iterator().next();
        assertEquals(0, product.getId());
        assertEquals(0, product.getQuantity());
        assertEquals("store", product.getStoreId());
        assertEquals(1, product.getPrice());
        assertEquals(1, product.getCategory());
    }

    @Test
    void registeredSuggestManagerRetrieve() {
        User newUser = new Registered("newUser", "newPassword", LocalDate.now());
        newUser.addWaitingAppoint_Manager("store", "owner", true, true, true, true, true);
        userRepository.saveUser(newUser);

        initNewRepo();
        User retrievedUser = anotherUserRepository.getUser("rnewUser");
        assertEquals(1, retrievedUser.getManagerSuggestions().size());
        ManagerSuggestion suggestion = retrievedUser.getManagerSuggestions().get(0);
        assertEquals("store:owner", suggestion.getSuggestionKey());
        assertEquals(Arrays.asList(true, true, true, true, true), suggestion.getSuggestionValues());
    }

    @Test
    void registeredNotificationRetrieve() {
        User newUser = new Registered("newUser", "newPassword", LocalDate.now());
        newUser.receiveDelayedNotification(new Notification("owner", "newUser", "test"));
        userRepository.saveUser(newUser);

        initNewRepo();
        User retrievedUser = anotherUserRepository.getUser("rnewUser");
        List<Notification> notifications = retrievedUser.getNotifications();
        assertEquals(1, notifications.size());
        Notification notification = notifications.get(0);
        assertEquals("newUser", notification.getReceiverUsername());
        assertEquals("owner", notification.getSenderUsername());
        assertEquals("test", notification.getTextContent());
    }

    // Problem cleaning up after the test because of foreign key constraints
    @Test
    void registeredRoleRetrieve() {
        Registered newUser = new Registered("newUser", "newPassword", LocalDate.now());
        newUser.addOwnerRole("owner", "store");
        newUser.addManagerRole("owner", "Store");
        newUser.setPermissionsToManager("Store", true, true, true, true, true);
        newUser.getRoleByStoreId("store").setRegisteredUser(newUser);
        newUser.getRoleByStoreId("Store").setRegisteredUser(newUser);
        userRepository.saveUser(newUser);

        initNewRepo();
        User retrievedUser = anotherUserRepository.getUser("rnewUser");
        Role owner = retrievedUser.getRoleByStoreId("store");
        Role manager = retrievedUser.getRoleByStoreId("Store");
        assertNotNull(owner);
        assertNotNull(manager);
        assertEquals("store", owner.getStoreId());
        assertEquals("Store", manager.getStoreId());
        assertEquals("owner", owner.getAppointedById());
        assertEquals("owner", manager.getAppointedById());
        assertTrue(owner.getRoleState().isOwner());
        assertTrue(manager.getRoleState().isManager());
        assertTrue(manager.getRoleState().isWatch());
        assertTrue(manager.getRoleState().isEditSupply());
        assertTrue(manager.getRoleState().isEditPurchasePolicy());
        assertTrue(manager.getRoleState().isEditDiscountPolicy());
        assertTrue(manager.getRoleState().isAcceptBids());

//        try {
//            entityManager.createNativeQuery("ALTER TABLE manager_suggestion NOCHECK CONSTRAINT ALL").executeUpdate();
//            entityManager.createNativeQuery("ALTER TABLE owner_suggestions NOCHECK CONSTRAINT ALL").executeUpdate();
//            entityManager.createNativeQuery("ALTER TABLE Registered_Message NOCHECK CONSTRAINT ALL").executeUpdate();
//            entityManager.createNativeQuery("ALTER TABLE roles NOCHECK CONSTRAINT ALL").executeUpdate();
//            entityManager.createNativeQuery("DROP TABLE Registered").executeUpdate();
//            entityManager.createNativeQuery("ALTER TABLE manager_suggestion CHECK CONSTRAINT ALL").executeUpdate();
//            entityManager.createNativeQuery("ALTER TABLE owner_suggestions CHECK CONSTRAINT ALL").executeUpdate();
//            entityManager.createNativeQuery("ALTER TABLE Registered_Message CHECK CONSTRAINT ALL").executeUpdate();
//            entityManager.createNativeQuery("ALTER TABLE roles CHECK CONSTRAINT ALL").executeUpdate();
//        }
//        catch (Exception e){
//
//        }
    }
}
