package com.example.trading_system.domain.externalservices;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class WsepClient implements PaymentService {

    private static final String API_ENDPOINT = "https://damp-lynna-wsep-1984852e.koyeb.app/";

    public WsepClient() {}

    /**
     *  handshake:<br>
     *  This action type is used for check the availability of the external systems.<br>
     *  action_type = handshake<br>
     *  Additional Parameters: none<br>
     *  Output: “OK” message to signify that the handshake has been successful
     */
    public String handshake() throws IOException {
        String urlParameters = "action_type=handshake";
        return sendPostRequest(urlParameters);
    }

    /**
     *  pay:<br>
     *  This action type is used for charging a payment for purchases.<br>
     *  action_type = pay<br>
     *  Additional Parameters: amount, currency, card_number, month, year, holder, ccv,
     *  id<br>
     *  Output: transaction id- an integer in the range [10000,100000] which indicates a
     *  transaction number if the transaction succeeds or <br> -1 if the transaction has failed.
     * @param amount
     * @param currency
     * @param cardNumber
     * @param month
     * @param year
     * @param holder
     * @param ccv
     * @param id
     * @return
     * @throws IOException
     */
    public String pay(String amount, String currency, String cardNumber, String month, String year, String holder, String ccv, String id) throws IOException {
        String urlParameters = String.format("action_type=pay&amount=%s&currency=%s&card_number=%s&month=%s&year=%s&holder=%s&ccv=%s&id=%s",
                amount, currency, cardNumber, month, year, holder, ccv, id);
        return sendPostRequest(urlParameters);
    }

    /**
     * cancel_pay:<br>
     *  This action type is used for cancelling a payment transaction.<br>
     *  action_type = cancel_pay<br>
     *  Additional Parameters: transaction_id- the id of the transaction id of the
     *  transaction to be canceled.<br>
     *  Output: 1 if the cancelation has been successful or-1 if the cancelation has failed
     * @param transactionId
     * @return
     * @throws IOException
     */
    public String cancelPay(String transactionId) throws IOException {
        String urlParameters = "action_type=cancel_pay&transaction_id=" + transactionId;
        return sendPostRequest(urlParameters);
    }

    /**
     *  supply:<br>
     *  This action type is used for dispatching a delivery to a costumer.<br>
     *  action_type = supply<br>
     *  Additional Parameters: name , address, city, country, zip<br>
     *  Output: transaction id- an integer in the range [10000,100000] which indicates a
     *  transaction number if the transaction succeeds or-1 if the transaction has failed
     * @param name
     * @param address
     * @param city
     * @param country
     * @param zip
     * @return
     * @throws IOException
     */
    public String supply(String name, String address, String city, String country, String zip) throws IOException {
        String urlParameters = String.format("action_type=supply&name=%s&address=%s&city=%s&country=%s&zip=%s",
                name, address, city, country, zip);
        return sendPostRequest(urlParameters);
    }

    /**
     *  cancel_supply:<br>
     *  This action type is used for cancelling a supply transaction.<br>
     *  action_type = cancel_supply<br>
     *  Additional Parameters: transaction_id- the id of the transaction id of the
     *  transaction to be canceled.<br>
     *  Output: 1 if the cancelation has been successful or-1 if the cancelation has failed
     * @param transactionId
     * @return
     * @throws IOException
     */
    public String cancelSupply(String transactionId) throws IOException {
        String urlParameters = "action_type=cancel_supply&transaction_id=" + transactionId;
        return sendPostRequest(urlParameters);
    }

    private String sendPostRequest(String urlParameters) throws IOException {
        URL url = new URL(API_ENDPOINT);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setDoOutput(true);

        try (DataOutputStream out = new DataOutputStream(connection.getOutputStream())) {
            out.writeBytes(urlParameters);
            out.flush();
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } else {
            throw new IOException("POST request failed with HTTP error code: " + responseCode);
        }
    }

//  check responses
    public static void main(String[] args) throws IOException {
        WsepClient apiClient = new WsepClient();

        // Test each API call
        System.out.println("Handshake Response: " + apiClient.handshake());

        String payResponse = apiClient.pay("1000", "a", "2222333344445555", "4", "2021", "Israel", "262", "20444444");
        System.out.println("Pay Response: " + payResponse);

        String cancelPayResponse = apiClient.cancelPay("20123");
        System.out.println("Cancel Pay Response: " + cancelPayResponse);

        String supplyResponse = apiClient.supply("Israel", "RagerBlvd12", "BeerSheva", "Israel", "8458527");
        System.out.println("Supply Response: " + supplyResponse);

        String cancelSupplyResponse = apiClient.cancelSupply("30525");
        System.out.println("Cancel Supply Response: " + cancelSupplyResponse);
    }

    @Override
    public int makePayment(double amount, String currency, String cardNumber, String month, String year, String holder, String ccv, String id) {
        String amountString = String.format("%.2f", amount);
        try {
            String res= pay(amountString, currency, cardNumber, month, year, holder,ccv, id);
            return Integer.parseInt(res);
        }
        catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public void cancelPayment(int paymentId) {
        String paymentIdString = String.valueOf(paymentId);
        try {
            String urlParameters = cancelPay(paymentIdString);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Not in use
    @Override
    public boolean connect() {
        return false;
    }
}