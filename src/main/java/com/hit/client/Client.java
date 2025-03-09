package com.hit.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hit.dm.Comment;
import com.hit.dm.Post;
import com.hit.dm.SearchResult;
import com.hit.dm.User;

import java.io.*;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client {
    private static Client instance;

    private final String host;
    private final int port;
    private final Gson gson;

    private Client(String host, int port) {
        this.host = host;
        this.port = port;
        this.gson = new Gson();
    }

    /**
     * Initialize the singleton instance of Client with the host and port.
     *
     * @param host the server host
     * @param port the server port
     */
    public static synchronized void initializeInstance(String host, int port) {
        instance = new Client(host, port);
    }

    /**
     * Returns the singleton instance if it was already initialized.
     *
     * @return the singleton instance of Client
     * @throws IllegalStateException if the instance has not been initialized yet
     */
    public static Client getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Client is not initialized. Call initializeInstance(host, port) first.");
        }
        return instance;
    }

    /**
     * Opens a socket connection, sends the given Request (serialized as JSON),
     * reads the JSON response, and converts it into a Response object.
     */
    private Response sendRequest(Request request) throws IOException {
        try (Socket socket = new Socket(host, port);
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String jsonRequest = gson.toJson(request);
            writer.println(jsonRequest);
            // Send an extra empty line to signal the end of the request.
            writer.println();
            String jsonResponse = reader.readLine();
            return gson.fromJson(jsonResponse, Response.class);
        }
    }

    // ====================================================
    // User Service Methods
    // ====================================================

    public boolean createUser(String userName, String password, User.Role role) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("userName", userName);
        body.put("password", password);
        body.put("role", role.name());
        Request request = new Request("user/create", body);
        Response response = sendRequest(request);
        if (response.getStatus() == 200) {
            return (Boolean) response.getBody().get("result");
        } else {
            throw new IOException("Error creating user: " + response.getBody());
        }
    }

    public boolean editUser(String editorName, String userName, String oldPassword, String newPassword, User.Role role) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("editorName", editorName);
        body.put("userName", userName);
        body.put("oldPassword", oldPassword);
        body.put("newPassword", newPassword);
        body.put("newRole", role.name());
        Request request = new Request("user/edit", body);
        Response response = sendRequest(request);
        if (response.getStatus() == 200) {
            return (Boolean) response.getBody().get("result");
        } else {
            throw new IOException("Error editing user: " + response.getBody());
        }
    }

    public boolean removeUser(String removerName, String userName, String password) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("removerName", removerName);
        body.put("userName", userName);
        body.put("password", password);
        Request request = new Request("user/remove", body);
        Response response = sendRequest(request);
        if (response.getStatus() == 200) {
            return (Boolean) response.getBody().get("result");
        } else {
            throw new IOException("Error removing user: " + response.getBody());
        }
    }

    public boolean authenticateUser(String userName, String password) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("userName", userName);
        body.put("password", password);
        Request request = new Request("user/authenticate", body);
        Response response = sendRequest(request);
        if (response.getStatus() == 200) {
            return (Boolean) response.getBody().get("result");
        } else {
            throw new IOException("Error authenticating user: " + response.getBody());
        }
    }

    public User getUser(String userName, String password) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("userName", userName);
        body.put("password", password);
        Request request = new Request("user/get", body);
        Response response = sendRequest(request);
        if (response.getStatus() == 200) {
            String jsonResult = gson.toJson(response.getBody().get("result"));
            return gson.fromJson(jsonResult, User.class);
        } else if (response.getStatus() == 404) {
            return null;
        } else {
            throw new IOException("Error getting user: " + response.getBody());
        }
    }

    // ====================================================
    // Post Service Methods
    // ====================================================

    public boolean createPost(String title, String userName, String content) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("title", title);
        body.put("userName", userName);
        body.put("content", content);
        Request request = new Request("post/create", body);
        Response response = sendRequest(request);
        if (response.getStatus() == 200) {
            return true;
        } else {
            throw new IOException("Error creating post: " + response.getBody());
        }
    }

    public boolean editPost(Long postId, String title, String userName, String content) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("postId", postId);
        body.put("title", title);
        body.put("userName", userName);
        body.put("content", content);
        Request request = new Request("post/edit", body);
        Response response = sendRequest(request);
        if (response.getStatus() == 200) {
            return (Boolean) response.getBody().get("result");
        } else {
            throw new IOException("Error editing post: " + response.getBody());
        }
    }

    public boolean removePost(Long postId, String userName) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("postId", postId);
        body.put("userName", userName);
        Request request = new Request("post/remove", body);
        Response response = sendRequest(request);
        if (response.getStatus() == 200) {
            return (Boolean) response.getBody().get("result");
        } else {
            throw new IOException("Error removing post: " + response.getBody());
        }
    }

    public Post getPostById(Long postId) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("postId", postId);
        Request request = new Request("post/get", body);
        Response response = sendRequest(request);
        if (response.getStatus() == 200) {
            String jsonResult = gson.toJson(response.getBody().get("result"));
            return gson.fromJson(jsonResult, Post.class);
        } else if (response.getStatus() == 404) {
            return null;
        } else {
            throw new IOException("Error getting post: " + response.getBody());
        }
    }

    public List<Post> getAllPosts() throws IOException {
        Request request = new Request("post/get-all", new HashMap<>());
        Response response = sendRequest(request);
        if (response.getStatus() == 200) {
            String jsonResult = gson.toJson(response.getBody().get("result"));
            Type listPostType = new TypeToken<List<Post>>() {
            }.getType();
            return gson.fromJson(jsonResult, listPostType);
        } else {
            throw new IOException("Error getting all posts: " + response.getBody());
        }
    }

    public List<Comment> getPostComments(Long postId) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("postId", postId);
        Request request = new Request("post/get-comments", body);
        Response response = sendRequest(request);
        if (response.getStatus() == 200) {
            String jsonResult = gson.toJson(response.getBody().get("result"));
            Type listCommentType = new TypeToken<List<Comment>>() {
            }.getType();
            return gson.fromJson(jsonResult, listCommentType);
        } else {
            throw new IOException("Error getting post comments: " + response.getBody());
        }
    }

    public SearchResult<Post> searchPostTitles(String searchPattern) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("searchPattern", searchPattern);
        Request request = new Request("post/search-titles", body);
        Response response = sendRequest(request);
        if (response.getStatus() == 200) {
            String jsonResult = gson.toJson(response.getBody().get("result"));
            Type postSearchResultType = new TypeToken<SearchResult<Post>>() {
            }.getType();
            return gson.fromJson(jsonResult, postSearchResultType);
        } else {
            throw new IOException("Error searching post titles: " + response.getBody());
        }
    }

    public SearchResult<Post> searchPostContents(String searchPattern) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("searchPattern", searchPattern);
        Request request = new Request("post/search-contents", body);
        Response response = sendRequest(request);
        if (response.getStatus() == 200) {
            String jsonResult = gson.toJson(response.getBody().get("result"));
            Type postSearchResultType = new TypeToken<SearchResult<Post>>() {
            }.getType();
            return gson.fromJson(jsonResult, postSearchResultType);
        } else {
            throw new IOException("Error searching post contents: " + response.getBody());
        }
    }

    // ====================================================
    // Comment Service Methods
    // ====================================================

    public boolean createComment(Long postId, String userName, String content) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("postId", postId);
        body.put("userName", userName);
        body.put("content", content);
        Request request = new Request("comment/create", body);
        Response response = sendRequest(request);
        if (response.getStatus() == 200) {
            return true;
        } else {
            throw new IOException("Error creating comment: " + response.getBody());
        }
    }

    public boolean editComment(Long commentId, String userName, String content) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("commentId", commentId);
        body.put("userName", userName);
        body.put("content", content);
        Request request = new Request("comment/edit", body);
        Response response = sendRequest(request);
        if (response.getStatus() == 200) {
            return (Boolean) response.getBody().get("result");
        } else {
            throw new IOException("Error editing comment: " + response.getBody());
        }
    }

    public boolean removeComment(Long commentId, String userName) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("commentId", commentId);
        body.put("userName", userName);
        Request request = new Request("comment/remove", body);
        Response response = sendRequest(request);
        if (response.getStatus() == 200) {
            return (Boolean) response.getBody().get("result");
        } else {
            throw new IOException("Error removing comment: " + response.getBody());
        }
    }

    public Comment getCommentById(Long commentId) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("commentId", commentId);
        Request request = new Request("comment/get", body);
        Response response = sendRequest(request);
        if (response.getStatus() == 200) {
            String jsonResult = gson.toJson(response.getBody().get("result"));
            return gson.fromJson(jsonResult, Comment.class);
        } else if (response.getStatus() == 404) {
            return null;
        } else {
            throw new IOException("Error getting comment: " + response.getBody());
        }
    }

    public List<Comment> getAllComments() throws IOException {
        Request request = new Request("comment/get-all", new HashMap<>());
        Response response = sendRequest(request);
        if (response.getStatus() == 200) {
            String jsonResult = gson.toJson(response.getBody().get("result"));
            Type listCommentType = new TypeToken<List<Comment>>() {
            }.getType();
            return gson.fromJson(jsonResult, listCommentType);
        } else {
            throw new IOException("Error getting all comments: " + response.getBody());
        }
    }

    public SearchResult<Comment> searchCommentContents(String searchPattern) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("searchPattern", searchPattern);
        Request request = new Request("comment/search-contents", body);
        Response response = sendRequest(request);
        if (response.getStatus() == 200) {
            String jsonResult = gson.toJson(response.getBody().get("result"));
            Type commentSearchResultType = new TypeToken<SearchResult<Comment>>() {
            }.getType();
            return gson.fromJson(jsonResult, commentSearchResultType);
        } else {
            throw new IOException("Error searching comment contents: " + response.getBody());
        }
    }
}
