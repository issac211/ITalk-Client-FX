package com.hit.italkclientfx.controllers;

import com.hit.client.Client;
import com.hit.dm.Comment;
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

public class CommentEditorController extends ITalkController implements Initializable {

    @FXML
    private Label editorStatusLabel; // Will display "Create Comment" or "Edit Comment"

    @FXML
    private TextArea commentContentArea; // For editing the comment content

    @FXML
    private Button createEditButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Label messageLabel; // For displaying error messages

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Determine mode based on whether a current comment exists
        if (appStatus.getCurrentComment() == null) {
            editorStatusLabel.setText("Create Comment");
            createEditButton.setText("Create");
        } else {
            editorStatusLabel.setText("Edit Comment");
            createEditButton.setText("Edit");
            // Populate with existing comment content
            commentContentArea.setText(appStatus.getCurrentComment().getContent());
        }
    }

    /**
     * Handles the Create/Edit button click.
     * In create mode, it creates a new comment under the current post.
     * In edit mode, it updates the existing comment.
     */
    @FXML
    private void handleCreateEdit(ActionEvent event) {
        appStatus.setVerificationFlag(false);
        String content = commentContentArea.getText().trim();

        if (content.isEmpty()) {
            messageLabel.setText("Please enter comment content.");
            return;
        }

        User currentUser = appStatus.getCurrentUser();
        if (currentUser == null) {
            messageLabel.setText("No user is logged in.");
            return;
        }

        try {
            boolean success;
            if (appStatus.getCurrentComment() == null) {
                // Create mode: get current post from appStatus
                Post currentPost = appStatus.getCurrentPost();
                if (currentPost == null) {
                    messageLabel.setText("No post selected.");
                    return;
                }
                success = Client.getInstance().createComment(currentPost.getId(), currentUser.getUsername(), content);
            } else {
                // Edit mode
                Comment currentComment = appStatus.getCurrentComment();
                success = Client.getInstance().editComment(currentComment.getId(), currentUser.getUsername(), content);
            }

            if (success) {
                // Setting Verification flag to true.
                appStatus.setVerificationFlag(true);
                // Close the comment editor stage.
                Stage stage = (Stage) createEditButton.getScene().getWindow();
                stage.close();
            } else {
                messageLabel.setText("Operation failed. Please try again.");
            }
        } catch (IOException e) {
            messageLabel.setText("Error: " + e.getMessage());
        }
    }

    /**
     * Handles the Cancel button click.
     * Closes the editor and returns to the post comments scene without saving changes.
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
