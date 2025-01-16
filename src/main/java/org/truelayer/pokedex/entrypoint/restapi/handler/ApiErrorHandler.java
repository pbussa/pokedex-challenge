package org.truelayer.pokedex.entrypoint.restapi.handler;

import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.truelayer.pokedex.model.ApplicationError;
import org.truelayer.pokedex.utils.ExceptionUtils;
import org.truelayer.pokedex.utils.ObjectMapperWrapper;

import java.nio.ByteBuffer;

public class ApiErrorHandler extends ErrorHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiErrorHandler.class.getName());

    @Override
    public boolean handle(Request request, Response response, Callback callback) {
        int code = response.getStatus();
        Throwable cause = (Throwable) request.getAttribute(ERROR_EXCEPTION);
        String message = cause != null ? ExceptionUtils.concatenateAllCause(cause)
                : (String) request.getAttribute(ERROR_MESSAGE);

        LOGGER.error(message, cause);

        ApplicationError error = new ApplicationError();
        error.setMessage(message);
        error.setStatus(code);
        returnError(response, callback, error, code);

        return true;
    }

    /**
     * Return unauthorized error (401)
     *
     * @param response
     * @param callback
     * @param t
     */
    public static void unauthorized(Response response, Callback callback, Throwable t) {
        LOGGER.error("Unauthorized", t);
        final ApplicationError error = new ApplicationError();
        error.setMessage(ExceptionUtils.getRootCause(t).getMessage());
        error.setStatus(401);
        returnError(response, callback, error, HttpStatus.UNAUTHORIZED_401);
    }

    /**
     * Return internal server error (500)
     *
     * @param response
     * @param callback
     * @param t
     */
    public static void internalServerError(Response response, Callback callback, Throwable t) {
        LOGGER.error("Internal Server Error", t);
        final ApplicationError error = new ApplicationError();
        error.setMessage(ExceptionUtils.getRootCause(t).getMessage());
        error.setStatus(500);
        returnError(response, callback, error, HttpStatus.INTERNAL_SERVER_ERROR_500);
    }

    /**
     * Return bad request error (400)
     *
     * @param response
     * @param callback
     * @param message
     */
    public static void badRequest(Response response, Callback callback, String message) {
        LOGGER.error("Bad Request: {}", message);
        final ApplicationError error = new ApplicationError();
        error.setMessage(message);
        error.setStatus(400);
        returnError(response, callback, error, HttpStatus.BAD_REQUEST_400);
    }

    /**
     * Return not found error (404)
     *
     * @param response
     * @param callback
     * @param message
     */
    public static void notFound(Response response, Callback callback, String message) {
        LOGGER.error("Not Found: {}", message);
        final ApplicationError error = new ApplicationError();
        error.setMessage(message);
        error.setStatus(404);
        returnError(response, callback, error, HttpStatus.NOT_FOUND_404);
    }

    private static void returnError(Response response, Callback callback, ApplicationError error, int status) {
        response.getHeaders().put(HttpHeader.CONTENT_TYPE, "application/json;charset=UTF-8");
        response.getHeaders().put(HttpHeader.CACHE_CONTROL, "no-store");

        response.setStatus(status);

        try {
            final byte[] bytes = ObjectMapperWrapper.getWriter().writeValueAsBytes(error.getMessage());
            response.write(true, ByteBuffer.wrap(bytes), callback);
        } catch (Exception e) {
            response.write(true, ByteBuffer.wrap("{\"error\": \"unable to serialize error message\"}".getBytes()),
                    callback);
        }

    }

}
