package com.hit.italkclientfx.controllers;

import com.hit.dm.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * ProfileController is responsible for displaying the user's profile details.
 * It allows the user to edit their profile, log out, delete their account, or return to the main posts page.
 */
public class ProfileController extends ITalkController implements Initializable {

    public Label messageLabel;
    @FXML
    private Button returnButton;

    @FXML
    private Button editProfileButton;

    @FXML
    private Button logoutButton;

    @FXML
    private Button deleteAccountButton;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label roleLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load current user details
        User currentUser = appStatus.getCurrentUser();
        if (currentUser != null) {
            usernameLabel.setText(currentUser.getUsername());
            roleLabel.setText(currentUser.getRole().name());
        } else {
            usernameLabel.setText("Unknown");
            roleLabel.setText("Unknown");
        }
    }

    @FXML
    private void handleReturn(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleEditProfile(ActionEvent event) {
        try {
            sceneManager.switchScene("profileEdit", appStatus,true);
        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        appStatus.setCurrentUser(null);
        Stage profileStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        profileStage.close();
        try {
            sceneManager.switchScene("login", appStatus, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteAccount(ActionEvent event) {
        try {
            sceneManager.switchScene("profileDelete", appStatus, true);
        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText(e.getMessage());
        }
    }
}
