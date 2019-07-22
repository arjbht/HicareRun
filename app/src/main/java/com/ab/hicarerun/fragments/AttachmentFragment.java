package com.ab.hicarerun.fragments;


import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ab.hicarerun.BaseApplication;
import com.ab.hicarerun.BaseFragment;
import com.ab.hicarerun.R;
import com.ab.hicarerun.adapter.AttachmentListAdapter;
import com.ab.hicarerun.adapter.TaskListAdapter;
import com.ab.hicarerun.databinding.FragmentAttachmentBinding;
import com.ab.hicarerun.handler.OnJobCardEventHandler;
import com.ab.hicarerun.handler.OnSaveEventHandler;
import com.ab.hicarerun.handler.UserAttachmentClickHandler;
import com.ab.hicarerun.network.NetworkCallController;
import com.ab.hicarerun.network.NetworkResponseListner;
import com.ab.hicarerun.network.models.AttachmentModel.AttachmentDeleteRequest;
import com.ab.hicarerun.network.models.AttachmentModel.GetAttachmentList;
import com.ab.hicarerun.network.models.AttachmentModel.PostAttachmentResponse;
import com.ab.hicarerun.network.models.GeneralModel.GeneralData;
import com.ab.hicarerun.network.models.LoginResponse;
import com.ab.hicarerun.utils.AppUtils;
import com.ab.hicarerun.utils.SharedPreferencesUtility;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import io.realm.RealmResults;

import static android.view.View.GONE;

/**
 * A simple {@link Fragment} subclass.
 */
public class AttachmentFragment extends BaseFragment implements UserAttachmentClickHandler, NetworkResponseListner {
    FragmentAttachmentBinding mFragmentAttachmentBinding;
    public static final String ARGS_TASKS = "ARGS_TASKS";
    private static final int POST_ATTACHMENT_REQ = 2000;
    private static final int GET_ATTACHMENT_REQ = 3000;
    private static final int DELETE_ATTACHMENT_REQ = 4000;
    private String UserId = "";
    private String taskId = "";
    private File imgFile;
    private ArrayList<String> images = new ArrayList<>();
    private Integer pageNumber = 1;
    AttachmentListAdapter mAdapter;
    RecyclerView.LayoutManager layoutManager;
    private boolean isCardRequired = false;
    private OnJobCardEventHandler mCallback;


    public AttachmentFragment() {
        // Required empty public constructor
    }

