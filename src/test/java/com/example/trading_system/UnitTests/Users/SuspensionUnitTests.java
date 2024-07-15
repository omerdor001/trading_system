package com.example.trading_system.UnitTests.users;

import com.example.trading_system.domain.NotificationSender;
import com.example.trading_system.domain.externalservices.DeliveryService;
import com.example.trading_system.domain.externalservices.PaymentService;
import com.example.trading_system.domain.stores.MarketFacade;
import com.example.trading_system.domain.stores.MarketFacadeImp;
import com.example.trading_system.domain.stores.StoreMemoryRepository;
import com.example.trading_system.domain.stores.StoreRepository;
import com.example.trading_system.domain.users.UserFacade;
import com.example.trading_system.domain.users.UserFacadeImp;
import com.example.trading_system.domain.users.UserMemoryRepository;
import com.example.trading_system.domain.users.UserRepository;
import org.junit.jupiter.api.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SuspensionUnitTests {
    MarketFacade marketFacade;
    UserFacade userFacade;
    private UserRepository userRepository;
    private StoreRepository storeRepository;

    @BeforeAll
    void setUpBA() {
        storeRepository= StoreMemoryRepository.getInstance();
        userRepository = UserMemoryRepository.getInstance();
        userFacade= UserFacadeImp.getInstance(mock(PaymentService.class),mock(DeliveryService.class), mock(NotificationSender.class),userRepository,storeRepository);
        marketFacade= MarketFacadeImp.getInstance(storeRepository);
        try {
            userFacade.enter(0);
            userFacade.enter(1);
            userFacade.enter(2);
            userFacade.register("testuser0","1pA22w0rd", LocalDate.now());
            userFacade.register("testuser1","pA22w0rd1", LocalDate.now());
            userFacade.register("testuser2","pA22w0rd2", LocalDate.now());
            userFacade.login("v0","testuser0","1pA22w0rd");
            userFacade.login("v1","testuser1","pA22w0rd1");
            userFacade.login("v2","testuser2","pA22w0rd2");
            userFacade.createStore("rtestuser0","Adidas","sport shop");
            userFacade.createStore("rtestuser0","Nike","sport shop");
            userFacade.suggestOwner("rtestuser0","rtestuser1","Adidas");
            userFacade.approveOwner("rtestuser1","Adidas","rtestuser0");
            userFacade.suggestManager("rtestuser0","rtestuser2","Adidas",false,false,false,false, false, false);
            userFacade.approveManager("rtestuser2","Adidas","rtestuser0",false,false,false,false, false, false);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    public void tearDown(){
        userFacade.logout(0,"rtestuser0");
        userFacade.logout(1,"rtestuser1");
        userFacade.logout(2,"rtestuser2");
        try{
            userFacade.exit("v2");
            userFacade.exit("v1");
            userFacade.exit("v0");
        }
        catch (Exception e){

        }
        marketFacade.deleteInstance();
        userFacade.deleteInstance();
    }

    @Test
    void suspendUser_Success() {
        boolean suspended = userFacade.isSuspended("rtestuser2");
        assertDoesNotThrow(() -> userFacade.suspendUser("rtestuser0", "rtestuser2", LocalDateTime.of(2024, 8, 1, 1, 1)), "suspendUser should not throw any exceptions");
        assertEquals(suspended, userFacade.isSuspended("rtestuser2"));
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userFacade.purchaseCart("rtestuser2", "1234 El Street, Springfield, IL, 62704-5678", "100.00", "USD", "4111111111111111", "12", "2025", "John Doe", "123", "123456789"));
        assertEquals("User is suspended from the system", exception.getMessage());
    }

    @Test
    void suspendUser_UserToSuspendNotExist() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userFacade.suspendUser("rtestuser0","rtestuser4", LocalDateTime.of(2024,8,1,1,1)));
        assertEquals("User to suspend doesn't exist in the system", exception.getMessage());
    }

    @Test
    void suspendUser_SuspenderNotExist() {
        boolean suspended=userFacade.isSuspended("rtestuser2");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userFacade.suspendUser("rtestuser4","rtestuser2", LocalDateTime.of(2024,8,1,1,1)));
        assertEquals("Admin user doesn't exist in the system", exception.getMessage());
        assertEquals(suspended,userFacade.isSuspended("rtestuser2"));
    }

    @Test
    void suspendUser_SuspenderNotAdmin() {
        boolean suspended=userFacade.isSuspended("rtestuser2");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userFacade.suspendUser("rtestuser1","rtestuser2", LocalDateTime.of(2024,8,1,1,1)));
        assertEquals("Only admin user can suspend users", exception.getMessage());
        assertEquals(suspended,userFacade.isSuspended("rtestuser2"));
    }

    @Test
    void suspendUser_EarlyDate() {
        boolean suspended=userFacade.isSuspended("rtestuser2");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userFacade.suspendUser("rtestuser0","rtestuser2", LocalDateTime.of(2024,6,1,1,1)));
        assertEquals("Date of suspension cannot be before now", exception.getMessage());
        assertEquals(suspended,userFacade.isSuspended("rtestuser2"));
    }

    @Test
    void endSuspendUser_Success() {
        userFacade.suspendUser("rtestuser0","rtestuser2", LocalDateTime.of(2024,8,1,1,1));
        boolean suspended=userFacade.isSuspended("rtestuser2");
        assertDoesNotThrow(() -> userFacade.endSuspendUser("rtestuser0","rtestuser2"), "endSuspendUser should not throw any exceptions");
        assertEquals(!suspended,userFacade.isSuspended("rtestuser2"));
        Exception exception = assertThrows(Exception.class, () -> userFacade.purchaseCart("rtestuser2", "1234 El Street, Springfield, IL, 62704-5678", "100.00", "USD", "4111111111111111", "12", "2025", "John Doe", "123", "123456789"));
        assertNotEquals("User is suspended from the system", exception.getMessage());
    }

    @Test
    void endSuspendUser_UserToSuspendNotExist() {
        userFacade.suspendUser("rtestuser0","rtestuser2", LocalDateTime.of(2024,8,1,1,1));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userFacade.endSuspendUser("rtestuser0","rtestuser4"));
        assertEquals("User to suspend doesn't exist in the system", exception.getMessage());
    }

    @Test
    void endSuspendUser_AdminNotExist() {
        boolean suspended=userFacade.isSuspended("rtestuser2");
        userFacade.suspendUser("rtestuser0","rtestuser2", LocalDateTime.of(2024,8,1,1,1));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userFacade.endSuspendUser("rtestuser4","rtestuser2"));
        assertEquals("Admin user doesn't exist in the system", exception.getMessage());
        assertEquals(suspended,userFacade.isSuspended("rtestuser2"));
    }

    @Test
    void endSuspendUser_SuspenderNotAdmin() {
        boolean suspended=userFacade.isSuspended("rtestuser2");
        userFacade.suspendUser("rtestuser0","rtestuser2", LocalDateTime.of(2024,8,1,1,1));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userFacade.endSuspendUser("rtestuser1","rtestuser2"));
        assertEquals("Only admin user can suspend users", exception.getMessage());
        assertEquals(suspended,userFacade.isSuspended("rtestuser2"));
    }

    @Test
    void endSuspendUser_UserNotSuspended() {
        boolean suspended=userFacade.isSuspended("rtestuser2");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userFacade.endSuspendUser("rtestuser0","rtestuser2"));
        assertEquals("User need to be suspend for ending suspend", exception.getMessage());
        assertEquals(suspended,userFacade.isSuspended("rtestuser2"));
    }

    @Test
    void watchSuspensions_success() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        userFacade.suspendUser("rtestuser0", "rtestuser2", LocalDateTime.of(2024, 8, 1, 1, 1));
        assertDoesNotThrow(() -> {
            userFacade.watchSuspensions("rtestuser0");
        }, "watchSuspensions should not throw any exceptions");
        String result = userFacade.watchSuspensions("rtestuser0");
        long duration_hours = Math.abs(Duration.between(currentDateTime, LocalDateTime.of(2024, 8, 1, 1, 1)).toHours());
        long duration_days = Math.abs(Duration.between(currentDateTime, LocalDateTime.of(2024, 8, 1, 1, 1)).toDays());
        String expectedJson = "[{\"End of suspension\":\"2024-08-01T01:01\",\"Username\":\"testuser2\",\"Time of suspension (in days)\":" + duration_days + ",\"Start of suspension\":\"" + currentDateTime.truncatedTo(ChronoUnit.SECONDS) + "\",\"Time of suspension (in hours)\":" + duration_hours + "}]";
        assertEquals(expectedJson, result);
    }


    @Test
    void watchSuspensions_AdminNotExist() {
        userFacade.suspendUser("rtestuser0","rtestuser2", LocalDateTime.of(2024,8,1,1,1));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userFacade.watchSuspensions("rtestuser4"));
        assertEquals("Admin user doesn't exist in the system", exception.getMessage());
    }

    @Test
    void watchSuspensions_SuspenderNotAdmin() {
        userFacade.suspendUser("rtestuser0","rtestuser2", LocalDateTime.of(2024,8,1,1,1));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userFacade.watchSuspensions("rtestuser2"));
        assertEquals("Only admin user can suspend users", exception.getMessage());
    }
}