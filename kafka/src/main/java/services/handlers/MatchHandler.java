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
import services.entities.Match;
import services.stores.MatchStore;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.sql.Date;
import java.time.Instant;
import java.util.List;

public class MatchHandler implements HttpHandler {
    private static final Logger LOGGER = LogManager.getLogger(MatchHandler.class);

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
        if (path.equals("/matches/all")) {
            handleGetAllMatches(exchange);
        } else {
            handlePathNotFound(exchange);
        }
    }

    private void handlePost(HttpExchange exchange) {
        URI uri = exchange.getRequestURI();
        String path = uri.getPath();
        if (path.equals("/matches/end")) {
            handlePostEndMatch(exchange);
        } else {
            handlePathNotFound(exchange);
        }
    }

    private void handleGetAllMatches(HttpExchange exchange) {
        ObjectMapper objectMapper = ObjectMapperUtils.getObjectMapper();
        List<Match> matches = MatchStore.getAllMatches();
        try {
            HttpUtils.sendHttpResponse(exchange, 200, objectMapper.writeValueAsBytes(matches));
        } catch (JsonProcessingException e) {
            handleError(exchange, e, "Error serializing JSON", 500);
        } catch (IOException e) {
            handleError(exchange, e, "Error sending http response", 500);
        }
    }

    private void handlePostEndMatch(HttpExchange exchange) {
        ObjectMapper objectMapper = ObjectMapperUtils.getObjectMapper();
        try (InputStream is = exchange.getRequestBody()) {
            Match match = objectMapper.readValue(is, Match.class);
            match.endedAt = Date.from(Instant.now());
            MatchStore.upsertMatch(match);
            HttpUtils.sendHttpResponse(exchange, 200);
        } catch (JsonParseException e) {
            handleError(exchange, e, "Error parsing JSON", 400);
        } catch (JsonMappingException e) {
            handleError(exchange, e, "Error deserializing JSON", 400);
        } catch (IOException e) {
            handleError(exchange, e, "Error reading request Body", 400);
        }
    }

    private void handlePathNotFound(HttpExchange exchange) {
        try {
            HttpUtils.sendHttpResponse(exchange, 404);
        } catch (IOException e) {
            LOGGER.error("Error sending http response", e);
        }
    }

    private void handleError(HttpExchange exchange, Throwable e, String message, int code) {
        LOGGER.error(message, e);
        try {
            HttpUtils.sendHttpResponse(exchange, code);
        } catch (IOException ioe) {
            LOGGER.error("Error sending http response", e);
        }
    }
}
