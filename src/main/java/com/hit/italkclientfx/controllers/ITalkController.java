package com.hit.italkclientfx.controllers;

import com.hit.client.BaseClient;
import com.hit.italkclientfx.AppStatus;
import com.hit.italkclientfx.SceneManager;

import java.util.List;
import java.util.Map;

public abstract class ITalkController {
    protected SceneManager sceneManager;
    protected AppStatus appStatus;
    protected Map<String, BaseClient> clients;

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    public void setAppStatus(AppStatus appStatus) {
        this.appStatus = appStatus;
    }

    public void setClients(Map<String, BaseClient> clients) {
        this.clients = clients;
    }
}
