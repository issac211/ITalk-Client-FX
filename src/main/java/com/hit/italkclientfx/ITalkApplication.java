package com.hit.italkclientfx;

import com.hit.client.Client;
import com.hit.italkclientfx.controllers.ITalkController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public class ITalkApplication extends Application {

    private SceneManager sceneManager;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Initialize global resources.
        AppStatus appStatus = new AppStatus();
        sceneManager = new SceneManager(primaryStage);
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
        Client.initializeInstance("localhost", 34567);

        // Sets a custom controller factory to inject SceneManager and AppStatus
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ITalk-login.fxml"));
        fxmlLoader.setControllerFactory(param -> {
            Object controller = null;
            try {
                controller = param.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            if (controller instanceof ITalkController) {
                ((ITalkController) controller).setSceneManager(sceneManager);
                ((ITalkController) controller).setAppStatus(appStatus);
            }
            return controller;
        });
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(Objects.requireNonNull(getClass()
                .getResource("/com/hit/italkclientfx/css/style.css")).toExternalForm());
        primaryStage.setTitle("ITalk");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
