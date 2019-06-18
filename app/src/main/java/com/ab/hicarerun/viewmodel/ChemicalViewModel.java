package com.ab.hicarerun.viewmodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.ab.hicarerun.network.models.AttachmentModel.GetAttachmentList;
import com.ab.hicarerun.network.models.ChemicalModel.Chemicals;

import java.util.HashMap;
import java.util.List;

public class ChemicalViewModel implements Parcelable {
    private String id;
    private String name;
    private String consumption;
    private String standard;
    private String actual;
    private String edtActual;
    private HashMap<Integer, String> actualList;

    public ChemicalViewModel() {
        this.id = "NA";
        this.name = "NA";
        this.consumption = "NA";
        this.standard = "NA";
        this.actual = "NA";
        this.edtActual = "NA";
    }

    protected ChemicalViewModel(Parcel in) {
        id = in.readString();
        name = in.readString();
        consumption = in.readString();
        standard = in.readString();
        actual = in.readString();
        edtActual = in.readString();
    }


    public static final Creator<ChemicalViewModel> CREATOR = new Creator<ChemicalViewModel>() {
        @Override
        public ChemicalViewModel createFromParcel(Parcel in) {
            return new ChemicalViewModel(in);
        }

        @Override
        public ChemicalViewModel[] newArray(int size) {
            return new ChemicalViewModel[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConsumption() {
        return consumption;
    }

    public void setConsumption(String consumption) {
        this.consumption = consumption;
    }

    public String getStandard() {
        return standard;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }

    public String getActual() {
        return actual;
    }

    public void setActual(String actual) {
        this.actual = actual;
    }

    public String getEdtActual() {
        return edtActual;
    }

    public void setEdtActual(String edtActual) {
        this.edtActual = edtActual;
    }

    public HashMap<Integer, String> getActualList() {
        return actualList;
    }

    public void setActualList(HashMap<Integer, String> actualList) {
        this.actualList = actualList;
    }

    public void clone(Chemicals chemicals) {
        this.id = chemicals.getId();
        this.name = chemicals.getCWFProductName();
        this.consumption = chemicals.getConsumption();
        this.standard = chemicals.getStandard_Usage();
        this.edtActual = chemicals.getActual_Usage();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(consumption);
        dest.writeString(standard);
        dest.writeString(actual);
        dest.writeString(edtActual);
    }
}
