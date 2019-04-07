package me.kbrewster.mojangapi.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Textures {

    @SerializedName("SKIN")
    @Expose
    private TextureURL skin;

    @SerializedName("CAPE")
    @Expose
    private TextureURL cape;

    public TextureURL getSkin() {
        return skin;
    }

    public TextureURL getCape() {
        return cape;
    }
}
