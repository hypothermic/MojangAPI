package me.kbrewster.mojangapi.profile;

public enum Model {

    STEVE(""),
    //ALEX("slim");

    ;

    private String model;

    Model(String model) {
        this.model = model;
    }

    public String getModel() {
        return model;
    }
}
