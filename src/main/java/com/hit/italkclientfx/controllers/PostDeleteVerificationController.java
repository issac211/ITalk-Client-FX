package com.hit.italkclientfx.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class PostDeleteVerificationController extends ITalkController {

    @FXML
    private Button confirmButton;

    @FXML
    private Button cancelButton;

    /**
     * Called when the user confirms deletion of the post.
     */
    @FXML
    private void handleConfirm(ActionEvent event) {
        // Set the verification flag to true for post deletion.
        appStatus.setVerificationFlag(true);
        // Close the current stage.
        Stage currentStage = (Stage) confirmButton.getScene().getWindow();
        currentStage.close();
    }

    /**
     * Called when the user cancels deletion of the post.
     */
    @FXML
    private void handleCancel(ActionEvent event) {
        // Set the verification flag to false.
        appStatus.setVerificationFlag(false);
        // Close the current stage.
        Stage currentStage = (Stage) cancelButton.getScene().getWindow();
        currentStage.close();
    }
}
