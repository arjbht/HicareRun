package com.ab.hicarerun.network.models.GeneralModel;

import io.realm.RealmModel;
import io.realm.RealmObject;

public class GeneralPaymentMode extends RealmObject {
    private String Value;

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }
}
