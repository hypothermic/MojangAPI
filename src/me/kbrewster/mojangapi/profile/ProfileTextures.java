package me.kbrewster.mojangapi.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProfileTextures {

    @SerializedName("timestamp")
    @Expose
    private long timestamp;

    @SerializedName("profileId")
    @Expose
    private String profileId;

    @SerializedName("profileName")
    @Expose
    private String profileName;

    @SerializedName("textures")
    @Expose
    private Textures textures;

    public ProfileTextures() {
    }

    public Textures getTextures() {
        return textures;
    }

    public String getProfileName() {
        return profileName;
    }

    public String getProfileId() {
        return profileId;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
