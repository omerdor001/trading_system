package com.example.trading_system.UnitTests.users;

import com.example.trading_system.domain.users.Notification;
import com.example.trading_system.domain.users.Registered;
import com.example.trading_system.domain.users.Visitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class NotificationUnitTests {
    private Registered registeredUser;
    private Visitor visitor;

    @BeforeEach
    void setUp() {
        registeredUser = new Registered(  "testUser", "testAddress", LocalDate.of(2000,1,1));
        visitor = new Visitor("testUser");
    }
/*//TODO FIX ME
    @Test
    void sendNotification() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date date = new Date();
        String dateStr = dateFormat.format(date);
        String expectedJson = "{\"senderId\":2,\"receiverId\":1,\"dateOfCreation\":\"" + dateStr + "\",\"textContent\":\"Test Content\"}";
        Notification notification = new Notification("2", "1", date, "Test Content");
        String actualJson = notification.toString();
        assertEquals(expectedJson, actualJson);
    }

    @Test
    void receiveNotification_Registered() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date date = new Date();
        String dateStr = dateFormat.format(date);
        String expectedJson = "{\"senderId\":2,\"receiverId\":1,\"dateOfCreation\":\"" + dateStr + "\",\"textContent\":\"Test Content\"}";

        Notification notification = new Notification("2", "1", dateFormat.parse(dateStr), "Test Content");
        String notificationString = notification.toString();
        Notification t = Notification.fromString(notificationString);

        assertEquals(expectedJson, notificationString);
//        assertEquals(notification.getSenderId(), t.getSenderId());
//        assertEquals(notification.getReceiverId(), t.getReceiverId());
        assertEquals(notification.getTextContent(), t.getTextContent());
        assertEquals(dateFormat.format(notification.getDateOfCreation()), dateFormat.format(t.getDateOfCreation()));
    }*/

//    @Test
//    void receiveNotification_Visitor() {
//        String notificationString = "Notification{senderId=2, receiverId=1, dateOfCreation=" + new Date() + ", textContent='Test Content'}";
//        UnsupportedOperationException thrown = assertThrows(
//                UnsupportedOperationException.class,
//                () -> visitor.receiveNotification(notificationString),
//                "Expected receiveNotification to throw, but it didn't"
//        );
//
//        assertTrue(thrown.getMessage().contains("Visitors cannot receive notifications"));
//    }
}
