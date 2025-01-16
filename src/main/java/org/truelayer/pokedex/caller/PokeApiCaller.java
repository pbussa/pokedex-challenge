package org.truelayer.pokedex.caller;

import org.truelayer.pokedex.configuration.ServerConfiguration;
import org.truelayer.pokedex.model.PokemonResponse;
import org.truelayer.pokedex.utils.ObjectMapperWrapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;


public class PokeApiCaller {

    private static final String POKEAPI_BASE_URL = "https://pokeapi.co/api/v2/pokemon-species/";

    public PokeApiCaller() {

    }

    /**
     * @param pokemonName the name of the Pokémon to retrieve
     * @retunrn deserialized Pokémon response
     * @throws Exception
     */
    public PokemonResponse getPokemonByName(final String pokemonName) throws Exception {
        if (pokemonName != null) {
            final String url = POKEAPI_BASE_URL + pokemonName;
            int code;
            PokemonResponse pokemonResponse;

            try (HttpClient httpClient = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofMillis(ServerConfiguration.getInstance().getTimeoutRequestOnExternalCall())).build()) {

                HttpRequest request = HttpRequest.newBuilder()
                        .GET().uri(URI.create(url))
                        .headers("Content-Type", "application/json").build();

                try {
                    HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
                    code = response.statusCode();

                    try {
                        pokemonResponse = ObjectMapperWrapper.getReader().readValue(response.body(), PokemonResponse.class);
                    } catch (IOException e) {
                        throw new Exception("Failed to parse the Pokémon API response.", e);
                    }
                } catch (Exception e) {
                    throw new Exception("Http request to '" + url + "' failed.", e);
                }
            }

            if (code < 200 || code > 300) {
                throw new Exception(
                        "Http request to '" + url + "' failed. Response code: " + code + ".");
            }

            return pokemonResponse;

        } else {
            throw new Exception("No Pokémon name provided.");
        }
    }

}
