package com.hit.italkclientfx;

import com.hit.client.BaseClient;
import com.hit.italkclientfx.controllers.ITalkController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.stage.Window;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SceneManager {
    // A map to store scene keys and the corresponding FXML file paths.
    private final Map<String, String> sceneMap;
    private Stage primaryStage;
    private Stage currentStage;
    private final Map<String, BaseClient> clients;

    public SceneManager(Stage primaryStage, Map<String, BaseClient> clients) {
        sceneMap = new HashMap<>();
        this.primaryStage = primaryStage;
        this.currentStage = primaryStage;
        this.clients = clients;
    }

    /**
     * Registers a new scene with a key and its corresponding FXML file path.
     *
     * @param key      the unique key for the scene
     * @param fxmlPath the FXML file path for the scene
     */
    public void addScene(String key, String fxmlPath) {
        sceneMap.put(key, fxmlPath);
    }

    /**
     * Changes the scene on the given stage to the scene associated with the provided key.
     *
     * @param sceneKey     the key of the scene to display
     * @param appStatus    the current app status of the application
     * @param preserveSize if true, the current stage's width and height are preserved for the new scene.
     * @throws IOException              if the FXML file cannot be loaded
     * @throws IllegalArgumentException if the key is not found in the scene map
     */
    public void switchScene(String sceneKey, AppStatus appStatus, boolean preserveSize) throws IOException {
        String fxmlPath = sceneMap.get(sceneKey);

        if (fxmlPath == null) {
            throw new IllegalArgumentException("Scene with key '" + sceneKey + "' not found.");
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        loader.setControllerFactory(param -> {
            Object controller = null;
            try {
                controller = param.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            if (controller instanceof ITalkController) {
                ((ITalkController) controller).setSceneManager(this);
                ((ITalkController) controller).setAppStatus(appStatus);
                ((ITalkController) controller).setClients(clients);
            }
            return controller;
        });

        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass()
                .getResource("/com/hit/italkclientfx/css/style.css")).toExternalForm());

        currentStage.setScene(scene);

        if (preserveSize) {
            double width = currentStage.getWidth();
            double height = currentStage.getHeight();
            currentStage.setScene(scene);
            currentStage.setWidth(width);
            currentStage.setHeight(height);
        } else {
            currentStage.setScene(scene);
        }

        currentStage.show();
    }

    /**
     * Opens a new stage and sets its scene to the one associated with the provided key.
     *
     * @param sceneKey    the key of the scene to display
     * @param appStatus   the current app status of the application
     * @param showAndWait determines whether to show the stage and wait
     * @return the newly created Stage
     * @throws IOException              if the FXML file cannot be loaded
     * @throws IllegalArgumentException if the key is not found in the scene map
     */
    public Stage openNewStage(String sceneKey, AppStatus appStatus, boolean showAndWait) throws IOException {
        String fxmlPath = sceneMap.get(sceneKey);

        if (fxmlPath == null) {
            throw new IllegalArgumentException("Scene with key '" + sceneKey + "' not found.");
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        loader.setControllerFactory(param -> {
            Object controller = null;
            try {
                controller = param.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            if (controller instanceof ITalkController) {
                ((ITalkController) controller).setSceneManager(this);
                ((ITalkController) controller).setAppStatus(appStatus);
                ((ITalkController) controller).setClients(clients);
            }
            return controller;
        });
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass()
                .getResource("/com/hit/italkclientfx/css/style.css")).toExternalForm());

        Stage newStage = new Stage();
        newStage.initOwner(currentStage);
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.setScene(scene);
        newStage.setTitle(currentStage.getTitle() + " - " + sceneKey);

        // When the new stage is closing, reset currentStage to its owner (or primaryStage if no owner)
        newStage.setOnHiding(e -> {
            Window owner = newStage.getOwner();
            if (owner instanceof Stage) {
                setCurrentStage((Stage) owner);
            } else {
                setCurrentStage(primaryStage);
            }
        });

        // Update the currentStage, then show the new stage.
        currentStage = newStage;
        if (showAndWait) {
            newStage.showAndWait();
        } else {
            newStage.show();
        }

        return newStage;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Stage getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
    }
}
