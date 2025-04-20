package com.hit.client;

import com.hit.dm.Comment;
import com.hit.dm.SearchResult;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.reflect.TypeToken;

public class CommentClient extends BaseClient {

    public CommentClient(String host, int port) {
        super(host, port);
    }

    public boolean createComment(Long postId, String userName,
                                 String content) throws IOException {
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

    public boolean editComment(Long commentId, String userName,
                               String content) throws IOException {
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
