package com.hit.italkclientfx.controllers;

import com.hit.client.CommentClient;
import com.hit.client.PostClient;
import com.hit.dm.Comment;
import com.hit.dm.Post;
import com.hit.dm.User;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

public class PostCommentsController extends ITalkController implements Initializable {

    public Button refreshButton;
    @FXML
    private Button returnButton;          // "Return to Main" button in the top nav
    @FXML
    private VBox postDetailsContainer;    // Container for post details
    @FXML
    private Label postTitleLabel;         // Displays post title
    @FXML
    private Label postTimeLabel;          // Displays post creation time
    @FXML
    private Label postUserLabel;          // Displays post creator's username
    @FXML
    private Label postEditedLabel;        // Shows "(Edited)" if the post was modified
    @FXML
    private Label postContentLabel;       // Displays post content
    @FXML
    private Button editPostButton;        // Visible only if current user is post owner
    @FXML
    private Button deletePostButton;      // Visible only if current user is post owner
    @FXML
    private VBox commentsContainer;       // Container where comments are dynamically added
    @FXML
    private Button newCommentButton;      // Button to create a new comment

    private CommentClient commentClient;
    private PostClient postClient;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        commentClient = (CommentClient) clients.get("commentClient");
        postClient = (PostClient) clients.get("postClient");
        loadAll();
    }

    /**
     * Handles the action for returning to the main posts scene.
     */
    @FXML
    private void handleReturn(ActionEvent event) {
        clearAllComments();

        try {
            sceneManager.switchScene("posts", appStatus, true);
            appStatus.setCurrentPost(null);
        } catch (IOException e) {
            showAlert(AlertType.ERROR, "Error", "Error returning to main: " + e.getMessage());
        }
    }

    /**
     * Handles the action for creating a new comment.
     */
    @FXML
    private void handleNewComment(ActionEvent event) {
        appStatus.setCurrentComment(null);

        try {
            appStatus.setVerificationFlag(false);
            sceneManager.openNewStage("commentEditor", appStatus, true);
        } catch (IOException e) {
            showAlert(AlertType.ERROR, "Error", "Error opening comment editor: " + e.getMessage());
        }

        if (appStatus.isVerified()) {
            refreshPage();
        }
    }

    /**
     * Handles editing the current post.
     */
    @FXML
    private void handleEditPost(ActionEvent event) {
        try {
            appStatus.setVerificationFlag(false);
            sceneManager.openNewStage("postEditor", appStatus, true);
        } catch (IOException e) {
            showAlert(AlertType.ERROR, "Error", "Error editing post: " + e.getMessage());
        }

        if (appStatus.isVerified()) {
            refreshPage();
        }
    }

    /**
     * Handles deleting the current post.
     */
    @FXML
    private void handleDeletePost(ActionEvent event) {
        Post currentPost = appStatus.getCurrentPost();
        if (currentPost == null) return;
        try {
            appStatus.setVerificationFlag(false);
            sceneManager.openNewStage("postDeleteVerification", appStatus, true);

            if (appStatus.isVerified()) {
                boolean deleted = postClient.removePost(currentPost.getId(), currentPost.getUserName());

                if (deleted) {
                    clearAllComments();
                    sceneManager.switchScene("posts", appStatus, true);
                    appStatus.setCurrentPost(null);
                } else {
                    showAlert(AlertType.ERROR, "Error", "Error deleting post.");
                }
            }
        } catch (IOException e) {
            showAlert(AlertType.ERROR, "Error", "Error deleting post: " + e.getMessage());
        }
    }

    /**
     * Handles editing a comment.
     *
     * @param comment The comment to edit.
     */
    private void handleEditComment(Comment comment) {
        appStatus.setCurrentComment(comment);
        try {
            appStatus.setVerificationFlag(false);
            sceneManager.openNewStage("commentEditor", appStatus, true);
        } catch (IOException e) {
            showAlert(AlertType.ERROR, "Error", "Error editing comment: " + e.getMessage());
        }

        if (appStatus.isVerified()) {
            refreshPage();
        }
    }

    /**
     * Handles deleting a comment.
     *
     * @param comment The comment to delete.
     */
    private void handleDeleteComment(Comment comment) {
        try {
            appStatus.setVerificationFlag(false);
            sceneManager.openNewStage("commentDeleteVerification", appStatus, true);

            if (appStatus.isVerified()) {
                boolean deleted = commentClient.removeComment(comment.getId(), comment.getUserName());

                if (deleted) {
                    refreshPage();
                } else {
                    showAlert(AlertType.ERROR, "Error", "Error deleting comment.");
                }
            }
        } catch (IOException e) {
            showAlert(AlertType.ERROR, "Error", "Error deleting comment: " + e.getMessage());
        }
    }

    public void handleRefreshButtonClick(ActionEvent actionEvent) {
        refreshPage();
    }

    /**
     * Utility method to display an alert dialog.
     *
     * @param type    the type of alert (e.g., ERROR, INFORMATION)
     * @param title   the title of the alert dialog
     * @param message the message to display in the alert
     */
    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    private void refreshPage() {
        clearAllComments();
        loadAll();
    }

    private void clearAllComments() {
        commentsContainer.getChildren().clear();
    }

    /**
     * Load current post and the comments for the current post.
     */
    private void loadAll() {
        // Load current post details from appStatus.
        loadPost();
        // Load the comments for the current post.
        loadComments();
    }

    /**
     * Load current post details.
     */
    private void loadPost() {
        Post currentPost = appStatus.getCurrentPost();
        if (currentPost != null) {
            postTitleLabel.setText(currentPost.getTitle());
            postContentLabel.setText(currentPost.getContent());
            postTimeLabel.setText("Posted on: " + currentPost.getLocalDateTime());
            postUserLabel.setText("By: " + currentPost.getUserName());
            postEditedLabel.setVisible(currentPost.getEdited());
        } else {
            showAlert(AlertType.ERROR, "Error", "No post selected.");
            try {
                sceneManager.switchScene("posts", appStatus, true);
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(AlertType.ERROR, "Error", e.getMessage());
                sceneManager.getCurrentStage().close();
            }
        }

        // Show post action buttons only if the current user is the creator of the post.
        User currentUser = appStatus.getCurrentUser();
        if (currentPost != null && currentUser != null &&
                (currentPost.getUserName().equals(currentUser.getUsername())
                        || currentUser.getRole() != User.Role.USER)) {
            editPostButton.setVisible(true);
            deletePostButton.setVisible(true);

        } else {
            editPostButton.setVisible(false);
            deletePostButton.setVisible(false);
        }
    }

    /**
     * Loads the comments for the current post and dynamically populates the commentsContainer.
     */
    private void loadComments() {
        try {
            Post currentPost = appStatus.getCurrentPost();
            User currentUser = appStatus.getCurrentUser();
            if (currentPost == null) return;
            List<Comment> comments = postClient.getPostComments(currentPost.getId());
            comments.sort(Comparator.comparing(Comment::getTimestamp));

            for (Comment comment : comments) {
                VBox commentBox = new VBox(5);
                commentBox.setPadding(new Insets(10));
                commentBox.setStyle("-fx-background-color: white; -fx-border-color: #BDBDBD; "
                        + "-fx-border-width: 1; -fx-background-radius: 5; -fx-border-radius: 5;");

                // Create header for the comment (user, time, edited flag).
                HBox header = new HBox(10);
                header.setAlignment(Pos.CENTER_LEFT);

                Label commentUserLabel = new Label("Comment by " + comment.getUserName());
                commentUserLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #757575;");

                Label commentTimeLabel = new Label("Posted on: " + comment.getLocalDateTime());
                commentTimeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #757575;");

                Label commentEditedLabel = new Label("(Edited)");
                commentEditedLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #D32F2F;");
                commentEditedLabel.setVisible(comment.getEdited());

                header.getChildren().addAll(commentUserLabel, commentTimeLabel, commentEditedLabel);

                // Create label for comment content.
                Label commentContentLabel = new Label(comment.getContent());
                commentContentLabel.setWrapText(true);
                commentContentLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #424242;");

                // Create action buttons for comment: visible only if current user is the comment's creator.
                HBox actions = new HBox(10);
                actions.setAlignment(Pos.CENTER_RIGHT);
                Button editCommentButton = new Button("Edit");
                editCommentButton.setPrefSize(70, 30);
                editCommentButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 12px;");
                Button deleteCommentButton = new Button("Delete");
                deleteCommentButton.setPrefSize(70, 30);
                deleteCommentButton.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: white; -fx-font-size: 12px;");

                if (currentUser != null &&
                        (currentUser.getUsername().equals(comment.getUserName())
                                || currentUser.getRole() != User.Role.USER)) {
                    editCommentButton.setVisible(true);
                    editCommentButton.getStyleClass().add("interactive-button");
                    editCommentButton.setOnAction(e -> handleEditComment(comment));
                    deleteCommentButton.setVisible(true);
                    deleteCommentButton.getStyleClass().add("interactive-button");
                    deleteCommentButton.setOnAction(e -> handleDeleteComment(comment));
                } else {
                    editCommentButton.setVisible(false);
                    deleteCommentButton.setVisible(false);
                }
                actions.getChildren().addAll(editCommentButton, deleteCommentButton);

                commentBox.getChildren().addAll(header, commentContentLabel, actions);
                commentsContainer.getChildren().add(commentBox);
            }
        } catch (IOException e) {
            showAlert(AlertType.ERROR, "Error", "Error loading comments: " + e.getMessage());
        }
    }
}
