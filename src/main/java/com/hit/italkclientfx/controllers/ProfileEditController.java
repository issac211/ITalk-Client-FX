package com.hit.italkclientfx.controllers;

import com.hit.client.Client;
import com.hit.dm.User.Role;
import com.hit.dm.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ProfileEditController extends ITalkController implements Initializable {

    public Label messageLabel;
    public Label roleLabel;
    @FXML
    private PasswordField oldPasswordField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Populate the roleComboBox with role options
        for (Role role : Role.values()) {
            roleComboBox.getItems().add(role.name());
        }
        // Preselect the current user's role, if available
        if (appStatus.getCurrentUser() != null) {
            roleComboBox.setValue(appStatus.getCurrentUser().getRole().name());
        }
    }

    /**
     * Handles the save button click. Verifies the old password, checks that the new passwords match,
     * then sends a request to update the user's password and role. On success, switches back to the profile scene.
     */
    @FXML
    private void handleSave(ActionEvent event) {
        String oldPass = oldPasswordField.getText();
        String newPass = newPasswordField.getText();
        String confirmPass = confirmPasswordField.getText();
        String newRoleStr = roleComboBox.getValue();

        // Basic validations
        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            messageLabel.setText("Please fill in all fields.");
            return;
        }
        if (!newPass.equals(confirmPass)) {
            messageLabel.setText("New password and confirmation do not match.");
            return;
        }

        // Get the current user from the application status
        User currentUser = appStatus.getCurrentUser();
        if (currentUser == null) {
            messageLabel.setText("No user is logged in.");
            return;
        }

        if (newRoleStr == null) {
            newRoleStr = currentUser.getRole().name();
        }

        try {
            // Optionally verify the old password with the server.
            boolean auth = Client.getInstance().authenticateUser(currentUser.getUsername(), oldPass);
            if (!auth) {
                messageLabel.setText("Old password is incorrect.");
                return;
            }

            // Attempt to update the user's details.
            boolean edited = Client.getInstance().editUser(
                    currentUser.getUsername(), // editorName
                    currentUser.getUsername(), // userName to edit
                    oldPass,
                    newPass,
                    Role.valueOf(newRoleStr)
            );

            if (edited) {
                // Update the local representation of the user.
                currentUser.setPassword(newPass);
                currentUser.setRole(Role.valueOf(newRoleStr));
                appStatus.setCurrentUser(currentUser);
                // Navigate back to the profile scene.
                sceneManager.switchScene("profile", appStatus, true);
            } else {
                messageLabel.setText("Failed to update profile. Please try again.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Error: " + e.getMessage());
        }
    }

    /**
     * Handles the cancel button click. Returns to the profile scene without saving changes.
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
