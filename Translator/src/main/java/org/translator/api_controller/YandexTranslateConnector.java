package org.translator.api_controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

public class YandexTranslateConnector implements APIConnector {

    final String URL_ADDR = "https://translate.api.cloud.yandex.net/translate/v2/translate";

    //TODO: ВПИСАТЬ КЛЮЧ API
    final String YNDX_API_KEY = "AQVNxg5DBJ4SOSouhidYlXC_5Lfd62cBcaD_ECkl";

    private static final YandexTranslateConnector connector = new YandexTranslateConnector();

    private YandexTranslateConnector() {}
    public static YandexTranslateConnector getInstance() { return connector; }

    @Override
    public String sendPostRequest(String sourceText, String sourceLang, String targetLang) throws IOException {

        final String SOURCE_LANG = (sourceLang != null) ? sourceLang : "en";
        final String TARGET_LANG = (targetLang != null) ? targetLang : "ru";
        final int CONNECTION_TIMEOUT = 10000;

        HttpURLConnection connection = (HttpURLConnection) new URL(URL_ADDR).openConnection();
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", String.format("Api-Key %s", YNDX_API_KEY));
        connection.setConnectTimeout(CONNECTION_TIMEOUT);
        connection.setRequestMethod("POST");

        connection.setDoOutput(true);

        sourceText = sourceText.replaceAll("[\\n\\r]+", " ");
        sourceText = String.format("[\"%s\"]", sourceText);
        final String requestBody = String.format("{\n" +
                "    \"texts\": %s,\n" +
                "    \"sourceLanguageCode\": \"%s\",\n" +
                "    \"targetLanguageCode\": \"%s\"\n" +
                "}", sourceText, SOURCE_LANG, TARGET_LANG);

        try(OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream())) {
            writer.write(requestBody);
        }

        if(connection.getResponseCode() != 200) {
            System.out.println("Connection failed!");
            return "";
        }

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("utf-8")))) {
            int code;
            StringBuilder builder = new StringBuilder();
            while((code = reader.read()) != -1) {
                builder.append((char)code);
            }

            String response = builder.toString();
            final String key = "\"text\": ";
            String[] result = response
                    .substring(response.indexOf(key) + key.length() + 1).split("\"\\s+}");
            return result[0];
        }
    }
}
