package me.kbrewster.mojangapi.authentication;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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

    @SerializedName("selectedProfile")
    @Expose
    private SelectedProfile selectedProfile;

    @SerializedName("user")
    @Expose
    private User user;

}
