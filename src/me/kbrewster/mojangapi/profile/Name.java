package me.kbrewster.mojangapi.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Name {

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("changedToAt")
    @Expose
    private long changedToAt;

    public String getName() {
        return name;
    }

    public long getChangedToAt() {
        return changedToAt;
    }
}
