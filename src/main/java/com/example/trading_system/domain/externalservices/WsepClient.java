package com.example.trading_system.domain.externalservices;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class WsepClient {
    private static final String BASE_URL = "https://damp-lynna-wsep-1984852e.koyeb.app/";

    public enum Action {
        HANDSHAKE("handshake"),
        PAY("pay"),
        CANCEL_PAY("cancel_pay"),
        SUPPLY("supply"),
        CANCEL_SUPPLY("cancel_supply");

        private final String value;

        Action(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public String sendRequest(Action action, Map<String, String> params) throws IOException {
        URL url = new URL(BASE_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");

        String jsonString = convertParamsToJson(params);
        connection.setDoOutput(true);
        connection.getOutputStream().write(jsonString.getBytes());

        int responseCode = connection.getResponseCode();

        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("Request failed with response code: " + responseCode);
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }

        return response.toString();
    }

    private String convertParamsToJson(Map<String, String> params) {
        StringBuilder json = new StringBuilder("{");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            json.append("\"").append(entry.getKey()).append("\":\"").append(entry.getValue()).append("\",");
        }
        json.deleteCharAt(json.length() - 1); // remove the last comma
        json.append("}");
        return json.toString();
    }

}
