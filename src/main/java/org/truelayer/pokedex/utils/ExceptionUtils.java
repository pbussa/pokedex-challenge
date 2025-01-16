package org.truelayer.pokedex.utils;

import java.util.Objects;

/**
 * This class contains all exception util methods
 */
public class ExceptionUtils {

    /**
     * Return the root cause of an exception
     *
     * @param throwable
     * @return
     */
    public static Throwable getRootCause(Throwable throwable) {
        Objects.requireNonNull(throwable);
        Throwable rootCause = throwable;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }
        return rootCause;
    }

    /**
     * Concatenate all cause of a Throwable. Each message is separated by a new line.
     *
     * @param throwable the exception
     * @return full message
     */
    public static String concatenateAllCause(Throwable throwable) {
        Objects.requireNonNull(throwable);
        StringBuilder error = new StringBuilder(throwable.getMessage());
        if (!throwable.getMessage().endsWith(".")) {
            error.append(".");
        }
        Throwable rootCause = throwable;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
            // ignore null pointer exception because are insignificant
            if (rootCause.getMessage() != null) {
                error.append("\n").append(rootCause.getMessage());
                if (!rootCause.getMessage().endsWith(".")) {
                    error.append(".");
                }
            }
        }
        return error.toString();
    }

}
