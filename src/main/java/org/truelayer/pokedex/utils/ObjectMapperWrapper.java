package org.truelayer.pokedex.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
/**
 * Wrapper for Jackson ObjectMapper to reuse across invocations for improved
 * performance, as suggested by Jackson docs.
 * From the Jackson docs: "Mapper instances are fully thread-safe provided that
 * ALL configuration of the instance occurs before ANY read or write calls."
 * This class only exposes ObjectReaders and ObjectWriters which are "cheap to
 * construct and their configuration can be safely changed in a thread-safe
 * manner"
 *
 */

public class ObjectMapperWrapper {

  private static ObjectMapper mapper = null;

  public static ObjectReader getReader() {
    return _getMapper().reader();
  }

  public static ObjectWriter getWriter() {
    return _getMapper().writer();
  }

  private static ObjectMapper _getMapper() {
    if (mapper == null) {
      mapper = new ObjectMapper();
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
      mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }
    return mapper;
  }
}
