package com.ab.hicarerun.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.hicarerun.BaseFragment;
import com.ab.hicarerun.R;
import com.ab.hicarerun.adapter.ChemicalRecycleAdapter;
import com.ab.hicarerun.adapter.TaskListAdapter;
import com.ab.hicarerun.databinding.FragmentChemicalBinding;
import com.ab.hicarerun.handler.OnSaveEventHandler;
import com.ab.hicarerun.network.NetworkCallController;
import com.ab.hicarerun.network.NetworkResponseListner;
import com.ab.hicarerun.network.models.ChemicalModel.ChemicalResponse;
import com.ab.hicarerun.network.models.ChemicalModel.Chemicals;
import com.ab.hicarerun.network.models.GeneralModel.GeneralData;
import com.ab.hicarerun.network.models.ReferralModel.ReferralList;
import com.ab.hicarerun.network.models.TaskModel.TaskChemicalList;
import com.ab.hicarerun.utils.AppUtils;

import net.igenius.customcheckbox.CustomCheckBox;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChemicalFragment extends BaseFragment implements NetworkResponseListner {
    FragmentChemicalBinding mFragmentChemicalBinding;
    private static final String ARG_ISVERIFIED = "ARG_ISVERIFIED";
    private static final String ARG_TASK = "ARG_TASK";
    private static final int CHEMICAL_REQ = 1000;
    private Boolean isVerified = false;
    RecyclerView.LayoutManager layoutManager;
    private String taskId = "";
    private Integer pageNumber = 1;
    ProgressDialog mProgressBar;
    ChemicalRecycleAdapter mAdapter;
    private OnSaveEventHandler mCallback;
    private HashMap<Integer, String> map = new HashMap<>();
    private List<TaskChemicalList> ChemList = new ArrayList<>();
    RealmResults<GeneralData> mGeneralRealmData = null;
    private String status = "";
    private String ActualStatus = "";
    private boolean isChemicalChecked = false;


    public ChemicalFragment() {
        // Required empty public constructor
    }

    public static ChemicalFragment newInstance(String taskId) {
        Bundle args = new Bundle();
        args.putString(ARG_TASK, taskId);
        ChemicalFragment fragment = new ChemicalFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onResume() {
        super.onResume();
        AppUtils.statusCheck(getActivity());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnSaveEventHandler) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement FragmentToActivity");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            taskId = getArguments().getString(ARG_TASK);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFragmentChemicalBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_chemical, container, false);

        mProgressBar = new ProgressDialog(getActivity());
        return mFragmentChemicalBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFragmentChemicalBinding.recycleView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mFragmentChemicalBinding.recycleView.setNestedScrollingEnabled(false);
        mFragmentChemicalBinding.recycleView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        mAdapter = new ChemicalRecycleAdapter(getActivity(), new ChemicalRecycleAdapter.OnEditTextChanged() {
            @Override
            public void onTextChanged(int position, String charSeq) {
                try {
                    if (charSeq != null && map != null) {
                        map.put(position, charSeq);

                        if (map.containsValue("")) {
                            map.remove(position);
                        }
                        ChemList.clear();
                        for(int i=0;i<map.size();i++) {
                            TaskChemicalList ChemModel = new TaskChemicalList();
                            ChemModel.setId(mAdapter.getItem(i).getId());
                            ChemModel.setCWFProductName(mAdapter.getItem(i).getName());
                            ChemModel.setConsumption(mAdapter.getItem(i).getConsumption());
                            ChemModel.setStandard(mAdapter.getItem(i).getStandard());
                            ChemModel.setActual(map.get(i));
                            ChemList.add(ChemModel);
                        }
                        mCallback.chemReqList(ChemList);
                        mGeneralRealmData =
                                getRealm().where(GeneralData.class).findAll();
                        for (int i = 0; i < mAdapter.getItemCount(); i++)
                            getValidation(i);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


        mFragmentChemicalBinding.checkChemVerified.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    isChemicalChecked = isChecked;
                    for (int i = 0; i < mAdapter.getItemCount(); i++)
                        getValidation(i);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

        mFragmentChemicalBinding.recycleView.setAdapter(mAdapter);
        setChemicals();
    }

    private void setChemicals() {
        try {
            mGeneralRealmData =
                    getRealm().where(GeneralData.class).findAll();

            if (mGeneralRealmData != null && mGeneralRealmData.size() > 0) {
                isVerified = mGeneralRealmData.get(0).getAutoSubmitChemicals();
                ActualStatus = mGeneralRealmData.get(0).getSchedulingStatus();


                NetworkCallController controller = new NetworkCallController(this);
                controller.setListner(this);
                controller.getChemicals(CHEMICAL_REQ, taskId);

                callAfterResponse();

            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    @Override
    public void onResponse(int requestCode, Object data) {
        List<Chemicals> items = (List<Chemicals>) data;
        if (items != null) {

            if (pageNumber == 1 && items.size() > 0) {
                mAdapter.setData(items);
                mAdapter.notifyDataSetChanged();
            } else if (items.size() > 0) {
                mAdapter.addData(items);
                mAdapter.notifyDataSetChanged();
            } else {
                pageNumber--;
            }
            callAfterResponse();
        }
    }

    @Override
    public void onFailure(int requestCode) {
    }

    public void getValidation(int position) {
        try {
            mGeneralRealmData =
                    getRealm().where(GeneralData.class).findAll();

            if (mGeneralRealmData != null && mGeneralRealmData.size() > 0) {
                isVerified = mGeneralRealmData.get(0).getAutoSubmitChemicals();

                if (!isVerified) {
                    Log.i("count", String.valueOf(mAdapter.getItemCount()));
                    if (map.size() == mAdapter.getItemCount()) {
                        mCallback.isChemicalChanged(false);
                    } else {
                        mCallback.isChemicalChanged(true);
                    }

                    if(isChemicalChecked){
                        mCallback.isChemicalVerified(false);
                    }else {
                        mCallback.isChemicalVerified(true);
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void callAfterResponse() {
        try {
            if (isVerified || ActualStatus.equals("Completed") || ActualStatus.equals("Incomplete")) {
                mFragmentChemicalBinding.checkChemVerified.setEnabled(false);
                mFragmentChemicalBinding.checkChemVerified.setChecked(true);
            }  else {
                mFragmentChemicalBinding.relChemicals.setVisibility(View.VISIBLE);
                mFragmentChemicalBinding.checkChemVerified.setEnabled(true);
                mFragmentChemicalBinding.checkChemVerified.setChecked(false);
            }

            for (int i = 0; i < mAdapter.getItemCount(); i++) {
                getValidation(i);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
