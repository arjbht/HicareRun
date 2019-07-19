package com.ab.hicarerun.handler;

import android.view.View;

public interface UserPaymentClickHandler {
    void onCalendarClicked(View view);
    void onBankNameClicked(View view);
    void onUploadChequeClicked(View view);
    void onSendPaymentLinkClicked(View view);
}
