package services.common;

import com.sun.net.httpserver.HttpExchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class HttpUtils {
    private static final Logger LOGGER = LogManager.getLogger(HttpUtils.class);

    public static final String HOST_ADDRESS_LOCAL = "localhost";

    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String DELETE = "DELETE";

    public static final String CONTENT_TYPE = "Content-type";
    public static final String CONTENT_LENGTH = "Content-length";
    public static final String APPLICATION_JSON = "application/json";

    public static void sendHttpResponse(HttpExchange exchange, int code) throws IOException {
        sendHttpResponse(exchange, code, new byte[]{});
    }

    public static void sendHttpResponse(HttpExchange exchange, int code, byte[] body) throws IOException {
        LOGGER.trace("Preparing Http Response... ");
        int bodyLength = body.length;
        exchange.getResponseHeaders().add(HttpUtils.CONTENT_TYPE, HttpUtils.APPLICATION_JSON);

        try {
            exchange.sendResponseHeaders(code, bodyLength);
            LOGGER.trace("Sending Http Response " + code);

            if (bodyLength > 0) {
                exchange.getResponseHeaders().add(HttpUtils.CONTENT_LENGTH, Integer.toString(bodyLength));
                LOGGER.trace("Body: " + new String(body));
                OutputStream os = exchange.getResponseBody();
                try {
                    os.write(body);
                    LOGGER.trace("Response body writing " + bodyLength);
                } finally {
                    os.close();
                }
            }
        } finally {
            exchange.close();
        }
    }
}
