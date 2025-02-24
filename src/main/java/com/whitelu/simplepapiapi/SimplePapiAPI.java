package com.whitelu.simplepapiapi;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Pattern;

public final class SimplePapiAPI extends JavaPlugin {
    private HttpServer server;
    private final Gson gson = new Gson();
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("^\\[.*\\]$");

    @Override
    public void onEnable() {
        //启动HTTP服务器
        try {
            server = HttpServer.create(new InetSocketAddress(4747), 0);
            server.createContext("/", new PlaceholderHandler());
            server.setExecutor(null);
            server.start();
            getLogger().info("HTTP server started on port 4747");
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Failed to start HTTP server", e);
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        if (server != null) {
            server.stop(0);
            getLogger().info("HTTP server stopped");
        }
    }

    private class PlaceholderHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                if (!"GET".equals(exchange.getRequestMethod())) {
                    sendResponse(exchange, 405, "Method not allowed");
                    return;
                }

                URI requestURI = exchange.getRequestURI();
                String query = requestURI.getRawQuery();
                getLogger().info("Received raw query: " + query);

                if (query == null) {
                    sendResponse(exchange, 400, "Missing parameters");
                    return;
                }

                Map<String, String> params = parseQueryString(query);
                String target = params.get("target");
                String variableList = params.get("variable");

                getLogger().info("Parsed parameters - target: " + target + ", variables: " + variableList);

                if (target == null || variableList == null) {
                    sendResponse(exchange, 404, "Missing required parameters");
                    return;
                }

                if (!VARIABLE_PATTERN.matcher(variableList).matches()) {
                    sendResponse(exchange, 404, "Invalid variable format: " + variableList);
                    return;
                }

                String[] variables = variableList.substring(1, variableList.length() - 1).split(",");
                Map<String, String> results = new HashMap<>();

                for (String variable : variables) {
                    variable = variable.trim();
                    try {
                        String result = PlaceholderAPI.setPlaceholders(
                                Bukkit.getOfflinePlayer(target),
                                variable
                        );
                        results.put(variable, result.equals(variable) ? null : result);
                    } catch (Exception e) {
                        getLogger().log(Level.WARNING, "Error processing variable: " + variable, e);
                        results.put(variable, null);
                    }
                }

                String response = gson.toJson(results);
                getLogger().info(String.format("Request - target: %s, variables: %s", target, variableList));
                getLogger().info("Response: " + response);

                // 发送响应
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                sendResponse(exchange, 200, response);
            } catch (Exception e) {
                getLogger().log(Level.SEVERE, "Error handling request", e);
                sendResponse(exchange, 500, "Internal server error: " + e.getMessage());
            }
        }

        private Map<String, String> parseQueryString(String query) {
            Map<String, String> params = new HashMap<>();
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                if (idx > 0) {
                    String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
                    String value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);
                    params.put(key, value);
                }
            }
            return params;
        }

        private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
            byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(statusCode, responseBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        }
    }
} 