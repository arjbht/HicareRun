package com.ab.hicarerun.network.models.LoggerModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Arjun Bhatt on 7/4/2019.
 */
public class ErrorLogRequest {
    @SerializedName("Level")
    @Expose
    private String level;
    @SerializedName("Error")
    @Expose
    private Object error;
    @SerializedName("Source")
    @Expose
    private String source;
    @SerializedName("ApplicationType")
    @Expose
    private String applicationType;
    @SerializedName("LogMessage")
    @Expose
    private String logMessage;
    @SerializedName("MethodName")
    @Expose
    private String methodName;
    @SerializedName("FilePath")
    @Expose
    private String filePath;
    @SerializedName("LineNo")
    @Expose
    private String lineNo;
    @SerializedName("LogDateTime")
    @Expose
    private String logDateTime;
    @SerializedName("ApplicationName")
    @Expose
    private String applicationName;

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getLineNo() {
        return lineNo;
    }

    public void setLineNo(String lineNo) {
        this.lineNo = lineNo;
    }

    public String getLogDateTime() {
        return logDateTime;
    }

    public void setLogDateTime(String logDateTime) {
        this.logDateTime = logDateTime;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

}

