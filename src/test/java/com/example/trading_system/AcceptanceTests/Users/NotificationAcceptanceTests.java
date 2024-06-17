package com.example.trading_system.AcceptanceTests.Users;

import com.example.trading_system.service.TradingSystem;
import com.example.trading_system.service.TradingSystemImp;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.fail;

class NotificationAcceptanceTests {
    private TradingSystem tradingSystem;
    private String token;
    private String username;

    @BeforeEach
    public void setUp() {
//        tradingSystem = TradingSystemImp.getInstance();
//        tradingSystem.register("owner1", "password123", LocalDate.now());
//        tradingSystem.openSystem();
//        String userToken = tradingSystem.enter().getBody();
//        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonNode rootNode = objectMapper.readTree(userToken);
//            token = rootNode.get("token").asText();
//        } catch (Exception e) {
//            fail("Setup failed: Unable to extract token from JSON response");
//        }
//        userToken = tradingSystem.login(token, "0", "owner1", "password123").getBody();
//        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonNode rootNode = objectMapper.readTree(userToken);
//            username = rootNode.get("username").asText();
//            token = rootNode.get("token").asText();
//        } catch (Exception e) {
//            fail("Setup failed: Unable to extract username and token from JSON response");
//        }
    }
//TODO FIX ME
/*    @Test
    void sendNotification() {
        // Create sender and receiver instances
        Registered sender = new Registered( "Sender", "Sender Address", LocalDate.of(2000,1,1));
        Registered receiver = new Registered( "Receiver", "Receiver Address",  LocalDate.of(2000,2,2));

        // Create the date string in the expected format
        String dateStr = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());

        //TODO change from mock to actual notifications
//        // Mock userFacade to trigger receiver.receiveNotification
//        doAnswer(invocation -> {
//            String notifStr = invocation.getArgument(2, String.class);
//            String sentNotification = sender.sendNotification(receiver.getId(), notifStr);
//            receiver.receiveNotification(sentNotification);
//            return null;
//        }).when(userFacade).sendNotification(sender, receiver, "Test Content");
//
//        // Trigger the userFacade to send the notification
//        userFacade.sendNotification(sender, receiver, "Test Content");

        // Verify the receiver's notifications
        assertEquals(1, receiver.getNotifications().size());
        Notification receivedNotification = receiver.getNotifications().get(0);
//        assertEquals(2, receivedNotification.getSenderId());
//        assertEquals(1, receivedNotification.getReceiverId());
        assertEquals("Test Content", receivedNotification.getTextContent());
        assertNotNull(receivedNotification.getDateOfCreation());

        // Verify the date format
        assertEquals(dateStr, new SimpleDateFormat("dd/MM/yyyy HH:mm").format(receivedNotification.getDateOfCreation()));
    }*/
}
