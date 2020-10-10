package services.common;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperUtils {
    private static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }
}
