package com.hit.client;

import com.hit.dm.User;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserClient extends BaseClient {

    public UserClient(String host, int port) {
        super(host, port);
    }

    public boolean createUser(String userName, String password,
                              User.Role role) throws IOException {
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

    public boolean editUser(String editorName, String userName, String oldPassword,
                            String newPassword, User.Role role) throws IOException {
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

    public boolean removeUser(String removerName, String userName,
                              String password) throws IOException {
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
}
