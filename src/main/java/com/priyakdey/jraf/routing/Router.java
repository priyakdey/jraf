package com.priyakdey.jraf.routing;

import com.priyakdey.jraf.http.HttpMethod;
import com.priyakdey.jraf.routing.handler.RouteHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Priyak Dey
 */
public class Router {

    private final Map<Route, RouteHandler> routeHandlers;

    private final RouteHandler pathNotFoundHandler;
    private final RouteHandler methodNotSupportedHandler;

    private static final RouteHandler DEFAULT_PATH_NOT_FOUND_HANDLER
            = (exchange, route) -> {
        try (exchange) {
            exchange.getResponseHeaders().add("Connection", "close");
            exchange.sendResponseHeaders(404, -1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    };

    private static final RouteHandler DEFAULT_METHOD_NOT_SUPPORTED_HANDLER
            = (exchange, route) -> {
        try (exchange) {
            exchange.getResponseHeaders().add("Connection", "close");
            exchange.sendResponseHeaders(405, -1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    };

    public Router() {
        routeHandlers = new HashMap<>();
        pathNotFoundHandler = DEFAULT_PATH_NOT_FOUND_HANDLER;
        methodNotSupportedHandler = DEFAULT_METHOD_NOT_SUPPORTED_HANDLER;
    }


    public void register(Route route, RouteHandler routeHandler) {
        routeHandlers.put(route, routeHandler);
    }

    public RouteHandler getRouteHandler(String path, HttpMethod method,
                                        Map<String, String> pathVars) {
        // TODO: whats the point of a map if we have to iterate?
        // We cannot distinguish between PATH_NOT_FOUND or METHOD_NOT_SUPPORTED.
        // Passing the map and mutating is stupid, need to bring in context.
        for (Map.Entry<Route, RouteHandler> entry : routeHandlers.entrySet()) {
            if (entry.getKey().matches(path, method, pathVars)) {
                return entry.getValue();
            }
        }
        return pathNotFoundHandler;
    }

}
