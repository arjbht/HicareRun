package com.ab.hicarerun.handler;

import com.ab.hicarerun.network.models.AttachmentModel.GetAttachmentList;
import com.ab.hicarerun.network.models.TaskModel.TaskChemicalList;

import java.util.List;

public interface OnJobCardEventHandler {
    public void isJobCardEnable(Boolean b);
    public void AttachmentList(List<GetAttachmentList> mList);
}
