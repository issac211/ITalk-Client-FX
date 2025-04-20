package com.hit.italkclientfx.controllers;

import com.hit.client.UserClient;
import com.hit.dm.User;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController extends ITalkController implements Initializable {
    public Button loginButton;
    public Button signUpButton;
    public PasswordField passwordField;
    public TextField usernameField;
    public Label messageLabel;
    private UserClient userClient;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userClient = (UserClient) clients.get("userClient");
    }

    public void handleLoginClick(ActionEvent actionEvent) {
        login();
    }

    public void handleSignUpClick(ActionEvent actionEvent) {
        signUp();
    }

    public void handleLoginKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            login();
        }
    }

    private void login() {
        try {
            User user = userClient.getUser(usernameField.getText(), passwordField.getText());

            if (user != null) {
                appStatus.setCurrentUser(user);
                sceneManager.switchScene("posts", appStatus, false);
            } else {
                messageLabel.setText("User Not Found, You may have entered the wrong username or password.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText(e.getMessage());
        }
    }

    private void signUp() {
        try {
            boolean created = userClient.createUser(usernameField.getText(), passwordField.getText(), User.Role.USER);

            if (created) {
                User user = new User(usernameField.getText(), passwordField.getText());
                appStatus.setCurrentUser(user);
                sceneManager.switchScene("posts", appStatus, false);
            } else {
                messageLabel.setText("Cannot create, username appears to already exist in the system.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText(e.getMessage());
        }
    }
}
