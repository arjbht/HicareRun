package com.ab.hicarerun.network.models.ChemicalModel;

import com.ab.hicarerun.network.models.ReferralModel.ReferralList;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChemicalResponse {

    @SerializedName("IsSuccess") @Expose
    private String IsSuccess;
    @SerializedName("Data") @Expose
    private List<Chemicals> data = null;
    @SerializedName("ErrorMessage") @Expose
    private String ErrorMessage;



    public ChemicalResponse() {
        IsSuccess = "NA";
        ErrorMessage = "NA";
    }

    public String getIsSuccess() {
        return IsSuccess;
    }

    public void setIsSuccess(String isSuccess) {
        IsSuccess = isSuccess;
    }

    public List<Chemicals> getData() {
        return data;
    }

    public void setData(List<Chemicals> data) {
        this.data = data;
    }

    public String getErrorMessage() {
        return ErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        ErrorMessage = errorMessage;
    }
}
