package com.ab.hicarerun.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ab.hicarerun.R;
import com.ab.hicarerun.databinding.ReferralListAdapterBinding;
import com.ab.hicarerun.databinding.TaskListAdapterBinding;
import com.ab.hicarerun.handler.OnDeleteListItemClickHandler;
import com.ab.hicarerun.handler.OnListItemClickHandler;
import com.ab.hicarerun.network.models.ReferralModel.ReferralList;
import com.ab.hicarerun.network.models.TaskModel.Tasks;
import com.ab.hicarerun.viewmodel.ReferralListViewModel;
import com.ab.hicarerun.viewmodel.TaskViewModel;

import java.util.ArrayList;
import java.util.List;

public class ReferralListAdapter extends RecyclerView.Adapter<ReferralListAdapter.ViewHolder> {


    private OnDeleteListItemClickHandler onItemClickHandler;
    private final Context mContext;
    private List<ReferralListViewModel> items = null;

    public ReferralListAdapter(Context context) {
        if (items == null) {
            items = new ArrayList<>();
        }
        this.mContext = context;
    }


    @Override
    public ReferralListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ReferralListAdapterBinding mReferralListAdapterBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.referral_list_adapter, parent, false);
        return new ReferralListAdapter.ViewHolder(mReferralListAdapterBinding);
    }


    @Override
    public void onBindViewHolder(ReferralListAdapter.ViewHolder holder, final int position) {
        holder.mReferralListAdapterBinding.txtName.setText(items.get(position).getFirstName() + " " + items.get(position).getLastName());
        holder.mReferralListAdapterBinding.txtMobile.setText(items.get(position).getMobileNo());
        holder.mReferralListAdapterBinding.txtAltMobile.setText(items.get(position).getAlternateMobileNo());
        holder.mReferralListAdapterBinding.txtInterested.setText(items.get(position).getInterestedService());
        holder.mReferralListAdapterBinding.txtEmail.setText(items.get(position).getEmail());


        holder.mReferralListAdapterBinding.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickHandler.onDeleteItemClicked(position);
            }
        });
    }

    public void setOnItemClickHandler(OnDeleteListItemClickHandler onItemClickHandler) {
        this.onItemClickHandler = onItemClickHandler;
    }


    @Override
    public int getItemCount() {
        return items.size();

    }

    public void setData(List<ReferralList> data) {
        items.clear();
        for (int index = 0; index < data.size(); index++) {
            ReferralListViewModel referralListViewModel = new ReferralListViewModel();
            referralListViewModel.clone(data.get(index));
            items.add(referralListViewModel);
        }
    }

    public void addData(List<ReferralList> data) {
        items.clear();
        for (int index = 0; index < data.size(); index++) {
            ReferralListViewModel referralListViewModel = new ReferralListViewModel();
            referralListViewModel.clone(data.get(index));
            items.add(referralListViewModel);
        }
    }
    public void removeAll() {
        items.removeAll(items);
        notifyDataSetChanged();
    }

    public ReferralListViewModel getItem(int position) {
        return items.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ReferralListAdapterBinding mReferralListAdapterBinding;

        public ViewHolder(ReferralListAdapterBinding mReferralListAdapterBinding) {
            super(mReferralListAdapterBinding.getRoot());
            this.mReferralListAdapterBinding = mReferralListAdapterBinding;
        }
    }
}
