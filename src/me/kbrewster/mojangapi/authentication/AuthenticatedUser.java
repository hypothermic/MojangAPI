package me.kbrewster.mojangapi.authentication;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sun.istack.internal.Nullable;

import java.util.List;

public class AuthenticatedUser {

    @SerializedName("accessToken")
    @Expose
    private String accessToken;

    @SerializedName("clientToken")
    @Expose
    private String clientToken;

    @SerializedName("availableProfiles")
    @Expose
    private List<AvailableProfile> availableProfiles = null;

    /*@SerializedName("selectedProfile")
    @Expose
    private SelectedProfile selectedProfile;*/

    @Nullable
    @SerializedName("user")
    @Expose
    private User user;

    public boolean isUserAvailible() {
        return user != null;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public User getUser() {
        return user;
    }
}
