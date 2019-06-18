package com.ab.hicarerun.viewmodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.ab.hicarerun.network.models.AttachmentModel.GetAttachmentList;

public class AttachmentListViewModel implements Parcelable {
    private Integer id;
    private String ResourceId;
    private String TaskId;
    private String FilePath;
    private String FileName;
    private String Created_On;
    private String File;
    private Boolean isVisible;
    private Boolean isChecked;

    public AttachmentListViewModel() {
        id = 0;
        ResourceId = "NA";
        TaskId = "NA";
        FilePath = "NA";
        FileName = "NA";
        Created_On = "NA";
        File = "NA";
        isVisible = false;
        isChecked = false;
    }

    protected AttachmentListViewModel(Parcel in) {
        ResourceId = in.readString();
        TaskId = in.readString();
        FilePath = in.readString();
        FileName = in.readString();
        Created_On = in.readString();
    }

    public static final Creator<AttachmentListViewModel> CREATOR = new Creator<AttachmentListViewModel>() {
        @Override
        public AttachmentListViewModel createFromParcel(Parcel in) {
            return new AttachmentListViewModel(in);
        }

        @Override
        public AttachmentListViewModel[] newArray(int size) {
            return new AttachmentListViewModel[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Boolean getVisible() {
        return isVisible;
    }

    public void setVisible(Boolean visible) {
        isVisible = visible;
    }

    public Boolean getChecked() {
        return isChecked;
    }

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }

    public String getFile() {
        return File;
    }

    public void setFile(String file) {
        File = file;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(ResourceId);
        dest.writeString(TaskId);
        dest.writeString(FilePath);
        dest.writeString(FileName);
        dest.writeString(Created_On);
    }


    public void clone(GetAttachmentList attachmentList) {
        this.id = attachmentList.getId();
        this.ResourceId = attachmentList.getResourceId();
        this.TaskId = attachmentList.getTaskId();
        this.FilePath = attachmentList.getFilePath();
        this.FileName = attachmentList.getFileName();
        this.Created_On= attachmentList.getCreated_On();
    }
}
