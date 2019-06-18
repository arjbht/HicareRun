package com.ab.hicarerun.network.models.ChemicalModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Chemicals {
    @SerializedName("Id") @Expose
    private String Id;

    @SerializedName("Standard_Usage__c") @Expose
    private String Standard_Usage;

    @SerializedName("Actual_Usage__c") @Expose
    private String Actual_Usage;

    @SerializedName("Consumption_U_O_M__c") @Expose
    private String Consumption;

    @SerializedName("CWFProductName__c") @Expose
    private String CWFProductName;


    public Chemicals() {
        Id = "NA";
        Standard_Usage = "NA";
        Actual_Usage = "NA";
        Consumption = "NA";
        CWFProductName = "NA";
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getStandard_Usage() {
        return Standard_Usage;
    }

    public void setStandard_Usage(String standard_Usage) {
        Standard_Usage = standard_Usage;
    }

    public String getActual_Usage() {
        return Actual_Usage;
    }

    public void setActual_Usage(String actual_Usage) {
        Actual_Usage = actual_Usage;
    }

    public String getConsumption() {
        return Consumption;
    }

    public void setConsumption(String consumption) {
        Consumption = consumption;
    }

    public String getCWFProductName() {
        return CWFProductName;
    }

    public void setCWFProductName(String CWFProductName) {
        this.CWFProductName = CWFProductName;
    }
}
