package org.truelayer.pokedex.entrypoint.restapi.handler;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.truelayer.pokedex.caller.PokeApiCaller;
import org.truelayer.pokedex.model.Pokemon;
import org.truelayer.pokedex.model.PokemonResponse;
import org.truelayer.pokedex.utils.ObjectMapperWrapper;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Handler for the pokemon endpoint.
 */

public class PokemonHandler extends Handler.Abstract {

	private static final Logger LOGGER = LoggerFactory.getLogger(PokemonHandler.class.getName());
	private final PokeApiCaller pokeApiCaller;

	public PokemonHandler(PokeApiCaller pokeApiCaller) {
		this.pokeApiCaller = pokeApiCaller;
	}

	@Override
	public boolean handle(Request request, Response response, Callback callback) throws Exception {
		OutputStream out = Response.asBufferedOutputStream(request, response);
		// Extract pokemon name
		String path = request.getHttpURI().getPath();
		String[] pathSegments = path.split("/");

		String pokemonName;
		if (path.endsWith("/pokemon/") || path.endsWith("/pokemon")) {
			pokemonName = "";
		} else {
			pokemonName = pathSegments.length > 2 ? pathSegments[pathSegments.length - 1] : "";
		}

		if (pokemonName.isEmpty()) {
			ApiErrorHandler.badRequest(response, callback, "Missing pokemon name");
			return true;
		}
        LOGGER.info("Calling PokeAPI for pokemon: {}", pokemonName);
		PokemonResponse pokemonResponse;
		
		try {
			pokemonResponse = pokeApiCaller.getPokemonByName(pokemonName);
		} catch (Exception e) {
			LOGGER.error("Error calling PokeAPI for pokemon: {}", pokemonName, e);
			ApiErrorHandler.internalServerError(response, callback, e);
			return true;
		}
		LOGGER.info("Received response from PokeAPI for pokemon: {}", pokemonName);

		Pokemon pokemon = new Pokemon(pokemonResponse.getName(), pokemonResponse.getEnglishDescription(), pokemonResponse.getHabitat() != null ? pokemonResponse.getHabitat().getName() : "Unknown", pokemonResponse.isLegendary());
		out.write(ObjectMapperWrapper.getWriter().writeValueAsBytes(pokemon));
		out.flush();
		// Write the response
		response.setStatus(200);
		response.write(true, null, callback);
		return true;
	}

}
