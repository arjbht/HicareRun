package com.ab.hicarerun.handler;

/**
 * Created by Arjun Bhatt on 7/18/2019.
 */
public interface OnCaptureListItemClickHandler extends OnListItemClickHandler {
    void onCaptureImageItemClick(int position);

    void onViewImageItemClick(int position);
}
