package org.truelayer.pokedex.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.server.Handler;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.truelayer.pokedex.caller.PokeApiCaller;
import org.truelayer.pokedex.entrypoint.restapi.handler.PokemonHandler;
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

public class PokeApiTest {
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

			Habitat habitat = null;
			habitat = new Habitat();
			habitat.setName("rare");

			return new PokemonResponse(
					name,
					Collections.singletonList(flavorTextEntry),
					habitat,
					true
			);
		}
	}

	@BeforeAll
	static void setUp() throws Exception {
		Map<String, Handler> handlers = new HashMap<>();
		handlers.put("/pokemon/*", new PokemonHandler(new MockPokeApiCaller()));
		TestHttpServer.start(handlers);
	}

	@AfterAll
	static void tearDown() throws Exception {
		TestHttpServer.stop();
	}

	@Test
	void testSuccessfulPokemonRequest() throws Exception {
		// Given
		HttpRequest request = HttpRequest.newBuilder()
				.uri(new URI(BASE_URL + "/pokemon/mewtwo"))
				.GET()
				.build();

		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		Pokemon pokemon = objectMapper.readValue(response.body(), Pokemon.class);

		assertEquals(200, response.statusCode());
		assertEquals("mewtwo", pokemon.getName());
		assertEquals("It was created by a scientist after years of horrific gene splicing and DNA engineering experiments.",
				pokemon.getDescription());
		assertEquals("rare", pokemon.getHabitat());
		assertTrue(pokemon.isLegendary());
	}

	@Test
	void testEmptyPokemonName() throws Exception {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(new URI(BASE_URL + "/pokemon/"))
				.GET()
				.build();

		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

		assertEquals(400, response.statusCode());
		assertTrue(response.body().contains("Missing pokemon name"));
	}

	@Test
	void testPokeApiError() throws Exception {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(new URI(BASE_URL + "/pokemon/error"))
				.GET()
				.build();

		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

		assertEquals(500, response.statusCode());
		assertTrue(response.body().contains("Simulated API error"));
	}

	@Test
	void testDescriptionFormatting() throws Exception {
		// Given
		HttpRequest request = HttpRequest.newBuilder()
				.uri(new URI(BASE_URL + "/pokemon/mewtwo"))
				.GET()
				.build();

		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		Pokemon pokemon = objectMapper.readValue(response.body(), Pokemon.class);

		assertEquals(200, response.statusCode());
		assertFalse(pokemon.getDescription().contains("\n"));
		assertFalse(pokemon.getDescription().contains("\f"));
	}
}