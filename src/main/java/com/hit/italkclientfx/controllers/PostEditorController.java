package com.hit.italkclientfx.controllers;

import com.hit.client.Client;
import com.hit.dm.Post;
import com.hit.dm.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PostEditorController extends ITalkController implements Initializable {

    @FXML
    private Label editorStatusLabel; // Displays "Create Post" or "Edit Post"

    @FXML
    private TextField titleField;

    @FXML
    private TextArea contentArea;

    @FXML
    private Button createEditButton;

    @FXML
    private Button cancelButton;

    @FXML
    public Label messageLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Determine if we are in create mode or edit mode.
        Post currentPost = appStatus.getCurrentPost();
        if (currentPost == null) {
            editorStatusLabel.setText("Create Post");
            createEditButton.setText("Create");
        } else {
            editorStatusLabel.setText("Edit Post");
            createEditButton.setText("Edit");
            // Populate fields with existing post data.
            titleField.setText(currentPost.getTitle());
            contentArea.setText(currentPost.getContent());
        }
    }

    /**
     * Handles the action for creating or editing a post.
     */
    @FXML
    private void handleCreateEdit(ActionEvent event) {
        appStatus.setVerificationFlag(false);
        String title = titleField.getText().trim();
        String content = contentArea.getText().trim();

        User currentUser = appStatus.getCurrentUser();
        if (currentUser == null) {
            messageLabel.setText("No user is logged in.");
            return;
        }

        if (title.isEmpty() || content.isEmpty()) {
            messageLabel.setText("Please fill in both title and content.");
            return;
        }

        try {
            if (appStatus.getCurrentPost() == null) {
                // Create mode
                boolean created = Client.getInstance().createPost(title, currentUser.getUsername(), content);

                if (created) {
                    // Setting Verification flag to true.
                    appStatus.setVerificationFlag(true);
                    // Close the current stage.
                    Stage currentStage = (Stage) createEditButton.getScene().getWindow();
                    currentStage.close();
                } else {
                    messageLabel.setText("Failed to create. Please try again.");
                }
            } else {
                // Edit mode
                Post currentPost = appStatus.getCurrentPost();
                boolean Edited = Client.getInstance().editPost(currentPost.getId(), title, currentUser.getUsername(), content);

                if (Edited) {
                    Post editedPost = Client.getInstance().getPostById(currentPost.getId());
                    appStatus.setCurrentPost(editedPost);
                    // Setting Verification flag to true.
                    appStatus.setVerificationFlag(true);
                    // Close the current stage.
                    Stage currentStage = (Stage) createEditButton.getScene().getWindow();
                    currentStage.close();
                } else {
                    if (!currentUser.getUsername().equals(currentPost.getUserName())) {
                        messageLabel.setText("Failed to edit. Only your own posts can be edited.");
                    } else {
                        messageLabel.setText("Failed to edit. Please try again.");
                    }
                }
            }
        } catch (IOException e) {
            messageLabel.setText("Error: " + e.getMessage());
        }
    }

    /**
     * Cancels the editing/creation and returns to the posts scene.
     */
    @FXML
    private void handleCancel(ActionEvent event) {
        // Setting Verification flag to false.
        appStatus.setVerificationFlag(false);
        // Close the current stage.
        Stage currentStage = (Stage) cancelButton.getScene().getWindow();
        currentStage.close();
    }
}
