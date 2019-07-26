package com.ab.hicarerun.network.models.LoggerModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Arjun Bhatt on 7/25/2019.
 */
public class ErrorLoggerModel {
    @SerializedName("ApplicationType")
    @Expose
    private String applicationType;
    @SerializedName("ApplicationName")
    @Expose
    private Object applicationName;
    @SerializedName("Source")
    @Expose
    private String source;
    @SerializedName("Data")
    @Expose
    private String data;

    public String getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(String applicationType) {
        this.applicationType = applicationType;
    }

    public Object getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(Object applicationName) {
        this.applicationName = applicationName;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}


