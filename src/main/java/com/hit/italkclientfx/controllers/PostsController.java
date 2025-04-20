package com.hit.italkclientfx.controllers;

import com.hit.client.CommentClient;
import com.hit.client.PostClient;
import com.hit.dm.Comment;
import com.hit.dm.MatchResult;
import com.hit.dm.Post;
import com.hit.dm.SearchResult;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class PostsController extends ITalkController implements Initializable {

    @FXML
    public Button makePostButton;
    public Button clearButton;

    @FXML
    private Button profileButton;

    @FXML
    private Button refreshButton;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private VBox postsContainer;

    private CommentClient commentClient;
    private PostClient postClient;

    // This method is called automatically when the FXML file is loaded.
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        commentClient = (CommentClient) clients.get("commentClient");
        postClient = (PostClient) clients.get("postClient");

        if (appStatus.getCurrentUser() == null) {
            try {
                sceneManager.switchScene("login", appStatus, false);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (appStatus.getCurrentSearch() == null) {
            loadPosts();
        } else {
            searchField.setText(appStatus.getCurrentSearch());
            makeSearch();
        }
    }

    @FXML
    private void handleMakePostButtonClick(ActionEvent event) {
        appStatus.setCurrentPost(null);
        try {
            appStatus.setVerificationFlag(false);
            sceneManager.openNewStage("postEditor", appStatus, true);

            if (appStatus.isVerified()) {
                clearAll();
                loadPosts();
            } else {
                refreshPage();
            }
        } catch (IOException e) {
            e.printStackTrace();
            clearAll();
            addErrorLabel(e.getMessage());
        }
    }

    /**
     * Called when a post is clicked. Navigates to the post's comments view.
     *
     * @param post the post that was clicked
     */
    private void handlePostClick(Post post) {
        appStatus.setCurrentPost(post);
        try {
            sceneManager.switchScene("postComments", appStatus, true);
        } catch (IOException e) {
            e.printStackTrace();
            clearAll();
            addErrorLabel(e.getMessage());
        }
    }

    @FXML
    public void handleSearchButtonClick(ActionEvent event) {
        makeSearch();
    }

    @FXML
    private void handleProfileButtonClick(ActionEvent event) {
        try {
            sceneManager.openNewStage("profile", appStatus, false);
        } catch (IOException e) {
            e.printStackTrace();
            clearAll();
            addErrorLabel(e.getMessage());
        }
    }

    @FXML
    private void handleRefreshButtonClick(ActionEvent event) {
        refreshPage();
    }

    public void handleSearchFieldKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            makeSearch();
        }
    }

    public void handleClearButtonClick(ActionEvent actionEvent) {
        clearAll();
        loadPosts();
    }

    private void clearAll() {
        appStatus.setCurrentSearch(null);
        searchField.clear();
        postsContainer.getChildren().clear();
    }

    private void refreshPage() {
        if (searchField.getText() == null || searchField.getText().isEmpty()) {
            clearAll();
            loadPosts();
        } else {
            appStatus.setCurrentSearch(searchField.getText());
            makeSearch();
        }
    }

    /**
     * Loads posts from the server and populates the postsContainer VBox.
     */
    private void loadPosts() {
        try {
            List<Post> posts = postClient.getAllPosts();
            posts.sort(Comparator.comparing(Post::getTimestamp, Comparator.reverseOrder()));
            for (Post post : posts) {
                String editedRedLabel = null;
                if (post.getEdited()) {
                    editedRedLabel = "(Post Edited)";
                }

                addPostContainer(
                        post,
                        post.getTitle(),
                        "Posted By: " + post.getUserName(),
                        "On: " + post.getLocalDateTime(),
                        editedRedLabel,
                        post.getContent()
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
            addErrorLabel("Error during Load: " + e.getMessage());
        }
    }

    private VBox addPostContainer(
            Post post, String title, String smallBoldedLabelString,
            String smallLabelString, String smallRedLabelString, String snippet) {
        // Create a container for the post.
        VBox postBox = new VBox();
        postBox.setStyle("-fx-background-color: white; -fx-border-color: #BDBDBD; -fx-border-width: 2; -fx-background-radius: 5; -fx-border-radius: 5; -fx-padding: 10;");
        postBox.getStyleClass().add("post-container");

        // Title label.
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        // small labels.
        HBox smallLabelsContainer = new HBox(10);
        smallLabelsContainer.setAlignment(Pos.CENTER_LEFT);
        Label boldedLabel = new Label(smallBoldedLabelString);
        boldedLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #757575;");
        Label smallLabel = new Label(smallLabelString);
        smallLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #757575;");
        Label redLabel = new Label(smallRedLabelString);
        redLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #D32F2F;");

        smallLabelsContainer.getChildren().addAll(boldedLabel, smallLabel, redLabel);

        postBox.getChildren().addAll(titleLabel, smallLabelsContainer);

        if (snippet != null) {
            // Content snippet label.
            Label contentLabel = new Label(snippet);
            contentLabel.setWrapText(true);
            contentLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555555;");
            contentLabel.setPadding(new Insets(10, 0, 0, 0));
            contentLabel.setMaxHeight(50);

            // Add labels to the post container.
            postBox.getChildren().add(contentLabel);
        }

        // Set an event handler for clicking on this post.
        postBox.setOnMouseClicked((MouseEvent event) -> {
            handlePostClick(post);
        });

        // Add the post container to the postsContainer.
        postsContainer.getChildren().add(postBox);

        return postBox;
    }

    private void addHeaderLabel(String header) {
        Label titleHeader = new Label(header);
        titleHeader.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        postsContainer.getChildren().add(titleHeader);
    }

    private void addErrorLabel(String errorString) {
        Label errorLabel = new Label(errorString);
        errorLabel.setStyle("-fx-text-fill: #D32F2F; -fx-font-size: 12px;");
        errorLabel.setTextAlignment(TextAlignment.CENTER);
        errorLabel.setWrapText(true);
        postsContainer.getChildren().add(errorLabel);
    }

    private void makeSearch() {
        String query = searchField.getText().trim();
        postsContainer.getChildren().clear();

        if (query.isEmpty()) {
            clearAll();
            loadPosts();
            return;
        }

        appStatus.setCurrentSearch(query);

        try {
            // Search in post titles
            SearchResult<Post> titleResults = postClient.searchPostTitles(query);
            // Search in post contents
            SearchResult<Post> contentResults = postClient.searchPostContents(query);
            // Search in comment contents
            SearchResult<Comment> commentResults = commentClient.searchCommentContents(query);

            // Section header for title results
            addHeaderLabel("All Matching Titles:");
            // Display posts that matched in their titles
            for (MatchResult<Post> matchResult : titleResults.getMatches()) {
                Post post = matchResult.getItem();

                String editedRedLabel = null;
                if (post.getEdited()) {
                    editedRedLabel = "(Post Edited)";
                }

                addPostContainer(
                        post,
                        "In Post: '" + post.getTitle() + "'",
                        "Posted By: " + post.getUserName(),
                        "On: " + post.getLocalDateTime(),
                        editedRedLabel,
                        null
                );
            }

            // Section header for content results
            addHeaderLabel("All Matching Posts Content:");
            // Display posts that matched in their content
            for (MatchResult<Post> matchResult : contentResults.getMatches()) {
                Post post = matchResult.getItem();
                int[] indexes = matchResult.getIndexes();
                for (int i : indexes) {
                    HBox snippet = getSnippet(
                            post.getContent(), query, i, 30, "Snippet (Content):");

                    String editedRedLabel = null;
                    if (post.getEdited()) {
                        editedRedLabel = "(Post Edited)";
                    }

                    VBox postContainer = addPostContainer(
                            post,
                            "In Post: '" + post.getTitle() + "'",
                            "Posted By: " + post.getUserName(),
                            "On: " + post.getLocalDateTime(),
                            editedRedLabel,
                            null
                    );
                    postContainer.getChildren().add(snippet);
                }
            }

            // Section header for comment results
            addHeaderLabel("All Matching Comments:");
            // Display comments that matched (grouped by post if desired)
            for (MatchResult<Comment> matchResult : commentResults.getMatches()) {
                int[] indexes = matchResult.getIndexes();
                Comment comment = matchResult.getItem();
                String editedRedLabel = null;
                if (comment.getEdited()) {
                    editedRedLabel = "(Comment Edited)";
                }

                for (int i : indexes) {
                    HBox snippet = getSnippet(
                            comment.getContent(), query, i, 30, "Snippet (comment):");

                    Post post = postClient.getPostById(comment.getPostId());

                    if (post != null) {
                        VBox postContainer = addPostContainer(
                                post,
                                "In Post: '" + post.getTitle() + "'",
                                "Commented By: " + comment.getUserName(),
                                "On: " + comment.getLocalDateTime(),
                                editedRedLabel,
                                null
                        );
                        postContainer.getChildren().add(snippet);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            addErrorLabel("Error during search: " + e.getMessage());
        }
    }

    /**
     * Helper method to extract a snippet of text from a string around a given index.
     */
    private HBox getSnippet(String text, String searchedText, int index, int snippetSize, String remark) {
        if (snippetSize <= 0) {
            return null;
        }
        int endOfSearchedText = index + searchedText.length() - 1;

        String addBefore = (index - snippetSize > 0) ? "..." : "";
        String addAfter = (endOfSearchedText + snippetSize + 1 <= text.length() - 1) ? "..." : "";
        String beforeText = text.substring(Math.max(0, index - snippetSize), index);
        String middleText = text.substring(index, searchedText.length() + index);
        String afterText = text.substring(
                Math.min(endOfSearchedText + 1, text.length()),
                Math.min(endOfSearchedText + snippetSize + 1, text.length())
        );

        HBox snippetHBox = new HBox();
//        snippetHBox.setStyle("-fx-font-size: 14px; -fx-text-fill: #555555;");
        snippetHBox.setPadding(new Insets(10, 0, 0, 0));
        snippetHBox.setMaxHeight(50);
        snippetHBox.setAlignment(Pos.TOP_CENTER);

        Label searchedTextLabel = new Label(middleText.replaceAll("\\s+", " "));
        searchedTextLabel.setStyle("-fx-font-weight: bold; -fx-background-color: #e69f43; -fx-font-size: 14px; -fx-text-fill: #555555;");
        Label beforeTextLabel = new Label(remark + addBefore + beforeText.replaceAll("\\s+", " "));
        beforeTextLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555555;");
        Label afterTextLabel = new Label(afterText.replaceAll("\\s+", " ") + addAfter);
        afterTextLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555555;");

        snippetHBox.getChildren().addAll(beforeTextLabel, searchedTextLabel, afterTextLabel);
        return snippetHBox;
    }
}
