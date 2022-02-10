package com.tikhonov.config;

import com.tikhonov.appcontainer.api.AppComponent;
import com.tikhonov.appcontainer.api.AppComponentsContainerConfig;
import com.tikhonov.services.*;

@AppComponentsContainerConfig(order = 1)
public class AppConfig {

    @AppComponent(order = 0, name = "equationPreparer")
    public EquationPreparer equationPreparer() {
        return new EquationPreparerImpl();
    }

    @AppComponent(order = 1, name = "playerService")
    public PlayerService playerService(IOService ioService) {
        return new PlayerServiceImpl(ioService);
    }

    @AppComponent(order = 2, name = "gameProcessor")
    public GameProcessor gameProcessor(IOService ioService,
                                       PlayerService playerService,
                                       EquationPreparer equationPreparer) {
        return new GameProcessorImpl(ioService, playerService, equationPreparer);
    }

    @AppComponent(order = 0, name = "ioService")
    public IOService ioService() {
        return new IOServiceImpl(System.out, System.in);
    }
}
