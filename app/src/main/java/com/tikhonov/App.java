package com.tikhonov;


import com.tikhonov.appcontainer.AppComponentsContainerImpl;
import com.tikhonov.appcontainer.api.AppComponentsContainer;
import com.tikhonov.config.AppConfig;
import com.tikhonov.services.GameProcessor;


public class App {

    public static void main(String[] args) {
        AppComponentsContainer container = new AppComponentsContainerImpl(AppConfig.class);
        GameProcessor gameProcessor = container.getAppComponent(GameProcessor.class);
//        GameProcessor gameProcessor = container.getAppComponent("gameProcessor");
        gameProcessor.startGame();
    }
}
