package org.truelayer.pokedex.util;

import org.eclipse.jetty.http.pathmap.PathSpec;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.PathMappingsHandler;
import org.eclipse.jetty.util.VirtualThreads;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.truelayer.pokedex.entrypoint.restapi.handler.ApiErrorHandler;

import java.util.Map;


public class TestHttpServer {

    private static final int PORT = 8011;
    private static Server server;

    /**
     * Start http test server
     * 
     * @param servlets
     * @throws Exception
     */
    public static void start(Map<String, Handler> servlets) throws Exception {
		QueuedThreadPool threadPool = new QueuedThreadPool(10);
		threadPool.setVirtualThreadsExecutor(VirtualThreads.getDefaultVirtualThreadsExecutor());
		server = new Server(threadPool);

		final ServerConnector connector = new ServerConnector(server);
		connector.setPort(PORT);
		server.addConnector(connector);

		PathMappingsHandler root = new PathMappingsHandler();
		for (final String path : servlets.keySet()) {
			root.addMapping(PathSpec.from(path), servlets.get(path));
		}

		server.setHandler(root);
		// generic error handler
		server.setErrorHandler(new ApiErrorHandler());

		server.start();
		if (server.isStarted()) {
			System.out.println("Test server is started on port " + PORT);
		}
    }

    /**
     * Stop http test server
     * 
     * @throws Exception
     */
    public static void stop() throws Exception {
		server.stop();
    }

}
