package com.ab.hicarerun.network.models.AttendanceModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Arjun Bhatt on 6/17/2019.
 */
public class ProfilePicRequest {
    @SerializedName("resourceId")
    @Expose
    private String resourceId;
    @SerializedName("profilePic")
    @Expose
    private String profilePic;


    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
}
