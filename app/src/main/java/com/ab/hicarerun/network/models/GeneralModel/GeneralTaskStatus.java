package com.ab.hicarerun.network.models.GeneralModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class GeneralTaskStatus extends RealmObject {
    @SerializedName("Status")
    @Expose
    private String Status;

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        this.Status = status;
    }
}
