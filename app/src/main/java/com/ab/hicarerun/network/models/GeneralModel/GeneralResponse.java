package com.ab.hicarerun.network.models.GeneralModel;

import com.ab.hicarerun.network.models.TaskModel.Tasks;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.realm.RealmObject;

public class GeneralResponse extends RealmObject {
    @SerializedName("IsSuccess")
    @Expose
    private Boolean isSuccess;
    @SerializedName("Data")
    @Expose
    private GeneralData data;
    @SerializedName("ErrorMessage")
    @Expose
    private String ErrorMessage;


    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

    public GeneralData getData() {
        return data;
    }

    public void setData(GeneralData data) {
        this.data = data;
    }

    public String getErrorMessage() {
        return ErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        ErrorMessage = errorMessage;
    }
}
