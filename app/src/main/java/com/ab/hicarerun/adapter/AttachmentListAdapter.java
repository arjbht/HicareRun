package com.ab.hicarerun.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ab.hicarerun.R;
import com.ab.hicarerun.databinding.AttachmentListAdapterBinding;
import com.ab.hicarerun.databinding.TaskListAdapterBinding;
import com.ab.hicarerun.handler.OnListItemClickHandler;
import com.ab.hicarerun.network.models.AttachmentModel.GetAttachmentList;
import com.ab.hicarerun.network.models.ReferralModel.ReferralList;
import com.ab.hicarerun.network.models.TaskModel.Tasks;
import com.ab.hicarerun.utils.AppUtils;
import com.ab.hicarerun.viewmodel.AttachmentListViewModel;
import com.ab.hicarerun.viewmodel.TaskViewModel;
import com.bumptech.glide.Glide;

import net.igenius.customcheckbox.CustomCheckBox;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class AttachmentListAdapter extends RecyclerView.Adapter<AttachmentListAdapter.ViewHolder> {

    private OnListItemClickHandler onItemClickHandler;
    private final Context mContext;
    private static List<AttachmentListViewModel> items = null;
    private AppCompatImageView imgSelect;
    private AppCompatTextView txtDelcount;


    public AttachmentListAdapter(Context context, AppCompatImageView imgSelect, AppCompatTextView txtDelcount) {
        if (items == null) {
            items = new ArrayList<>();
        }
        this.mContext = context;
        this.imgSelect = imgSelect;
        this.txtDelcount = txtDelcount;
    }


    @Override
    public AttachmentListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        AttachmentListAdapterBinding mAttachmentListAdapterBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.attachment_list_adapter, parent, false);
        return new AttachmentListAdapter.ViewHolder(mAttachmentListAdapterBinding);
    }

    @Override
    public void onBindViewHolder(final AttachmentListAdapter.ViewHolder holder, final int position) {
        final AttachmentListViewModel model = items.get(position);

        Glide.with(mContext)
                .load(model.getFilePath())
                .error(android.R.drawable.stat_notify_error)
                .into(holder.mAttachmentListAdapterBinding.imgJob);

        try {
            String date = AppUtils.reFormatDateTime(items.get(position).getCreated_On(), "dd-MMM-yyyy");
            holder.mAttachmentListAdapterBinding.txtDate.setText(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        holder.mAttachmentListAdapterBinding.txtName.setText(model.getFileName());

        if (model.getVisible()) {
            holder.mAttachmentListAdapterBinding.lnrSelect.setVisibility(View.VISIBLE);
        } else {
            holder.mAttachmentListAdapterBinding.lnrSelect.setVisibility(View.GONE);
        }


        imgSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.mAttachmentListAdapterBinding.lnrSelect.getVisibility() == View.VISIBLE) {
                    for (int i = 0; i < items.size(); i++) {
                        items.get(i).setVisible(false);
                        imgSelect.setImageResource(R.drawable.ic_deselect);
                        holder.mAttachmentListAdapterBinding.checkAttach.setChecked(false);
                    }
                    notifyDataSetChanged();

                } else {
                    for (int i = 0; i < items.size(); i++) {
                        items.get(i).setVisible(true);
                        imgSelect.setImageResource(R.drawable.ic_general);

                    }
                    notifyDataSetChanged();
                }
            }
        });

        holder.mAttachmentListAdapterBinding.checkAttach.setOnCheckedChangeListener(new CustomCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CustomCheckBox checkBox, boolean isChecked) {
                model.setChecked(isChecked);

                int count = 0;

                for (int i = 0; i < items.size(); i++) {
                    if (getItem(i).getChecked()) {
                        count++;
                        txtDelcount.setVisibility(View.VISIBLE);
                        txtDelcount.setText("(" + count + ")");
                    } else if (count == 0) {
                        txtDelcount.setVisibility(View.GONE);
                    } else {
                        count--;
                    }

                }


            }
        });


    }

    @Override
    public int getItemCount() {
        return items.size();

    }

    public void setOnItemClickHandler(OnListItemClickHandler onItemClickHandler) {
        this.onItemClickHandler = onItemClickHandler;
    }


    public void setData(List<GetAttachmentList> data) {
        items.clear();
        for (int index = 0; index < data.size(); index++) {
            AttachmentListViewModel attachmentViewModel = new AttachmentListViewModel();
            for (int i = 0; i < data.size(); i++) {
                attachmentViewModel.setVisible(false);
            }
            attachmentViewModel.clone(data.get(index));
            items.add(attachmentViewModel);
        }
    }

    public void addData(List<GetAttachmentList> data) {
        items.clear();
        for (int index = 0; index < data.size(); index++) {
            AttachmentListViewModel attachmentViewModel = new AttachmentListViewModel();
            for (int i = 0; i < data.size(); i++) {
                attachmentViewModel.setVisible(false);
            }
            attachmentViewModel.clone(data.get(index));
            items.add(attachmentViewModel);
        }
    }

    public void removeAll() {
        items.removeAll(items);
        notifyDataSetChanged();
    }

    public AttachmentListViewModel getItem(int position) {
        return items.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final AttachmentListAdapterBinding mAttachmentListAdapterBinding;

        public ViewHolder(AttachmentListAdapterBinding mAttachmentListAdapterBinding) {
            super(mAttachmentListAdapterBinding.getRoot());
            this.mAttachmentListAdapterBinding = mAttachmentListAdapterBinding;
        }
    }

}
