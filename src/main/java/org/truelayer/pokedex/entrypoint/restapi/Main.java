package org.truelayer.pokedex.entrypoint.restapi;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.http.UriCompliance;
import org.eclipse.jetty.http.pathmap.PathSpec;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.PathMappingsHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.ResourceFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.truelayer.pokedex.caller.FunTranslationsCaller;
import org.truelayer.pokedex.caller.PokeApiCaller;
import org.truelayer.pokedex.configuration.ServerConfiguration;
import org.truelayer.pokedex.entrypoint.restapi.handler.*;

import java.net.URISyntaxException;
import java.util.Objects;
import java.util.concurrent.Executors;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final String V1 = "v1";
    private static final String API_V1 = "/api/" + V1;

    private static final String POKEMON = "/pokemon/*";
    private static final String POKEMON_TRANSLATED = "/pokemon/translated/*";

    private static final String SWAGGER = "/*";
    private static final String LIVEZ = "/livez";
    private static final String READYZ = "/readyz";

    public static void main(String[] args) throws Exception {
        // load configuration
        ServerConfiguration configuration = ServerConfiguration.getInstance();

        Server server = createServer(configuration);

        PathMappingsHandler root = new PathMappingsHandler();
        PokeApiCaller pokeApiCaller = new PokeApiCaller();
        FunTranslationsCaller funTranslationsCaller = new FunTranslationsCaller();

        // setup handlers
        setupMonitoringAndHealthChecks(root);
        setupV1Mappings(root, pokeApiCaller, funTranslationsCaller);

        server.setHandler(root);
        server.setErrorHandler(new ApiErrorHandler());
        server.start();

        if (server.isStarted()) {
            LOGGER.info("Pokedex is started on port {}", configuration.getPort());
        }
        server.join();
    }

    private static Server createServer(ServerConfiguration configuration) throws Exception {
        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setVirtualThreadsExecutor(
                Executors.newThreadPerTaskExecutor(Thread.ofVirtual().name("v-thread-", 0).factory()));

        HttpConfiguration httpConfiguration = new HttpConfiguration();
        httpConfiguration.setUriCompliance(UriCompliance.from("RFC3986,AMBIGUOUS_PATH_SEPARATOR"));
        httpConfiguration.setSendServerVersion(false);

        Server server = new Server(threadPool);
        ServerConnector connector = new ServerConnector(server, new HttpConnectionFactory(httpConfiguration));
        connector.setPort(configuration.getPort());
        connector.setIdleTimeout(configuration.getIdleTimeout());
        server.addConnector(connector);

        return server;
    }

    private static void setupMonitoringAndHealthChecks(PathMappingsHandler root) {
        addHandler(root, LIVEZ, new CustomHeaderHandler(new LivezHandler()));
        addHandler(root, READYZ, new CustomHeaderHandler(new ReadyzHandler()));
    }

    private static void setupV1Mappings(PathMappingsHandler root, PokeApiCaller pokeApiCaller, FunTranslationsCaller funTranslationsCaller) {
        addHandler(root, API_V1 + POKEMON, (new CustomHeaderHandler(new PokemonHandler(pokeApiCaller))));
        addHandler(root, API_V1 + POKEMON_TRANSLATED, (new CustomHeaderHandler(new TranslationHandler(pokeApiCaller, funTranslationsCaller))));
    }

    private static void addHandler(PathMappingsHandler root, String path, Handler handler) {
        root.addMapping(PathSpec.from(path), handler);
    }
}