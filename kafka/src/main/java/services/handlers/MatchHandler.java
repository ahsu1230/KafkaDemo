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
import services.consumers.MatchConsumer;
import services.entities.Match;
import services.producers.MatchProducer;
import services.stores.MatchStore;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public class MatchHandler implements HttpHandler {
    private static final Logger LOGGER = LogManager.getLogger(MatchHandler.class);

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        LOGGER.trace("Received handle " + exchange.getRequestURI().getPath());
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
        if (path.equals("/matches/start")) {
            handlePostStartMatch(exchange);
        } else if (path.equals("/matches/end")) {
            handlePostEndMatch(exchange);
        } else {
            handlePathNotFound(exchange);
        }
    }

    private void handleGetAllMatches(HttpExchange exchange) {
        ObjectMapper objectMapper = ObjectMapperUtils.getObjectMapper();
        MatchConsumer consumer = new MatchConsumer();
        try {
            consumer.consume();
            List<Match> matches = MatchStore.getAllMatches();
            HttpUtils.sendHttpResponse(exchange, 200, objectMapper.writeValueAsBytes(matches));
        } catch (JsonProcessingException e) {
            handleError(exchange, e, "Error serializing JSON", 500);
        } catch (IOException e) {
            handleError(exchange, e, "Error sending http response", 500);
        }
    }

    private void handlePostStartMatch(HttpExchange exchange) {
        ObjectMapper objectMapper = ObjectMapperUtils.getObjectMapper();
        MatchProducer producer = new MatchProducer();
        try (InputStream is = exchange.getRequestBody()) {
            Match match = objectMapper.readValue(is, Match.class);
            producer.produce(match);
            HttpUtils.sendHttpResponse(exchange, 200);
        } catch (JsonParseException e) {
            handleError(exchange, e, "Error parsing JSON", 400);
        } catch (JsonMappingException e) {
            handleError(exchange, e, "Error deserializing JSON", 400);
        } catch (IOException e) {
            handleError(exchange, e, "Error reading request body", 400);
        } finally {
            producer.close();
        }
    }

    private void handlePostEndMatch(HttpExchange exchange) {
        Map<String, String> queryMap = HttpUtils.queryToMap(exchange.getRequestURI().getQuery());
        if (!queryMap.containsKey("matchId")) {
            LOGGER.info("matchId not found");
            try {
                HttpUtils.sendHttpResponse(exchange, 400);
            } catch(IOException e) {
                handleError(exchange, e, "Error sending http response", 500);
            }
            return;
        }

        String matchId = queryMap.get("matchId");
        Match match = MatchStore.getMatch(matchId);
        match.endedAt = Date.from(Instant.now());

        MatchProducer producer = new MatchProducer();
        try {
            producer.produce(match);
            HttpUtils.sendHttpResponse(exchange, 200);
        } catch (IOException e) {
            handleError(exchange, e, "Error reading request Body", 400);
        } finally {
            producer.close();
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