    public static AttachmentFragment newInstance(String taskId) {
        Bundle args = new Bundle();
        args.putString(ARGS_TASKS, taskId);
        AttachmentFragment fragment = new AttachmentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            taskId = getArguments().getString(ARGS_TASKS);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnJobCardEventHandler) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement FragmentToActivity");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFragmentAttachmentBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_attachment, container, false);
        mFragmentAttachmentBinding.setHandler(this);
        return mFragmentAttachmentBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Upload Job Card");
        mFragmentAttachmentBinding.recycleView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        mFragmentAttachmentBinding.recycleView.setLayoutManager(layoutManager);
        mAdapter = new AttachmentListAdapter(getActivity(), mFragmentAttachmentBinding.imgSelect, mFragmentAttachmentBinding.txtDelcount);
        mFragmentAttachmentBinding.recycleView.setAdapter(mAdapter);
        getAttachmentList();

    }


    private void getAttachmentList() {
        try {
            RealmResults<LoginResponse> LoginRealmModels =
                    BaseApplication.getRealm().where(LoginResponse.class).findAll();
            if (LoginRealmModels != null && LoginRealmModels.size() > 0) {
                UserId = LoginRealmModels.get(0).getUserID();
                NetworkCallController controller = new NetworkCallController(this);
                controller.setListner(this);
                controller.getAttachments(GET_ATTACHMENT_REQ, taskId, UserId);
            }
        } catch (Exception e) {
            String lineNo = String.valueOf(new Exception().getStackTrace()[0].getLineNumber());
            AppUtils.sendErrorLogs(getActivity(), e.toString(), getClass().getSimpleName(), "getAttachmentList", lineNo);
        }

    }

    @Override
    public void onAddImageClicked(View view) {
        try {
            RealmResults<GeneralData> mGeneralRealmData =
                    getRealm().where(GeneralData.class).findAll();

            if (mGeneralRealmData != null && mGeneralRealmData.size() > 0) {
                isCardRequired = mGeneralRealmData.get(0).getJobCardRequired();
                if (isCardRequired) {
                    PickImageDialog.build(new PickSetup()).setOnPickResult(new IPickResult() {
                        @Override
                        public void onPickResult(PickResult pickResult) {
                            if (pickResult.getError() == null) {
                                images.add(pickResult.getPath());
                                imgFile = new File(pickResult.getPath());
                                RealmResults<LoginResponse> LoginRealmModels =
                                        BaseApplication.getRealm().where(LoginResponse.class).findAll();
                                if (pickResult.getPath() != null) {
                                    if (LoginRealmModels != null && LoginRealmModels.size() > 0) {
                                        UserId = LoginRealmModels.get(0).getUserID();
                                        NetworkCallController controller = new NetworkCallController();
                                        controller.setListner(new NetworkResponseListner() {
                                            @Override
                                            public void onResponse(int requestCode, Object response) {
                                                PostAttachmentResponse postResponse = (PostAttachmentResponse) response;
                                                if (postResponse.getSuccess() == true) {
//                                                    Toast.makeText(getActivity(), "Post Successfully.", Toast.LENGTH_LONG).show();
                                                    Toasty.success(getActivity(), "Job card uploaded successfully.", Toast.LENGTH_LONG).show();
                                                    getAttachmentList();
                                                } else {
                                                    Toast.makeText(getActivity(), "Posting Failed.", Toast.LENGTH_LONG).show();
                                                }
                                            }

                                            @Override
                                            public void onFailure(int requestCode) {

                                            }
                                        });

                                        controller.postAttachments(POST_ATTACHMENT_REQ, imgFile, taskId, UserId);
                                    }
                                }
                            }
                        }
                    }).show(getActivity());

                } else {
                    mCallback.isJobCardEnable(false);
                    Toast.makeText(getActivity(), "Disable", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String lineNo = String.valueOf(new Exception().getStackTrace()[0].getLineNumber());
            AppUtils.sendErrorLogs(getActivity(), e.toString(), getClass().getSimpleName(), "onAddImageClicked", lineNo);
        }

    }

    @Override
    public void onSelectImageClicked(View view) {

    }


    @Override
    public void onDeleteImageClicked(View view) {
        try {
            AttachmentDeleteRequest request;
            List<AttachmentDeleteRequest> model = null;
            model = new ArrayList<>();
            int count = mAdapter.getItemCount();
            for (int i = 0; i < count; i++) {
                if (mAdapter.getItem(i).getChecked()) {
                    request = new AttachmentDeleteRequest();
                    try {
                        request.setId(mAdapter.getItem(i).getId());
                        request.setTaskId(mAdapter.getItem(i).getTaskId());
                        request.setResourceId(mAdapter.getItem(i).getResourceId());
                        request.setCreated_On(mAdapter.getItem(i).getCreated_On());
                        request.setFileName(mAdapter.getItem(i).getFileName());
                        request.setFilePath(mAdapter.getItem(i).getFilePath());
                        request.setFile(mAdapter.getItem(i).getFile());
                        model.add(request);
                    } catch (Exception e) {
                        Log.i("e", e.toString());
                        Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT);
                    }
                }
            }
            NetworkCallController controller = new NetworkCallController(this);
            controller.setListner(this);
            controller.getDeleteAttachments(DELETE_ATTACHMENT_REQ, model);
        }catch (Exception e){
            String lineNo = String.valueOf(new Exception().getStackTrace()[0].getLineNumber());
            AppUtils.sendErrorLogs(getActivity(), e.toString(), getClass().getSimpleName(), "onDeleteImageClicked", lineNo);
        }

    }

    @Override
    public void onResponse(int requestCode, Object data) {
        switch (requestCode) {
            case GET_ATTACHMENT_REQ:
                List<GetAttachmentList> items = (List<GetAttachmentList>) data;


                if (items != null) {
                    if (pageNumber == 1 && items.size() > 0) {
                        mFragmentAttachmentBinding.txtData.setVisibility(View.GONE);
                        mAdapter.setData(items);
                        mAdapter.notifyDataSetChanged();
                        mFragmentAttachmentBinding.imgSelect.setEnabled(true);
                        if (isCardRequired) {
                            mCallback.isJobCardEnable(true);
                        } else {
                            mCallback.isJobCardEnable(false);
                        }
                        mCallback.AttachmentList(items);
                    } else if (items.size() > 0) {
                        mFragmentAttachmentBinding.txtData.setVisibility(View.GONE);
                        mAdapter.addData(items);
                        mAdapter.notifyDataSetChanged();
                        mFragmentAttachmentBinding.imgSelect.setEnabled(true);
                        if (isCardRequired) {
                            mCallback.isJobCardEnable(true);

                        } else {
                            mCallback.isJobCardEnable(false);
                        }
                        mCallback.AttachmentList(items);
                    } else {
                        pageNumber--;
                    }
                } else {
                    mFragmentAttachmentBinding.txtData.setVisibility(View.VISIBLE);
                    mFragmentAttachmentBinding.imgSelect.setEnabled(false);
                    mCallback.isJobCardEnable(false);
                    mCallback.AttachmentList(items);
                }

                break;

            case DELETE_ATTACHMENT_REQ:
                PostAttachmentResponse deleteResponse = (PostAttachmentResponse) data;

                if (deleteResponse.getSuccess()) {
                    mAdapter.removeAll();
                    if (mAdapter.getItemCount() == 0) {
                        mFragmentAttachmentBinding.txtData.setVisibility(View.VISIBLE);
                    } else {
                        mFragmentAttachmentBinding.txtData.setVisibility(GONE);
                    }


                    for (int i = 0; i < mAdapter.getItemCount(); i++) {
                        if (mAdapter.getItem(i).getChecked()) {
                            mAdapter.getItem(i).setChecked(false);
                        }
                    }

                    getAttachmentList();
                    Toasty.success(getActivity(), "Deleted successfully.", Toasty.LENGTH_SHORT).show();
                    mAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity(), "Failed.", Toast.LENGTH_LONG).show();

                }
                break;
        }
    }

    @Override
    public void onFailure(int requestCode) {

    }
}
