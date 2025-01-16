package org.truelayer.pokedex.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.server.Handler;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.truelayer.pokedex.caller.FunTranslationsCaller;
import org.truelayer.pokedex.caller.PokeApiCaller;
import org.truelayer.pokedex.entrypoint.restapi.handler.TranslationHandler;
import org.truelayer.pokedex.model.*;
import org.truelayer.pokedex.util.TestHttpServer;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TranslationTest {
    private static final String BASE_URL = "http://localhost:8011";
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static class MockPokeApiCaller extends PokeApiCaller {
        @Override
        public PokemonResponse getPokemonByName(String name) {
            if (name.equals("error")) {
                throw new RuntimeException("Simulated API error");
            }

            Language language = new Language();
            language.setName("en");

            FlavorTextEntry flavorTextEntry = new FlavorTextEntry();
            flavorTextEntry.setFlavorText("It was created by\na scientist after\nyears of horrific\fgene splicing and\nDNA engineering\nexperiments.");
            flavorTextEntry.setLanguage(language);

            Habitat habitat = new Habitat();
            if (name.equals("cave-pokemon")) {
                habitat.setName("cave");
            } else {
                habitat.setName("rare");
            }

            return new PokemonResponse(
                    name,
                    Collections.singletonList(flavorTextEntry),
                    habitat,
                    name.equals("legendary-pokemon")
            );
        }
    }

    private static class MockFunTranslationsCaller extends FunTranslationsCaller {
        @Override
        public TranslateResponse translate(String text, String translator) throws Exception {
            if (text == null) throw new RuntimeException("Text cannot be null");

            if (translator.equals("error")) {
                throw new RuntimeException("Translation service error");
            }

            TranslateResponse response = new TranslateResponse();
            Contents contents = new Contents();
            contents.setText(text);
            contents.setTranslation(translator);

            if (translator.equals("shakespeare")) {
                contents.setTranslated("'t wast did create by a scientist after years of horrific gene splicing and dna engineering experiments.");
            } else if (translator.equals("yoda")) {
                contents.setTranslated("Created by a scientist after years of horrific gene splicing and DNA engineering experiments, it was.");
            }

            response.setContents(contents);
            return response;
        }
    }

    @BeforeAll
    static void setUp() throws Exception {
        Map<String, Handler> handlers = new HashMap<>();
        handlers.put("/pokemon/translated/*", new TranslationHandler(
                new MockPokeApiCaller(),
                new MockFunTranslationsCaller()
        ));
        TestHttpServer.start(handlers);
    }

    @AfterAll
    static void tearDown() throws Exception {
        TestHttpServer.stop();
    }

    @Test
    void testNormalPokemonTranslation() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(BASE_URL + "/pokemon/translated/mewtwo"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Pokemon pokemon = objectMapper.readValue(response.body(), Pokemon.class);

        assertEquals(200, response.statusCode());
        assertEquals("mewtwo", pokemon.getName());
        assertEquals("'t wast did create by a scientist after years of horrific gene splicing and dna engineering experiments.",
                pokemon.getDescription());
        assertEquals("rare", pokemon.getHabitat());
        assertFalse(pokemon.isLegendary());
    }

    @Test
    void testLegendaryPokemonTranslation() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(BASE_URL + "/pokemon/translated/legendary-pokemon"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Pokemon pokemon = objectMapper.readValue(response.body(), Pokemon.class);

        assertEquals(200, response.statusCode());
        assertTrue(pokemon.isLegendary());
        assertEquals("Created by a scientist after years of horrific gene splicing and DNA engineering experiments, it was.",
                pokemon.getDescription());
    }

    @Test
    void testCavePokemonTranslation() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(BASE_URL + "/pokemon/translated/cave-pokemon"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Pokemon pokemon = objectMapper.readValue(response.body(), Pokemon.class);

        assertEquals(200, response.statusCode());
        assertEquals("cave", pokemon.getHabitat());
        assertEquals("Created by a scientist after years of horrific gene splicing and DNA engineering experiments, it was.",
                pokemon.getDescription());
    }

    @Test
    void testEmptyPokemonName() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(BASE_URL + "/pokemon/translated/"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertTrue(response.body().contains("Missing pokemon name"));
    }

    @Test
    void testPokeApiError() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(BASE_URL + "/pokemon/translated/error"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(500, response.statusCode());
    }
}