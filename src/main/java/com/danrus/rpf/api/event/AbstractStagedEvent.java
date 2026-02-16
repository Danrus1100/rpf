package com.danrus.rpf.api.event;

public class AbstractStagedEvent extends RpfEvent{

    private final Stage stage;

    public AbstractStagedEvent(Stage stage) {
        this.stage = stage;
    }

    public Stage getStage() {
        return stage;
    }

    public enum Stage {
        PRE,
        POST
    }
}
