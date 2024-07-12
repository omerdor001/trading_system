package com.example.trading_system.ExyernalSystem;

import com.example.trading_system.domain.externalservices.WsepClient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
public class WsepClientTests {
    private WsepClient apiClient = new WsepClient();

    @Test
    public void testHandshake() throws IOException {
        String response = apiClient.handshake();
        System.out.println("test Handshake response "+ response);
        assertEquals("OK", response.trim());
    }


    @Test
    @Disabled
    public void testPay() throws IOException {
        String response = apiClient.pay("1000", "USD", "2222333344445555", "4", "2021", "Israel", "262", "20444444");
        System.out.println("test Pay response "+ response);
        // Assuming response will contain either a transaction id or -1
        boolean isTransactionId = response.matches("\\d{5,6}"); // Matches a transaction id in the range [10000, 100000]
        boolean isFailure = response.equals("-1");
        assertEquals(true, isTransactionId);
    }

    @Test
    @Disabled
    public void testPayCcv984() throws IOException {
        String response = apiClient.pay("1000", "USD", "2222333344445555", "4", "2021", "Israel", "984", "20444444");
        System.out.println("test Pay cvv 984 response "+ response);
        // Assuming response will contain either a transaction id or -1
        boolean isTransactionId = response.matches("\\d{5,6}"); // Matches a transaction id in the range [10000, 100000]
        boolean isFailure = response.equals("unexpected-output");
        assertEquals(true, isFailure);
    }
    @Test
    @Disabled
    public void testPayCcv986() throws IOException {
        String response = apiClient.pay("1000", "USD", "2222333344445555", "4", "2021", "Israel", "986", "20444444");
        System.out.println("test Pay cvv 986 response "+ response);
        // Assuming response will contain either a transaction id or -1
        boolean isTransactionId = response.matches("\\d{5,6}"); // Matches a transaction id in the range [10000, 100000]
        boolean isFailure = response.equals("-1");
        assertEquals(true, isFailure);
    }

    @Test
    @Disabled
    public void testPayCcv988() throws IOException {
        String response = apiClient.pay("1000", "USD", "2222333344445555", "4", "2021", "Israel", "988", "20444444");
        System.out.println("test Pay cvv 988 response "+ response);
        // Assuming response will contain either a transaction id or -1
        boolean isTransactionId = response.matches("\\d{5,6}"); // Matches a transaction id in the range [10000, 100000]
        boolean isFailure = response.equals("-1");
        assertEquals(true, isFailure);
    }

    @Test
    public void testCancelPay() throws IOException {
        String response = apiClient.cancelPay("20123");
        System.out.println("test CancelPay response "+ response);
        // Assuming response will be either "1" or "-1"
        assertEquals("1", response.trim());
    }

    @Test
    public void testSupply() throws IOException {
        String response = apiClient.supply("Israel Israelovice", "RagerBlvd12", "BeerSheva", "Israel", "8458527");
        System.out.println("test Supply response "+ response);
        // Assuming response will contain either a transaction id or -1
        boolean isTransactionId = response.matches("\\d{5,6}"); // Matches a transaction id in the range [10000, 100000]
        boolean isFailure = response.equals("-1");
        assertEquals(true, isTransactionId);
    }

    @Test
    public void testCancelSupply() throws IOException {
        String response = apiClient.cancelSupply("30525");
        System.out.println("test CancelSupply response "+ response);
        // Assuming response will be either "1" or "-1"
        assertEquals("1", response.trim());
    }
}
