package com.ab.hicarerun.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

public class FragmentDatePicker extends DialogFragment
                            implements DatePickerDialog.OnDateSetListener {

    protected onDatePickerListener mDatePickerListener=null;

    public onDatePickerListener getmDatePickerListener() {
        return mDatePickerListener;
    }

    public void setmDatePickerListener(onDatePickerListener mDatePickerListener) {
        this.mDatePickerListener = mDatePickerListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c;
        int year = 0;
        int month = 0;
        int day = 0;


        c = Calendar.getInstance();

         year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

    Calendar mCMax  = Calendar.getInstance();
        mCMax.add(Calendar.DATE,1);
        Calendar mCMin  =
                Calendar.getInstance();
        mCMin .add(Calendar.DATE,-90);


        DatePickerDialog dialogFrag = new DatePickerDialog(getActivity(), this, year, month, day);
        dialogFrag.getDatePicker().setMinDate(mCMin.getTime().getTime());
        dialogFrag.getDatePicker().setMaxDate(mCMax.getTime().getTime());
        // Create a new instance of DatePickerDialog and return it
        return dialogFrag ;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {

        if(mDatePickerListener!=null){
            mDatePickerListener.onDateSet(view,year,month,day);
        }
        // Do something with the date chosen by the user
    }
    public interface onDatePickerListener {
        public void onDateSet(DatePicker view, int year, int month, int day);
    }
}