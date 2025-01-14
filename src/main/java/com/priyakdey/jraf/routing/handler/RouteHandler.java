package com.priyakdey.jraf.routing.handler;

import com.priyakdey.jraf.routing.Route;
import com.sun.net.httpserver.HttpExchange;

/**
 * @author Priyak Dey
 */
public interface RouteHandler {

    // TODO: This will change later
    void handle(HttpExchange exchange, Route route);

}
