package com.ab.hicarerun.network.models.AttachmentModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetAttachmentList {
    @SerializedName("Id")
    @Expose
    private Integer Id;
    @SerializedName("ResourceId")
    @Expose
    private String ResourceId;
    @SerializedName("TaskId")
    @Expose
    private String TaskId;
    @SerializedName("FilePath")
    @Expose
    private String FilePath;
    @SerializedName("FileName")
    @Expose
    private String FileName;
    @SerializedName("Created_On")
    @Expose
    private String Created_On;

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public String getResourceId() {
        return ResourceId;
    }

    public void setResourceId(String resourceId) {
        ResourceId = resourceId;
    }

    public String getTaskId() {
        return TaskId;
    }

    public void setTaskId(String taskId) {
        TaskId = taskId;
    }

    public String getFilePath() {
        return FilePath;
    }

    public void setFilePath(String filePath) {
        FilePath = filePath;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getCreated_On() {
        return Created_On;
    }

    public void setCreated_On(String created_On) {
        Created_On = created_On;
    }
}
