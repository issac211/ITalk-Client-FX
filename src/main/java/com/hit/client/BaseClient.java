package com.hit.client;

import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;

public abstract class BaseClient {
    protected final String host;
    protected final int port;
    protected final Gson gson;

    protected BaseClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.gson = new Gson();
    }

    /**
     * Opens a socket connection, sends the given Request (serialized as JSON),
     * reads the JSON response, and converts it into a Response object.
     */
    protected Response sendRequest(Request request) throws IOException {
        try (Socket socket = new Socket(host, port);
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String jsonRequest = gson.toJson(request);
            writer.println(jsonRequest);
            writer.println(); // Signal end of request
            String jsonResponse = reader.readLine();
            return gson.fromJson(jsonResponse, Response.class);
        }
    }
}
