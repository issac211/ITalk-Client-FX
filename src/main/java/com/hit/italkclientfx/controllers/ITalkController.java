package com.hit.italkclientfx.controllers;

import com.hit.italkclientfx.AppStatus;
import com.hit.italkclientfx.SceneManager;

public abstract class ITalkController {
    protected SceneManager sceneManager;
    protected AppStatus appStatus;

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    public void setAppStatus(AppStatus appStatus) {
        this.appStatus = appStatus;
    }
}
