package com.hit.italkclientfx.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ProfileDeleteVerificationController extends ITalkController {

    @FXML
    private Button confirmButton;

    @FXML
    private Button cancelButton;

    /**
     * Called when the user confirms deletion of the profile.
     */
    @FXML
    private void handleConfirm(ActionEvent event) {
        // Setting Verification flag to true for profile deletion.
        appStatus.setVerificationFlag(true);
        // Close the current stage.
        Stage currentStage = (Stage) cancelButton.getScene().getWindow();
        currentStage.close();
    }

    /**
     * Called when the user cancels deletion of the profile.
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
