package com.ab.hicarerun.handler;

public interface OnCallListItemClickHandler extends OnListItemClickHandler {
    void onPrimaryMobileClicked(int position);
    void onAlternateMobileClicked(int position);
    void onTelePhoneClicked(int position);
    void onTrackLocationIconClicked(int position);
}
