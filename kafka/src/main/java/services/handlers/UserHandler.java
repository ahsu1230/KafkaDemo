package services.handlers;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.common.HttpUtils;
import services.common.ObjectMapperUtils;
import services.consumers.UserConsumer;
import services.entities.User;
import services.producers.UserProducer;
import services.stores.UserStore;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

public class UserHandler implements HttpHandler {
    private static final Logger LOGGER = LogManager.getLogger(UserHandler.class);

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case HttpUtils.GET:
                handleGet(exchange);
                break;
            case HttpUtils.POST:
                handlePost(exchange);
                break;
            default:
                LOGGER.warn("Unsupported method " + method);
                handlePathNotFound(exchange);
        }
    }

    private void handleGet(HttpExchange exchange) {
        URI uri = exchange.getRequestURI();
        String path = uri.getPath();
        LOGGER.trace("Path: " + path);
        if (path.equals("/users/all")) {
            handleGetAllUsers(exchange);
        } else {
            handlePathNotFound(exchange);
        }
    }

    private void handlePost(HttpExchange exchange) {
        URI uri = exchange.getRequestURI();
        String path = uri.getPath();
        LOGGER.trace("Path: " + path);
        if (path.equals("/users/upsert")) {
            handlePostUpsertUser(exchange);
        } else {
            handlePathNotFound(exchange);
        }
    }

    private void handleGetAllUsers(HttpExchange exchange) {
        ObjectMapper objectMapper = ObjectMapperUtils.getObjectMapper();
        LOGGER.trace("Before Init");
        UserConsumer consumer = new UserConsumer();
        LOGGER.trace("After Init");
        try {
            LOGGER.trace("Before Consume");
            consumer.consume();
            LOGGER.trace("After Consume");
            List<User> users = UserStore.getAllUsers();
            LOGGER.trace("After Store");
            HttpUtils.sendHttpResponse(exchange, 200, objectMapper.writeValueAsBytes(users));
        } catch (JsonProcessingException e) {
            handleError(exchange, e, "Error serializing JSON", 500);
        } catch (IOException e) {
            handleError(exchange, e, "Error sending HTTP Response", 500);
        } finally {
            LOGGER.trace("Consumer closed");
            consumer.close();
        }
    }

    private void handlePostUpsertUser(HttpExchange exchange) {
        ObjectMapper objectMapper = ObjectMapperUtils.getObjectMapper();
        UserProducer producer = new UserProducer();
        try (InputStream is = exchange.getRequestBody()) {
            User user = objectMapper.readValue(is, User.class);
            producer.produce(user);
            HttpUtils.sendHttpResponse(exchange, 200);
        } catch (JsonParseException e) {
            handleError(exchange, e, "Error parsing JSON", 400);
        } catch (JsonMappingException e) {
            handleError(exchange, e, "Error deserializing JSON", 400);
        } catch (IOException e) {
            handleError(exchange, e, "Error reading request Body", 400);
        } finally {
            producer.close();
        }
    }

    private void handlePathNotFound(HttpExchange exchange) {
        LOGGER.error("Path not found! " + exchange.getRequestURI().getPath());
        try {
            HttpUtils.sendHttpResponse(exchange, 404);
        } catch (IOException e) {
            LOGGER.error("Exception from sending Http Response", e);
        }
    }

    private void handleError(HttpExchange exchange, Throwable e, String message, int code) {
        try {
            LOGGER.error(message, e);
            HttpUtils.sendHttpResponse(exchange, code);
        } catch (IOException ioe) {
            LOGGER.error("Exception from sending Http Response", ioe);
        }
    }
}
