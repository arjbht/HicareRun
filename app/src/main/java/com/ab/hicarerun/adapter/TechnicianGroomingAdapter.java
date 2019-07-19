package com.ab.hicarerun.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ab.hicarerun.R;
import com.ab.hicarerun.databinding.OnJobTechniciansListBinding;
import com.ab.hicarerun.databinding.ReferralListAdapterBinding;
import com.ab.hicarerun.handler.OnCaptureListItemClickHandler;
import com.ab.hicarerun.handler.OnDeleteListItemClickHandler;
import com.ab.hicarerun.network.models.ReferralModel.ReferralList;
import com.ab.hicarerun.network.models.TechnicianGroomingModel.TechGroom;
import com.ab.hicarerun.viewmodel.GroomingViewModel;
import com.ab.hicarerun.viewmodel.ReferralListViewModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arjun Bhatt on 7/18/2019.
 */
public class TechnicianGroomingAdapter extends RecyclerView.Adapter<TechnicianGroomingAdapter.ViewHolder> {


    private OnCaptureListItemClickHandler onItemClickHandler;
    private final Context mContext;
    private List<GroomingViewModel> items = null;

    public TechnicianGroomingAdapter(Context context) {
        if (items == null) {
            items = new ArrayList<>();
        }
        this.mContext = context;
    }


    @Override
    public TechnicianGroomingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        OnJobTechniciansListBinding mOnJobTechniciansListBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.on_job_technicians_list, parent, false);
        return new TechnicianGroomingAdapter.ViewHolder(mOnJobTechniciansListBinding);
    }


    @Override
    public void onBindViewHolder(TechnicianGroomingAdapter.ViewHolder holder, final int position) {
        holder.mOnJobTechniciansListBinding.txtTechnician.setText(items.get(position).getTechnicianName());
        holder.mOnJobTechniciansListBinding.txtMobile.setText(items.get(position).getMobileNo());
        holder.mOnJobTechniciansListBinding.txtCode.setText(items.get(position).getEmployeeCode());
        if (items.get(position).getImageUrl() != null) {
            Glide.with(mContext).load(items.get(position).getImageUrl()).into(holder.mOnJobTechniciansListBinding.imgProfile);
            holder.mOnJobTechniciansListBinding.lnrCapture.setVisibility(View.GONE);
            holder.mOnJobTechniciansListBinding.imgProfile.setVisibility(View.VISIBLE);

        } else {
            holder.mOnJobTechniciansListBinding.lnrCapture.setVisibility(View.VISIBLE);
            holder.mOnJobTechniciansListBinding.imgProfile.setVisibility(View.GONE);
        }

        holder.mOnJobTechniciansListBinding.imgCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickHandler.onCaptureImageItemClick(position);
            }
        });

        holder.mOnJobTechniciansListBinding.imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickHandler.onViewImageItemClick(position);
            }
        });
    }

    public void setOnItemClickHandler(OnCaptureListItemClickHandler onItemClickHandler) {
        this.onItemClickHandler = onItemClickHandler;
    }


    @Override
    public int getItemCount() {
        return items.size();

    }

    public void setData(List<TechGroom> data) {
        items.clear();
        for (int index = 0; index < data.size(); index++) {
            GroomingViewModel groomingViewModel = new GroomingViewModel();
            groomingViewModel.clone(data.get(index));
            items.add(groomingViewModel);
        }
    }

    public void addData(List<TechGroom> data) {
        items.clear();
        for (int index = 0; index < data.size(); index++) {
            GroomingViewModel groomingViewModel = new GroomingViewModel();
            groomingViewModel.clone(data.get(index));
            items.add(groomingViewModel);
        }
    }

    public void removeAll() {
        items.removeAll(items);
        notifyDataSetChanged();
    }

    public GroomingViewModel getItem(int position) {
        return items.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final OnJobTechniciansListBinding mOnJobTechniciansListBinding;

        public ViewHolder(OnJobTechniciansListBinding mOnJobTechniciansListBinding) {
            super(mOnJobTechniciansListBinding.getRoot());
            this.mOnJobTechniciansListBinding = mOnJobTechniciansListBinding;
        }
    }
}
