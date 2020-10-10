package services;

import com.sun.net.httpserver.HttpServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.handlers.MatchHandler;
import services.handlers.UserHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static services.common.HttpUtils.HOST_ADDRESS_LOCAL;
import static services.common.Ports.PORT_MAIN;

public class MainService {
    private static final Logger LOGGER = LogManager.getLogger(MainService.class);

    public static void main(String[] args) {
        int port = PORT_MAIN;
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(HOST_ADDRESS_LOCAL, port), 0);
            server.createContext("/users", new UserHandler());
            server.createContext("/matches", new MatchHandler());
            server.setExecutor(threadPoolExecutor);
            server.start();
            LOGGER.info("Main service starting on port: " + port);
        } catch (IOException e) {
            LOGGER.error("Problem creating HttpServer", e);
        }
    }
}
