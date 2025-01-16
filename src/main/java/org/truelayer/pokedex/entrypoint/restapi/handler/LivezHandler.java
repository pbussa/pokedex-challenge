package org.truelayer.pokedex.entrypoint.restapi.handler;

import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class LivezHandler extends Handler.Abstract.NonBlocking {

    @Override
    public boolean handle(Request request, Response response, Callback callback) {
        response.setStatus(HttpStatus.OK_200);
        response.write(false, ByteBuffer.wrap("{\"status\":\"ok\"}".getBytes(StandardCharsets.UTF_8)), callback);
        return true;
    }
}
