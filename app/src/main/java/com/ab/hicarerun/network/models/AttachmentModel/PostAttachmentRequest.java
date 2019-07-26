package com.ab.hicarerun.network.models.AttachmentModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Arjun Bhatt on 7/23/2019.
 */
public class PostAttachmentRequest {

    @SerializedName("Id")
    @Expose
    private Integer id;
    @SerializedName("ResourceId")
    @Expose
    private String resourceId;
    @SerializedName("TaskId")
    @Expose
    private String taskId;
    @SerializedName("FilePath")
    @Expose
    private String filePath;
    @SerializedName("FileName")
    @Expose
    private String fileName;
    @SerializedName("Created_On")
    @Expose
    private String createdOn;
    @SerializedName("File")
    @Expose
    private String file;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
