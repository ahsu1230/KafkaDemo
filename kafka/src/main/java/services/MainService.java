package services;

import com.sun.net.httpserver.HttpServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.handlers.MatchHandler;
import services.handlers.UserHandler;
import services.streams.MatchToMatchStream;
import services.streams.MatchToUserStream;
import services.streams.UserStream;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static services.common.HttpUtils.HOST_ADDRESS_LOCAL;
import static services.common.Ports.PORT_MAIN;

public class MainService {
    private static final Logger LOGGER = LogManager.getLogger(MainService.class);

    public static void main(String[] args) {
        int port = PORT_MAIN;
        final CountDownLatch latch = new CountDownLatch(1);
        final UserStream userStream = new UserStream();
        final MatchToMatchStream matchToMatchStream = new MatchToMatchStream();
        final MatchToUserStream matchToUserStream = new MatchToUserStream();
        final ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

        Runtime.getRuntime().addShutdownHook(new Thread("main-service-shutdown-hook") {
            @Override
            public void run() {
                LOGGER.info("Process shutting down...");
                LOGGER.info("Closing all streams.");
                boolean cleanUp = false;    // Enable this as true if you want to clear your stream states.
                matchToMatchStream.close(cleanUp);
                matchToUserStream.close(cleanUp);
                userStream.close(cleanUp);
                latch.countDown();
            }
        });

        try {
            // Start all streams
            userStream.start();
            matchToUserStream.start();
            matchToMatchStream.start();

            // Start webserver
            HttpServer server = HttpServer.create(new InetSocketAddress(HOST_ADDRESS_LOCAL, port), 0);
            server.createContext("/users", new UserHandler());
            server.createContext("/matches", new MatchHandler());
            server.setExecutor(threadPoolExecutor);
            server.start();
            LOGGER.info("Main service starting on port: " + port);
            latch.await();
            LOGGER.info("Server closed");
        } catch (IOException e) {
            LOGGER.error("Problem creating HttpServer", e);
        } catch (InterruptedException e) {
            LOGGER.info("Service has been interrupted.");
        }
    }
}
