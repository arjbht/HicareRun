package com.ab.hicarerun.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import com.ab.hicarerun.R;
import com.ab.hicarerun.activities.HomeActivity;
import com.ab.hicarerun.databinding.TaskListAdapterBinding;
import com.ab.hicarerun.handler.OnCallListItemClickHandler;
import com.ab.hicarerun.handler.OnDeleteListItemClickHandler;
import com.ab.hicarerun.handler.OnListItemClickHandler;
import com.ab.hicarerun.network.models.TaskModel.Tasks;
import com.ab.hicarerun.utils.AppUtils;
import com.ab.hicarerun.viewmodel.TaskViewModel;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;


public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.ViewHolder> {

    private OnListItemClickHandler onItemClickHandler;
    private OnCallListItemClickHandler onCallListItemClickHandler;
    private String street = "";
    private boolean isShown = false;
    private final Context mContext;
    private Activity activity = null;
    private List<TaskViewModel> items = null;
    String currentdate;

    public TaskListAdapter(Activity context) {
        if (items == null) {
            items = new ArrayList<>();
        }
        this.mContext = context;
        this.activity = context;
        currentdate = AppUtils.currentDateTime();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TaskListAdapterBinding mTaskListAdapterBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.task_list_adapter, parent, false);
        return new ViewHolder(mTaskListAdapterBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.mTaskListAdapterBinding.txtTime.setText(items.get(position).getTaskAssignmentStartTime() + " - " + items.get(position).getTaskAssignmentEndTime());
        holder.mTaskListAdapterBinding.txtName.setText(items.get(position).getAccountName());
        holder.mTaskListAdapterBinding.txtStatus.setPrimaryText(items.get(position).getStatus());
        if (items.get(position).getStatus().equals("Completed")) {
            holder.mTaskListAdapterBinding.txtStatus.setTriangleBackgroundColor(Color.parseColor("#1E90FF"));
        } else if (items.get(position).getStatus().equals("Dispatched")) {
            holder.mTaskListAdapterBinding.txtStatus.setTriangleBackgroundColor(Color.parseColor("#FF8C00"));
        } else if (items.get(position).getStatus().equals("On-Site")) {
            holder.mTaskListAdapterBinding.txtStatus.setTriangleBackgroundColor(Color.parseColor("#ffc40d"));
        } else if (items.get(position).getStatus().equals("Incomplete")) {
            holder.mTaskListAdapterBinding.txtStatus.setTriangleBackgroundColor(Color.parseColor("#FF69B4"));
        }

        holder.mTaskListAdapterBinding.txtService.setText(items.get(position).getServicePlan());
        holder.mTaskListAdapterBinding.txtType.setText(items.get(position).getServiceType());
        if (items.get(position).getStreet() != null) {
            street = items.get(position).getStreet();
        }


        holder.mTaskListAdapterBinding.txtAddress.setText(items.get(position).getBuildingName() + ", "
                + items.get(position).getLocality() + ", "
                + street);

        if (items.get(position).getLandmark() != null && !items.get(position).getLandmark().equals("")) {
            holder.mTaskListAdapterBinding.lnrLandmark.setVisibility(View.VISIBLE);
            holder.mTaskListAdapterBinding.txtLandmark.setText(items.get(position).getLandmark());
        } else {
            holder.mTaskListAdapterBinding.lnrLandmark.setVisibility(View.GONE);
        }
        holder.mTaskListAdapterBinding.txtPostcode.setText(items.get(position).getPostalCode());
        holder.mTaskListAdapterBinding.txtOrderno.setText(items.get(position).getOrderNumber());
        holder.mTaskListAdapterBinding.txtAmount.setText(items.get(position).getAmount());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickHandler.onItemClick(position);
            }
        });


        holder.mTaskListAdapterBinding.dispatchTaskMobileNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCallListItemClickHandler.onPrimaryMobileClicked(position);
            }
        });

        holder.mTaskListAdapterBinding.dispatchTaskAltMobileNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCallListItemClickHandler.onAlternateMobileClicked(position);
            }
        });

        holder.mTaskListAdapterBinding.dispatchTaskPhoneNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCallListItemClickHandler.onTelePhoneClicked(position);
            }
        });
        holder.mTaskListAdapterBinding.imgTracklocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCallListItemClickHandler.onTrackLocationIconClicked(position);
            }
        });

        if (AppUtils.CompareTime(items.get(position).getTaskAssignmentStartTime()).equals("0")) {
            holder.mTaskListAdapterBinding.imgWarning.setVisibility(View.VISIBLE);
            Animation animation = new AlphaAnimation(1, 0); //to change visibility from visible to invisible
            animation.setDuration(500); //1 second duration for each animation cycle
            animation.setInterpolator(new LinearInterpolator());
            animation.setRepeatCount(Animation.INFINITE); //repeating indefinitely
            animation.setRepeatMode(Animation.REVERSE); //animation will start from end point once ended.
            holder.mTaskListAdapterBinding.imgWarning.startAnimation(animation); //to start animation
        } else {
            holder.mTaskListAdapterBinding.imgWarning.setVisibility(View.GONE);
        }
