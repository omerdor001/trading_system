package com.example.trading_system.users;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.example.trading_system.domain.users.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class NotificationAcceptanceTests {
    private UserFacade userFacade;

    @BeforeEach
    public void setUp() {
        userFacade = mock(UserFacade.class);
    }

    @Test
    void sendNotification() {
        // Create sender and receiver instances
        Registered sender = new Registered(2, "Sender", "Sender Address", LocalDate.of(2000,1,1));
        Registered receiver = new Registered(1, "Receiver", "Receiver Address",  LocalDate.of(2000,2,2));

        // Create the date string in the expected format
        String dateStr = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());


        // Mock userFacade to trigger receiver.receiveNotification
        doAnswer(invocation -> {
            String notifStr = invocation.getArgument(2, String.class);
            String sentNotification = sender.sendNotification(receiver.getId(), notifStr);
            receiver.receiveNotification(sentNotification);
            return null;
        }).when(userFacade).sendNotification(sender, receiver, "Test Content");

        // Trigger the userFacade to send the notification
        userFacade.sendNotification(sender, receiver, "Test Content");

        // Verify the receiver's notifications
        assertEquals(1, receiver.getNotifications().size());
        Notification receivedNotification = receiver.getNotifications().get(0);
        assertEquals(2, receivedNotification.getSenderId());
        assertEquals(1, receivedNotification.getReceiverId());
        assertEquals("Test Content", receivedNotification.getTextContent());
        assertNotNull(receivedNotification.getDateOfCreation());

        // Verify the date format
        assertEquals(dateStr, new SimpleDateFormat("dd/MM/yyyy HH:mm").format(receivedNotification.getDateOfCreation()));
    }
}
