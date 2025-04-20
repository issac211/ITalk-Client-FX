package com.hit.italkclientfx.controllers;

import com.hit.client.UserClient;
import com.hit.dm.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ProfileDeleteController extends ITalkController implements Initializable {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    @FXML
    private Button deleteButton;

    @FXML
    private Button cancelButton;

    private UserClient userClient;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userClient = (UserClient) clients.get("userClient");

        // Pre-fill the username field if the current user is available.
        if (appStatus.getCurrentUser() != null) {
            usernameField.setText(appStatus.getCurrentUser().getUsername());
        }
    }

    /**
     * Handles the deletion of the account.
     * Verifies the entered username and password, calls the UserClient's removeUser method,
     * and if successful, clears the current user, closes the current stage,
     * and navigates to the login scene.
     */
    @FXML
    private void handleDelete(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        User currentUser = appStatus.getCurrentUser();

        if (currentUser.getRole() != User.Role.ADMIN) {
            if (username.isEmpty() || password.isEmpty()) {
                messageLabel.setText("Please enter both username and password.");
                return;
            }

            try {
                boolean authenticated = userClient.authenticateUser(username, password);
                if (!authenticated || !username.equals(currentUser.getUsername())) {
                    messageLabel.setText("Account deletion failed. Please check your credentials.");
                    return;
                }
            } catch (IOException e) {
                messageLabel.setText("Error: " + e.getMessage());
            }
        }

        try {
            appStatus.setVerificationFlag(false);
            sceneManager.openNewStage("profileDeleteVerification", appStatus, true);

            if (appStatus.isVerified()) {
                boolean deleted = userClient.removeUser(currentUser.getUsername(), username, password);

                if (deleted) {
                    if (currentUser.getUsername().equals(username)) {
                        // Clear the current user and navigate to the login scene.
                        appStatus.setCurrentUser(null);
                        // Close the current stage.
                        Stage currentStage = (Stage) deleteButton.getScene().getWindow();
                        currentStage.close();
                        // Switch to the login scene.
                        sceneManager.switchScene("login", appStatus, false);
                    } else {
                        messageLabel.setText("Deleted successfully.");
                    }
                } else {
                    messageLabel.setText("Account deletion failed. Please check your credentials.");
                }
            }
        } catch (IOException e) {
            messageLabel.setText("Error: " + e.getMessage());
        }
    }

    /**
     * Handles the cancel action. Navigates back to the profile scene without deleting the account.
     */
    @FXML
    private void handleCancel(ActionEvent event) {
        try {
            sceneManager.switchScene("profile", appStatus, true);
        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText(e.getMessage());
        }
    }
}
