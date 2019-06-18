package com.ab.hicarerun.handler;

import com.ab.hicarerun.network.models.TaskModel.TaskChemicalList;

import java.util.HashMap;
import java.util.List;

public interface OnSaveEventHandler {
    public void status(String s);
    public void mode(String s);
    public void amountCollected(String s);
    public void amountToCollect(String s);
//    public void actualPropertySize(String s);
//    public void standardPropertySize(String s);
    public void feedbackCode(String s);
    public void signatory(String s);
    public void signature(String s);
    public void duration(String s);
    public void chemicalList(HashMap<Integer, String> map);
    public void chemReqList(List<TaskChemicalList> mList);
    public void isGeneralChanged(Boolean b);
    public void isChemicalChanged(Boolean b);
    public void isPaymentChanged(Boolean b);
    public void isSignatureChanged(Boolean b);
    public void isOTPValidated(Boolean b);
    public void isFeedbackRequired(Boolean b);
    public void getIncompleteReason(String s);
    public void isAttachmentError(Boolean b);
}
