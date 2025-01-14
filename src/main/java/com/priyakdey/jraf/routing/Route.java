package com.priyakdey.jraf.routing;

import com.priyakdey.jraf.http.HttpMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a route in the REST framework, including its pattern, HTTP method,
 * and path variable placeholders.
 * <p>
 * A {@code Route} maps a user-friendly URI template (e.g., {@code /profiles/
 * {id}})
 * to a path pattern for matching and extracting path variables dynamically.
 * </p>
 *
 * <h2>Usage Example:</h2>
 *
 * <pre>
 * {@code
 * Route route = Route.toRoute("/profiles/{id}", HttpMethod.GET);
 *
 * String uri = "/profiles/123";
 * Map<String, String> pathVars = new HashMap<>();
 * if (route.matches(uri, pathVars)) {
 *     System.out.println("Matched! Path variables: " + pathVars);
 * }
 * }
 * </pre>
 *
 * @param path               The path pattern used to match URIs.
 * @param method              The HTTP method associated with this route.
 * @param pathVarPlaceHolders The list of path variable placeholders in the
 *                            route template.
 * @author Priyak Dey
 */
public record Route(String path, HttpMethod method,
                    List<String> pathVarPlaceHolders) {

    /**
     * Converts a URI template to a {@code Route} by dynamically generating a
     * path pattern
     * and extracting path variable placeholders.
     * <p>
     * For example, a template {@code /profiles/{id}} will be converted to
     * the path
     * {@code ^/profiles/(?&lt;id&gt;[^/]+)/?$} and will extract the
     * placeholder {@code id}.
     * </p>
     *
     * @param path   The URI template containing placeholders (e.g., {@code
     *               /profiles/{id}}).
     * @param method The HTTP method associated with this route.
     * @return A {@code Route} object containing the generated path, HTTP
     * method, and placeholders.
     */
    public static Route toRoute(String path, HttpMethod method) {
        List<String> placeholderNames = new ArrayList<>();

        Pattern placeholderPattern = Pattern.compile("\\{(\\w+)}");
        Matcher matcher = placeholderPattern.matcher(path);

        StringBuilder regexBuilder = new StringBuilder();
        while (matcher.find()) {
            String placeholder = matcher.group(1);
            placeholderNames.add(placeholder);
            matcher.appendReplacement(regexBuilder,
                    "(?<" + placeholder + ">[^/]+)");
        }
        matcher.appendTail(regexBuilder);
        String regex = "^" + regexBuilder + "/?$";
        return new Route(regex, method, placeholderNames);
    }

    /**
     * Matches the given URI against the route's path pattern and extracts
     * path variables.
     * <p>
     * If the URI matches the route's path, the method populates the {@code
     * pathVars} map
     * with the extracted path variable values.
     * </p>
     *
     * @param uri      The URI to be matched (e.g., {@code /profiles/123}).
     * @param pathVars A map to store extracted path variables. The map will
     *                 be updated with
     *                 key-value pairs where keys are placeholder names and
     *                 values are their
     *                 corresponding values from the URI.
     * @return {@code true} if the URI matches the route's path, otherwise
     * {@code false}.
     */
    public boolean matches(String uri, HttpMethod method,
                           Map<String, String> pathVars) {
        if (method != this.method) return false;

        Pattern pattern = Pattern.compile(this.path());
        Matcher matcher = pattern.matcher(uri);

        if (!matcher.matches()) return false;

        for (String pathVarPlaceHolder : this.pathVarPlaceHolders()) {
            pathVars.put(pathVarPlaceHolder, matcher.group(pathVarPlaceHolder));
        }

        return true;
    }

}
