package com.ab.hicarerun.network.models.LoggerModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Arjun Bhatt on 7/25/2019.
 */
public class ErrorLog {
    @SerializedName("UserId")
    @Expose
    private Integer userId;
    @SerializedName("Level")
    @Expose
    private String level;
    @SerializedName("Type")
    @Expose
    private String type;
    @SerializedName("Source")
    @Expose
    private String source;
    @SerializedName("ApplicationType")
    @Expose
    private String applicationType;
    @SerializedName("ApplicationName")
    @Expose
    private String applicationName;
    @SerializedName("LogMessage")
    @Expose
    private String logMessage;
    @SerializedName("MethodName")
    @Expose
    private String methodName;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(String applicationType) {
        this.applicationType = applicationType;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getLogMessage() {
        return logMessage;
    }

    public void setLogMessage(String logMessage) {
        this.logMessage = logMessage;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

}
