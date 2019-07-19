package com.ab.hicarerun.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.format.Time;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Chronometer;

import com.ab.hicarerun.BaseApplication;
import com.ab.hicarerun.R;
import com.ab.hicarerun.activities.HomeActivity;
import com.ab.hicarerun.databinding.TaskListAdapterBinding;
import com.ab.hicarerun.handler.OnCallListItemClickHandler;
import com.ab.hicarerun.handler.OnDeleteListItemClickHandler;
import com.ab.hicarerun.handler.OnListItemClickHandler;
import com.ab.hicarerun.network.models.LoginResponse;
import com.ab.hicarerun.network.models.TaskModel.Tasks;
import com.ab.hicarerun.utils.AppUtils;
import com.ab.hicarerun.utils.MyCountDownTimer;
import com.ab.hicarerun.viewmodel.TaskViewModel;
import com.orhanobut.logger.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.RealmResults;
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
    long milli = 0;
    private Handler customHandler = new Handler();
    private long startTime = 0L;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    Runnable updateTimerThread;

    private int time = 20;
    private Timer timer;

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
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mTaskListAdapterBinding.imgWarning.setVisibility(View.GONE);
        holder.mTaskListAdapterBinding.txtTime.setText(items.get(position).getTaskAssignmentStartTime() + " - " + items.get(position).getTaskAssignmentEndTime());
        holder.mTaskListAdapterBinding.txtName.setText(items.get(position).getAccountName());
        holder.mTaskListAdapterBinding.txtStatus.setPrimaryText(items.get(position).getStatus());
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.blink);
        holder.mTaskListAdapterBinding.imgWarning.startAnimation(animation);
        if (items.get(position).getStatus().equals("Completed")) {
//            holder.mTaskListAdapterBinding.txtStatus.setTriangleBackgroundColor(Color.parseColor("#1E90FF"));
            holder.mTaskListAdapterBinding.txtStatus.setTriangleBackgroundColor(Color.parseColor("#1E90FF"));
        } else if (items.get(position).getStatus().equals("Dispatched")) {
//            holder.mTaskListAdapterBinding.txtStatus.setTriangleBackgroundColor(Color.parseColor("#FF8C00"));
            holder.mTaskListAdapterBinding.txtStatus.setTriangleBackgroundColor(Color.parseColor("#FFA500"));
        } else if (items.get(position).getStatus().equals("On-Site")) {
//            holder.mTaskListAdapterBinding.txtStatus.setTriangleBackgroundColor(Color.parseColor("#ffc40d"));
            holder.mTaskListAdapterBinding.txtStatus.setTriangleBackgroundColor(Color.parseColor("#e5e112"));
        } else if (items.get(position).getStatus().equals("Incomplete")) {
            holder.mTaskListAdapterBinding.txtStatus.setTriangleBackgroundColor(Color.parseColor("#FF69B4"));
        }

        if(items.get(0).getTag()!=null){
            holder.mTaskListAdapterBinding.lnrTag.setVisibility(View.VISIBLE);
            holder.mTaskListAdapterBinding.txtTag.setText(items.get(0).getTag());
        }else {
            holder.mTaskListAdapterBinding.lnrTag.setVisibility(View.GONE);
        }

        if( items.get(0).getSequenceNumber() != 0){
            holder.mTaskListAdapterBinding.lnrSequence.setVisibility(View.VISIBLE);
            holder.mTaskListAdapterBinding.txtSequence.setText(String.valueOf(items.get(0).getSequenceNumber()));
        }else {
            holder.mTaskListAdapterBinding.lnrSequence.setVisibility(View.GONE);
        }

        holder.mTaskListAdapterBinding.txtService.setText(items.get(position).getServicePlan());
        holder.mTaskListAdapterBinding.txtType.setText(items.get(position).getServiceType());
        if (items.get(position).getStreet() != null) {
            street = items.get(position).getStreet();
        }

        if (items.get(position).getBuildingName().trim().length() != 0) {
            holder.mTaskListAdapterBinding.lnrAddress.setVisibility(View.VISIBLE);
            holder.mTaskListAdapterBinding.txtAddress.setText(items.get(position).getBuildingName() + ", "
                    + items.get(position).getLocality() + ", "
                    + street);
        } else {
            holder.mTaskListAdapterBinding.lnrAddress.setVisibility(View.GONE);
        }

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
        holder.mTaskListAdapterBinding.lnrMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCallListItemClickHandler.onTrackLocationIconClicked(position);
            }
        });


        final Handler ha = new Handler();
        ha.postDelayed(new Runnable() {

            @Override
            public void run() {
                //call function

                if (items.get(position).getStatus().equals("Dispatched")) {
                    startCountUpTimer(holder, position);
                }else {
                    holder.mTaskListAdapterBinding.imgWarning.setVisibility(View.GONE);
                }
                ha.postDelayed(this, 100000);
            }
        }, 10000);


        holder.mTaskListAdapterBinding.imgWarning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    String titleText = "Running Late...";

                    // Initialize a new foreground color span instance
                    ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.BLACK);
                    // Initialize a new spannable string builder instance
                    SpannableStringBuilder ssBuilder = new SpannableStringBuilder(titleText);
                    // Apply the text color span
                    ssBuilder.setSpan(
                            foregroundColorSpan,
                            0,
                            titleText.length(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    );

                    startCountUpTimer(holder, position);
                    String time = AppUtils.millisTOHr(milli);
                    getErrorDialog(ssBuilder, "You're running late by " + time + "." );

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void startCountUpTimer(final ViewHolder holder, int position) {
        String sdate = items.get(position).getTaskAssignmentStartDate();
        String edate = items.get(position).getTaskAssignmentEndDate();
//        yyyy-MM-dd HH:mm:ss

        String[] split_start = sdate.split("T");
        String start_date = (split_start[0]);
        String start_time = (split_start[1]);
        final String sDate = start_date + " " + start_time;

        String[] split_end = edate.split("T");
        String end_date = (split_end[0]);
        String end_time = (split_end[1]);
        String eDate = end_date + " " + end_time;


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


        final Time conferenceTime = new Time(Time.getCurrentTimezone());

        final int hour = Integer.parseInt(hr_start);
        final int minute = Integer.parseInt(mn_start);
        final int second = Integer.parseInt(secs_start);
        final int monthDay = Integer.parseInt(day_start);
        final int month = Integer.parseInt(month_start) - 1;
        final int year = Integer.parseInt(year_start);
        String isStartDate = AppUtils.compareDates(AppUtils.currentDateTime(), sDate);

//        if (isStartDate.equals("afterdate")) {
//            conferenceTime.set(second, minute, hour, monthDay, month, year);
//            holder.mTaskListAdapterBinding.lnrTimer.setVisibility(View.VISIBLE);
//            holder.mTaskListAdapterBinding.txtChronometer.start();
//            holder.mTaskListAdapterBinding.txtChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
//                @Override
//                public void onChronometerTick(Chronometer chronometer) {
//                    String after15Mins = AppUtils.compareDates(currentdate, sDate);
//                    if (after15Mins.equals("after15date")) {
//                        holder.mTaskListAdapterBinding.lnrTimer.setVisibility(View.GONE);
//                        holder.mTaskListAdapterBinding.imgWarning.setVisibility(View.VISIBLE);
//                    } else {
//                        holder.mTaskListAdapterBinding.lnrTimer.setVisibility(View.VISIBLE);
//                        holder.mTaskListAdapterBinding.imgWarning.setVisibility(View.GONE);
//                    }
////                    if (chronometer.getText().toString().equalsIgnoreCase("15:00")) {
////                        holder.mTaskListAdapterBinding.lnrTimer.setVisibility(View.GONE);
////                        holder.mTaskListAdapterBinding.imgWarning.setVisibility(View.VISIBLE);
////                    }
//                }
//            });
//
//        } else if(isStartDate.equals("afterdate")){
//            holder.mTaskListAdapterBinding.lnrTimer.setVisibility(View.GONE);
//        }

        if (items.get(position).getStatus().equals("Dispatched")) {

            if (isStartDate.equals("afterdate")) {
                conferenceTime.set(second, minute, hour, monthDay, month, year);
                holder.mTaskListAdapterBinding.lnrTimer.setVisibility(View.GONE);
                holder.mTaskListAdapterBinding.imgWarning.setVisibility(View.VISIBLE);
                notifyDataSetChanged();
            }
//            else if (isStartDate.equals("after15date")) {
//                conferenceTime.set(second, minute, hour, monthDay, month, year);
//                holder.mTaskListAdapterBinding.lnrTimer.setVisibility(View.GONE);
//                holder.mTaskListAdapterBinding.imgWarning.setVisibility(View.VISIBLE);
//            }
            else {
                holder.mTaskListAdapterBinding.lnrTimer.setVisibility(View.GONE);
                holder.mTaskListAdapterBinding.imgWarning.setVisibility(View.GONE);
                conferenceTime.set(second, minute, hour, monthDay, month, year);
                notifyDataSetChanged();
            }
        }


        conferenceTime.normalize(true);
        final long confMillis = conferenceTime.toMillis(true);
        Time nowTime = new Time(Time.getCurrentTimezone());
        nowTime.setToNow();
        nowTime.normalize(true);
        long nowMillis = nowTime.toMillis(true);
        long milliDiff = nowMillis - confMillis;
        milli = milliDiff;
        long millis = 900000 - milliDiff;
        timeSwapBuff += timeInMilliseconds;

//        CustomTimerTask myTask = new CustomTimerTask(holder);
//        Timer myTimer = new Timer();
//        myTimer.schedule(myTask, millis, 1000);

//        customHandler.removeCallbacks(updateTimerThread);

//        MyCountDownTimer timer = new MyCountDownTimer(millis) {
//            public void onTick(String Hours, String Minutes, String Seconds) {
//                holder.mTaskListAdapterBinding.txtTimer.setText(Hours + ":" + Minutes + ":" + Seconds);
//                String isStartDate = AppUtils.compareDates(AppUtils.currentDateTime(), sDate);
//                if (isStartDate.equals("after15date")) {
//                    holder.mTaskListAdapterBinding.imgWarning.setVisibility(View.VISIBLE);
//                    holder.mTaskListAdapterBinding.lnrTimer.setVisibility(View.GONE);
//                } else if (isStartDate.equals("afterdate")) {
//                    holder.mTaskListAdapterBinding.imgWarning.setVisibility(View.GONE);
//                    holder.mTaskListAdapterBinding.lnrTimer.setVisibility(View.GONE);
//                } else {
//                    holder.mTaskListAdapterBinding.lnrTimer.setVisibility(View.GONE);
//                    holder.mTaskListAdapterBinding.imgWarning.setVisibility(View.GONE);
//                }
//            }
//        };
//
//        timer.start();


//        new CountDownTimer(-milliDiff, 1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//
//                int days = (int) ((millisUntilFinished / 1000) / 86400);
//                int hours = (int) (((millisUntilFinished / 1000) - (days * 86400)) / 3600);
//                int minutes = (int) (((millisUntilFinished / 1000) - ((days * 86400) + (hours * 3600))) / 60);
//                int seconds = (int) ((millisUntilFinished / 1000) % 60);
//                Animation an = new RotateAnimation(0.0f, 90.0f, 250f, 273f);
//                an.setInterpolator(new LinearInterpolator());
//                an.setRepeatCount(Animation.INFINITE); //repeating indefinitely
//                an.setRepeatMode(Animation.INFINITE); //animation will start from end point once ended.
//                an.setFillAfter(true);
//
//                String Days = String.format("%02d", days);
//                String Hours = String.format("%02d", hours);
//                String Minutes = String.format("%02d", minutes);
//                String Seconds = String.format("%02d", seconds);
//
//                holder.mTaskListAdapterBinding.txtTimer.setText(Hours + ":" + Minutes + ":" + Seconds);
//                conferenceTime.set(second, minute, hour, monthDay, month, year);
//                String isStartDate = AppUtils.compareDates(currentdate, sDate);
//
//                if(isStartDate.equals("after15date")){
//                    holder.mTaskListAdapterBinding.imgWarning.setVisibility(View.VISIBLE);
//                    holder.mTaskListAdapterBinding.lnrTimer.setVisibility(View.GONE);
//                }
//
//            }
//
//            @Override
//            public void onFinish() {
//            }
//
//        }.start();

    }


    public void getErrorDialog(SpannableStringBuilder title, String message) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).create().show();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("error", e.getMessage());
        }
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
            if (!isShown) {
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
