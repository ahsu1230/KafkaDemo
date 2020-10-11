package services.common;

import com.sun.net.httpserver.HttpExchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class HttpUtils {
    private static final Logger LOGGER = LogManager.getLogger(HttpUtils.class);

    public static final String HOST_ADDRESS_LOCAL = "localhost";

    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String DELETE = "DELETE";

    public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    public static final String CONTENT_TYPE = "Content-type";
    public static final String CONTENT_LENGTH = "Content-length";
    public static final String APPLICATION_JSON = "application/json";

    public static Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            }else{
                result.put(entry[0], "");
            }
        }
        return result;
    }

    public static void sendHttpResponse(HttpExchange exchange, int code) throws IOException {
        sendHttpResponse(exchange, code, new byte[]{});
    }

    public static void sendHttpResponse(HttpExchange exchange, int code, byte[] body) throws IOException {
        int bodyLength = body.length;
        exchange.getResponseHeaders().add(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        exchange.getResponseHeaders().add(HttpUtils.CONTENT_TYPE, HttpUtils.APPLICATION_JSON);

        try {
            exchange.sendResponseHeaders(code, bodyLength);
            LOGGER.trace("Http Response " + code);

            if (bodyLength > 0) {
                exchange.getResponseHeaders().add(HttpUtils.CONTENT_LENGTH, Integer.toString(bodyLength));
                LOGGER.trace("Body: " + new String(body));
                OutputStream os = exchange.getResponseBody();
                try {
                    os.write(body);
                } finally {
                    os.close();
                }
            }
        } finally {
            exchange.close();
        }
    }
}
