package org.translator.api_controller;

import java.io.IOException;

public interface APIConnector {
    String sendPostRequest(String sourceText, String sourceLang, String targetLang) throws IOException;
}
