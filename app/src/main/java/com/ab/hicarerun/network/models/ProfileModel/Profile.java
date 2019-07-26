package com.ab.hicarerun.network.models.ProfileModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Arjun Bhatt on 7/26/2019.
 */
public class Profile {
    @SerializedName("FirstName") @Expose
    private String FirstName;
    @SerializedName("Mobile") @Expose
    private String Mobile;
    @SerializedName("ProfilePic") @Expose
    private String ProfilePic;
    @SerializedName("EmployeeCode") @Expose
    private String EmployeeCode;

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getProfilePic() {
        return ProfilePic;
    }

    public void setProfilePic(String profilePic) {
        ProfilePic = profilePic;
    }

    public String getEmployeeCode() {
        return EmployeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        EmployeeCode = employeeCode;
    }
}
