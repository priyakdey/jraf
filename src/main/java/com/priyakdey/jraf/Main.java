package com.priyakdey.jraf;

import com.priyakdey.jraf.http.HttpMethod;
import com.priyakdey.jraf.routing.Route;
import com.priyakdey.jraf.routing.handler.RouteHandler;
import com.priyakdey.jraf.routing.Router;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Priyak Dey
 */
public class Main {

    public static void main(String[] args) throws IOException {
        /* register all routes */
        String path1 = "/profiles";
        String path2 = "/profiles/{id}";
        String path3 = "/profiles/{id}/settings";

        Route route1 = Route.toRoute(path1, HttpMethod.GET);
        Route route2 = Route.toRoute(path2, HttpMethod.GET);
        Route route3 = Route.toRoute(path3, HttpMethod.GET);

        RouteHandler handler1 = createRouteHandler(path1);
        RouteHandler handler2 = createRouteHandler(path2);
        RouteHandler handler3 = createRouteHandler(path3);

        Router router = new Router();
        router.register(route1, handler1);
        router.register(route2, handler2);
        router.register(route3, handler3);

        /* start the server */
        int port = 8080;
        InetSocketAddress address = new InetSocketAddress("127.0.0.1", port);
        HttpServer server = HttpServer.create(address, 0);

        server.createContext("/", (exchange) -> {
            String path = exchange.getRequestURI().toString();
            HttpMethod method =
                    HttpMethod.toHttpMethod(exchange.getRequestMethod());
            Map<String, String> pathVars = new HashMap<>();
            RouteHandler handler = router.getRouteHandler(path, method,
                    pathVars);
            handler.handle(exchange, null);
        });

        System.out.printf("Starting server and listening to port: %d...%n",
                port);

        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.stop(0);
            System.out.println("Server stopped.");
        }));
    }

    private static RouteHandler createRouteHandler(String path) {
        return (exchange, r) -> {
            StringBuilder buf = new StringBuilder();
            buf.append(String.format("Hello from %s API%n", path));

            try (exchange; OutputStream out = exchange.getResponseBody()) {
                exchange.getResponseHeaders().add("Connection", "close");
                exchange.sendResponseHeaders(200, buf.length());

                out.write(buf.toString().getBytes());
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }
}
