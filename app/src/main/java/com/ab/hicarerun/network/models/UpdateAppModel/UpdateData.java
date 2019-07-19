package com.ab.hicarerun.network.models.UpdateAppModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Arjun Bhatt on 7/9/2019.
 */
public class UpdateData {
    @SerializedName("version")
    @Expose
    private String version;
    @SerializedName("apkurl")
    @Expose
    private String apkurl;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getApkurl() {
        return apkurl;
    }

    public void setApkurl(String apkurl) {
        this.apkurl = apkurl;
    }
}
