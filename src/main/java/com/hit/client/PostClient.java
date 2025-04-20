package com.hit.client;

import com.hit.dm.Comment;
import com.hit.dm.Post;
import com.hit.dm.SearchResult;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.reflect.TypeToken;

public class PostClient extends BaseClient {

    public PostClient(String host, int port) {
        super(host, port);
    }

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

    public boolean editPost(Long postId, String title, String userName,
                            String content) throws IOException {
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
}
