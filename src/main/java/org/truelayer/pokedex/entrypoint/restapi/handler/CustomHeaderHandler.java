package org.truelayer.pokedex.entrypoint.restapi.handler;

import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;

public class CustomHeaderHandler extends Handler.Abstract {
	
    private final Abstract nextHandler;

    public CustomHeaderHandler(Abstract nextHandler) {
        this.nextHandler = nextHandler;
    }
    
    @Override
    public boolean handle(Request request, Response response, Callback callback) throws Exception {
    	
    	response.getHeaders().put(HttpHeader.CONTENT_TYPE, "application/json;charset=UTF-8");
        response.getHeaders().put(HttpHeader.CACHE_CONTROL, "no-store");

	    nextHandler.handle(request, response, callback);
		return true;
    }
    
}

