package com.ab.hicarerun.network.models.GeneralModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class IncompleteReason extends RealmObject {

    @SerializedName("Value")
    @Expose
    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
