package org.truelayer.pokedex.caller;

import org.truelayer.pokedex.configuration.ServerConfiguration;
import org.truelayer.pokedex.model.TranslateRequest;
import org.truelayer.pokedex.model.TranslateResponse;
import org.truelayer.pokedex.utils.ObjectMapperWrapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class FunTranslationsCaller {

    private static final String FUNTRANSLATIONS_BASE_URL = "https://api.funtranslations.com/translate/";

    public FunTranslationsCaller() {

    }

    /**
     *
     * @param text
     * @param type
     * @throws Exception
     */
    public TranslateResponse translate(final String text, final String type) throws Exception {
        if (type != null && text != null) {
            final String url = FUNTRANSLATIONS_BASE_URL + type + ".json";
            int code;
            TranslateRequest translateRequest = new TranslateRequest(text);
            TranslateResponse translateResponse;

            try (HttpClient httpClient = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofMillis(ServerConfiguration.getInstance().getTimeoutRequestOnExternalCall())).build()) {
                String json = ObjectMapperWrapper.getWriter().writeValueAsString(translateRequest);

                HttpRequest request = HttpRequest.newBuilder()
                        .POST(HttpRequest.BodyPublishers.ofString(json)).uri(URI.create(url))
                        .headers("Content-Type", "application/json").build();

                try {
                    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                    code = response.statusCode();

                    try {
                        translateResponse = ObjectMapperWrapper.getReader().readValue(response.body(), TranslateResponse.class);
                    } catch (IOException e) {
                        throw new Exception("Failed to parse the FunTranslations API response.", e);
                    }
                } catch (Exception e) {
                    throw new Exception("Http request to '" + url + "' failed.", e);
                }
            }

            if (code < 200 || code > 300) {
                throw new Exception(
                        "Http request to '" + url + "' failed. Response code: " + code + ".");
            }

            return translateResponse;

        } else {
            throw new Exception("No text or type provided.");
        }
    }
}
