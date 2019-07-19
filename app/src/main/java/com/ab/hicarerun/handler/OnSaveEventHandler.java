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

    public void isSignatureValidated(Boolean b);

    public void isOTPValidated(Boolean b);

    public void isFeedbackRequired(Boolean b);

    public void getIncompleteReason(String s);

    public void isAttachmentError(Boolean b);

    public void isIncompleteReason(Boolean b);

    public void bankName(String s);

    public void chequeNumber(String s);

    public void chequeDate(String s);

    public void chequeImage(String s);

    public void isAmountCollectedRequired(Boolean b);

    public void isBankNameRequired(Boolean b);

    public void isChequeDateRequired(Boolean b);

    public void isChequeNumberRequired(Boolean b);

    public void isInvalidChequeNumber(Boolean b);

    public void isChequeImageRequired(Boolean b);

    public void isACEquals(Boolean b);
}