//        startcountdownTimerDemo(holder, position);
    }

    public void startcountdownTimerDemo(final ViewHolder holder, int position) {
        String sdate = items.get(position).getTaskAssignmentStartDate();
        String[] stdate = sdate.split(" ");
        String edate = items.get(position).getTaskAssignmentEndDate();
        String[] edaet = edate.split(" ");


        String[] split_start = sdate.split(" ");
        String start_date = (split_start[0]);
        String start_time = (split_start[1]);


        String[] split_end = edate.split(" ");
        String end_date = (split_end[0]);
        String end_time = (split_end[1]);


        String[] split_sDate = start_date.split("-");
        String year_start = split_sDate[0];
        String month_start = split_sDate[1];
        String day_start = split_sDate[2];


        String[] split_sTime = start_time.split(":");
        String hr_start = split_sTime[0];
        String mn_start = split_sTime[1];
        String secs_start = split_sTime[2];


        String[] split_eDate = end_date.split("-");
        String year_end = split_eDate[0];
        String month_end = split_eDate[1];
        String day_end = split_eDate[2];

        String[] split_eTime = end_time.split(":");
        String hr_end = split_eTime[0];
        String mn_end = split_eTime[1];
        String sec_end = split_eTime[2];


        Time conferenceTime = new Time(Time.getCurrentTimezone());

        final int hour = Integer.parseInt(hr_start);
        final int minute = Integer.parseInt(mn_start);
        final int second = Integer.parseInt(secs_start);
        final int monthDay = Integer.parseInt(day_start);
        final int month = Integer.parseInt(month_start) - 1;
        final int year = Integer.parseInt(year_start);


        final int hourEnd = Integer.parseInt(hr_end);
        final int minuteEnd = Integer.parseInt(mn_end);
        final int secondEnd = Integer.parseInt(sec_end);
        final int monthDayEnd = Integer.parseInt(day_end);
        final int monthEnd = Integer.parseInt(month_end) - 1;
        final int yearEnd = Integer.parseInt(year_end);

        String isBeforeEndDate = AppUtils.compareDates(currentdate, items.get(position).getTaskAssignmentEndDate());

        if (isBeforeEndDate.equals("afterdate")) {

            String isBeforeStartDate = AppUtils.compareDates(currentdate, items.get(position).getTaskAssignmentStartDate());

            if (!isBeforeStartDate.equals("beforedate")) {

                holder.mTaskListAdapterBinding.lnrTimer.setVisibility(View.VISIBLE);
                holder.mTaskListAdapterBinding.txtTimer.setTextColor(Color.parseColor("#2bb77a"));
                conferenceTime.set(second, minute, hour, monthDay, month, year);

            } else {
                conferenceTime.set(secondEnd, minuteEnd, hourEnd, monthDayEnd, monthEnd, yearEnd);
                holder.mTaskListAdapterBinding.lnrTimer.setVisibility(View.GONE);
            }


        } else {


        }
        conferenceTime.normalize(true);
        long confMillis = conferenceTime.toMillis(true);
        Time nowTime = new Time(Time.getCurrentTimezone());
        nowTime.setToNow();
        nowTime.normalize(true);
        long nowMillis = nowTime.toMillis(true);
        long milliDiff = confMillis - nowMillis;

        new CountDownTimer(milliDiff, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int days = (int) ((millisUntilFinished / 1000) / 86400);
                int hours = (int) (((millisUntilFinished / 1000) - (days * 86400)) / 3600);
                int minutes = (int) (((millisUntilFinished / 1000) - ((days * 86400) + (hours * 3600))) / 60);
                int seconds = (int) ((millisUntilFinished / 1000) % 60);
                Animation an = new RotateAnimation(0.0f, 90.0f, 250f, 273f);
                an.setFillAfter(true);

                String Days = String.format("%02d", days);
                String Hours = String.format("%02d", hours);
                String Minutes = String.format("%02d", minutes);
                String Seconds = String.format("%02d", seconds);

                holder.mTaskListAdapterBinding.txtTime.setText(Days + ":" + Hours + ":" + Minutes + ":" + Seconds);
            }

            @Override
            public void onFinish() {
            }

        }.start();

    }


    @Override
    public int getItemCount() {
        return items.size();

    }

    public void setOnItemClickHandler(OnListItemClickHandler onItemClickHandler) {
        this.onItemClickHandler = onItemClickHandler;
    }

    public void setOnCallClickHandler(OnCallListItemClickHandler onCallListItemClickHandler) {
        this.onCallListItemClickHandler = onCallListItemClickHandler;
    }

    public void setData(List<Tasks> data) {
        items.clear();
        for (int index = 0; index < data.size(); index++) {
            TaskViewModel taskViewModel = new TaskViewModel();
            taskViewModel.clone(data.get(index));
            items.add(taskViewModel);
        }
    }

    public void addData(List<Tasks> data) {
        items.clear();
        for (int index = 0; index < data.size(); index++) {
            TaskViewModel taskViewModel = new TaskViewModel();
            taskViewModel.clone(data.get(index));
            items.add(taskViewModel);
        }
    }

    public TaskViewModel getItem(int position) {
        return items.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TaskListAdapterBinding mTaskListAdapterBinding;

        public ViewHolder(TaskListAdapterBinding mTaskListAdapterBinding) {
            super(mTaskListAdapterBinding.getRoot());
            this.mTaskListAdapterBinding = mTaskListAdapterBinding;

            // sequence example
            if(!isShown) {
                ShowcaseConfig config = new ShowcaseConfig();
                config.setDelay(500); // half second between each showcase view

                MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(activity, "arj");

                sequence.setConfig(config);


                sequence.addSequenceItem(mTaskListAdapterBinding.imgTracklocation,
                        "This is to track customer location.", "GOT IT");

                sequence.addSequenceItem(mTaskListAdapterBinding.dispatchTaskMobileNo,
                        "This is to call customer.", "GOT IT");

                sequence.addSequenceItem(mTaskListAdapterBinding.txtStatus,
                        "You can check your task status here.", "GOT IT");

                sequence.start();
                isShown = true;
            }
        }
    }
}
