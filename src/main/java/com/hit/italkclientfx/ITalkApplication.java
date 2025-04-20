package com.hit.italkclientfx;

import com.hit.client.*;
import com.hit.italkclientfx.controllers.ITalkController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import java.util.*;

public class ITalkApplication extends Application {

    private SceneManager sceneManager;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Initialize global resources.
        UserClient userClient = new UserClient("localhost", 34567);
        PostClient postClient = new PostClient("localhost", 34567);
        CommentClient commentClient = new CommentClient("localhost", 34567);
        Map<String, BaseClient> clients = new HashMap<>();
        clients.put("userClient", userClient);
        clients.put("postClient", postClient);
        clients.put("commentClient", commentClient);
        AppStatus appStatus = new AppStatus();

        sceneManager = new SceneManager(primaryStage, clients);
        sceneManager.addScene("login", "ITalk-login.fxml");
        sceneManager.addScene("profile", "ITalk-profile.fxml");
        sceneManager.addScene("profileEdit", "ITalk-profileEdit.fxml");
        sceneManager.addScene("profileDelete", "ITalk-profileDelete.fxml");
        sceneManager.addScene("profileDeleteVerification", "ITalk-profileDeleteVerification.fxml");
        sceneManager.addScene("posts", "ITalk-posts.fxml");
        sceneManager.addScene("postEditor", "ITalk-postEditor.fxml");
        sceneManager.addScene("postComments", "ITalk-postComments.fxml");
        sceneManager.addScene("postDeleteVerification", "ITalk-postDeleteVerification.fxml");
        sceneManager.addScene("commentEditor", "ITalk-commentEditor.fxml");
        sceneManager.addScene("commentDeleteVerification", "ITalk-commentDeleteVerification.fxml");

        primaryStage.setTitle("ITalk");
        sceneManager.switchScene("login", appStatus, false);
    }
}
