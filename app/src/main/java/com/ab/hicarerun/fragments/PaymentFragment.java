package com.ab.hicarerun.fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.hicarerun.BaseFragment;
import com.ab.hicarerun.R;
import com.ab.hicarerun.adapter.BankSearchAdapter;
import com.ab.hicarerun.databinding.FragmentPaymentBinding;
import com.ab.hicarerun.handler.OnSaveEventHandler;
import com.ab.hicarerun.handler.UserPaymentClickHandler;
import com.ab.hicarerun.network.NetworkCallController;
import com.ab.hicarerun.network.NetworkResponseListner;
import com.ab.hicarerun.network.models.FeedbackModel.FeedbackRequest;
import com.ab.hicarerun.network.models.FeedbackModel.FeedbackResponse;
import com.ab.hicarerun.network.models.GeneralModel.GeneralData;
import com.ab.hicarerun.network.models.GeneralModel.GeneralPaymentMode;
import com.ab.hicarerun.network.models.PayementModel.PaymentLinkRequest;
import com.ab.hicarerun.network.models.PayementModel.PaymentLinkResponse;
import com.ab.hicarerun.utils.AppUtils;
import com.ab.hicarerun.utils.MyDividerItemDecoration;
import com.bumptech.glide.Glide;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.dmoral.toasty.Toasty;
import io.realm.RealmResults;

import static android.view.View.GONE;

/**
 * A simple {@link Fragment} subclass.
 */
public class PaymentFragment extends BaseFragment implements UserPaymentClickHandler, FragmentDatePicker.onDatePickerListener {

    FragmentPaymentBinding mFragmentPaymentBinding;
    private RealmResults<GeneralPaymentMode> generalPaymentRealmModel;
    private String mode = "";
    private boolean isTrue = false;
    private boolean isChequeRequired = false;
    private ArrayList<String> images = new ArrayList<>();
    private int amounttocollect = 0;
    private int mYear, mMonth, mDay;
    private String AmountCollected = "";
    private ArrayList<String> type = null;
    private RealmResults<GeneralData> mGeneralRealmData = null;

    private OnSaveEventHandler mCallback;

    private String[] bankNames;
    List<String> bankList;
    private BankSearchAdapter mAdapter;
    private AlertDialog alertDialog;
    private String[] arrayMode = null;
    private int finalAmount = 0;
    private String finalChequeNo = "";
    private String selectedImagePath = "";
    private Bitmap bitmap;
    private String chequeImg = "";
    private String Email = "";
    private String mask = "";
    private static final int POST_PAYMENT_LINK = 1000;
    private static final String ARG_TASK = "ARG_TASK";
    private String taskId = "";
    private String mobile = "";
    private String sta = "";


    public PaymentFragment() {
        // Required empty public constructor
    }

    public static PaymentFragment newInstance(String taskId) {
        Bundle args = new Bundle();
        args.putString(ARG_TASK, taskId);
        PaymentFragment fragment = new PaymentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            taskId = getArguments().getString(ARG_TASK);
        }

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
    public void onResume() {
        super.onResume();
        AppUtils.statusCheck(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentPaymentBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_payment, container, false);
        bankNames = getResources().getStringArray(R.array.bank_name);
        bankList = new ArrayList<String>(Arrays.asList(bankNames));
        mFragmentPaymentBinding.setHandler(this);
        return mFragmentPaymentBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFragmentPaymentBinding.spnPtmmode.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        getPaymentData();

        mFragmentPaymentBinding.txtCollected.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mCallback.amountCollected(s.toString());
                getValidated(amounttocollect);
            }
        });

        mFragmentPaymentBinding.txtChequeNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                getValidated(amounttocollect);
                mCallback.chequeNumber(s.toString());
            }
        });
    }

    private void getPaymentData() {
        mGeneralRealmData =
                getRealm().where(GeneralData.class).findAll();


        if (mGeneralRealmData != null && mGeneralRealmData.size() > 0) {
            mobile = mGeneralRealmData.get(0).getMobileNumber();
            Email = mGeneralRealmData.get(0).getEmail();
            try {
                mask = mobile.replaceAll("\\w(?=\\w{4})", "*");
            } catch (Exception e) {
                e.printStackTrace();
            }

            chequeImg = mGeneralRealmData.get(0).getChequeImageUrl();
            AmountCollected = mGeneralRealmData.get(0).getAmountCollected();

            amounttocollect = Integer.parseInt(mGeneralRealmData.get(0).getAmountToCollect());

            mFragmentPaymentBinding.txtCollect.setText(amounttocollect + " " + "\u20B9");

            generalPaymentRealmModel = getRealm().where(GeneralPaymentMode.class).findAll().sort("Value");

            type = new ArrayList<>();
            type.clear();
            for (GeneralPaymentMode generalPaymentMode : generalPaymentRealmModel) {
                type.add(generalPaymentMode.getValue());
            }

            isTrue = mGeneralRealmData.get(0).getPaymentValidation();
            isChequeRequired = mGeneralRealmData.get(0).getChequeRequired();
            type.add(0, "None");
            Log.i("type", String.valueOf(type.size()));

            arrayMode = new String[type.size()];
            arrayMode = type.toArray(arrayMode);
            Log.i("payment", Arrays.toString(arrayMode));
            final String status = mGeneralRealmData.get(0).getSchedulingStatus();
            sta = mGeneralRealmData.get(0).getSchedulingStatus();


            try {
                if (mGeneralRealmData.get(0).getChequeImageUrl() != null && mGeneralRealmData.get(0).getChequeImageUrl().length() != 0) {
                    mFragmentPaymentBinding.lnrUpload.setVisibility(View.GONE);
                    Glide.with(getActivity())
                            .load(mGeneralRealmData.get(0).getChequeImageUrl())
                            .error(android.R.drawable.stat_notify_error)
                            .into(mFragmentPaymentBinding.imgUploadCheque);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (amounttocollect == 0) {
                mFragmentPaymentBinding.txtCollected.setEnabled(false);
                type.clear();
                type.add(0, "None");
                arrayMode = new String[type.size()];
                arrayMode = type.toArray(arrayMode);
            } else if (status.equals("Completed")) {
                try {
                    mFragmentPaymentBinding.txtCollected.setEnabled(false);
                    mFragmentPaymentBinding.lnrBank.setEnabled(false);
                    mFragmentPaymentBinding.lnrDate.setEnabled(false);
                    mFragmentPaymentBinding.txtChequeNo.setEnabled(false);
                    mFragmentPaymentBinding.txtCollected.setText(AmountCollected);
                    mFragmentPaymentBinding.txtChequeNo.setText(mGeneralRealmData.get(0).getChequeNo());
                    mFragmentPaymentBinding.txtBankname.setText(mGeneralRealmData.get(0).getBankName());
                    if (mGeneralRealmData.get(0).getChequeDate() != null) {
                        try {
                            mFragmentPaymentBinding.txtDate.setText(AppUtils.reFormatDateTime(mGeneralRealmData.get(0).getChequeDate(), "dd-MMM-yyyy"));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    type.clear();
                    type.add(0, mGeneralRealmData.get(0).getPaymentMode());
                    arrayMode = new String[type.size()];
                    arrayMode = type.toArray(arrayMode);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (status.equals("Incomplete")) {
                mFragmentPaymentBinding.txtCollected.setEnabled(false);
                mFragmentPaymentBinding.lnrBank.setEnabled(false);
                mFragmentPaymentBinding.lnrDate.setEnabled(false);
                mFragmentPaymentBinding.txtChequeNo.setEnabled(false);
                type.clear();
                type.add(0, "None");
                arrayMode = new String[type.size()];
                arrayMode = type.toArray(arrayMode);
            }

//            if (amounttocollect == 0) {
//                mFragmentPaymentBinding.txtCollected.setEnabled(false);
//                type.clear();
//                type.add(0, "None");
//                arrayMode = new String[type.size()];
//                arrayMode = type.toArray(arrayMode);
//            } else if (status.equals("Completed")) {
//                mFragmentPaymentBinding.txtCollected.setEnabled(false);
//                mFragmentPaymentBinding.lnrBank.setEnabled(false);
//                mFragmentPaymentBinding.lnrDate.setEnabled(false);
//                mFragmentPaymentBinding.txtChequeNo.setEnabled(false);
//                mFragmentPaymentBinding.txtCollected.setText(AmountCollected);
//                mFragmentPaymentBinding.txtChequeNo.setText(mGeneralRealmData.get(0).getChequeNo());
//                mFragmentPaymentBinding.txtBankname.setText(mGeneralRealmData.get(0).getBankName());
//                if(mFragmentPaymentBinding.txtDate.getText().length() != 0 && !mFragmentPaymentBinding.txtDate.getText().toString().equals("Select Date")){
//                    try {
//                        mFragmentPaymentBinding.txtDate.setText(AppUtils.reFormatDateTime(mGeneralRealmData.get(0).getChequeDate(), "dd-MMM-yyyy"));
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                type.clear();
//                type.add(0, mGeneralRealmData.get(0).getPaymentMode());
//                arrayMode = new String[type.size()];
//                arrayMode = type.toArray(arrayMode);
//            } else if (status.equals("Incomplete")) {
//                mFragmentPaymentBinding.txtCollected.setEnabled(false);
//                mFragmentPaymentBinding.lnrBank.setEnabled(false);
//                mFragmentPaymentBinding.lnrDate.setEnabled(false);
//                mFragmentPaymentBinding.txtChequeNo.setEnabled(false);
//                mFragmentPaymentBinding.txtCollected.setText(AmountCollected);
//                mFragmentPaymentBinding.txtChequeNo.setText(mGeneralRealmData.get(0).getChequeNo());
//                mFragmentPaymentBinding.txtBankname.setText(mGeneralRealmData.get(0).getBankName());
//                mFragmentPaymentBinding.txtDate.setText(mGeneralRealmData.get(0).getChequeDate());
//                type.clear();
//                type.add(0, "None");
//                arrayMode = new String[type.size()];
//                arrayMode = type.toArray(arrayMode);
//            } else if (status.equals("Dispatched")) {
//                mFragmentPaymentBinding.txtCollected.setBackgroundResource(R.drawable.disable_edit_borders);
//                mFragmentPaymentBinding.txtCollected.setEnabled(false);
//                mFragmentPaymentBinding.lnrBank.setEnabled(false);
//                mFragmentPaymentBinding.lnrDate.setEnabled(false);
//                mFragmentPaymentBinding.txtChequeNo.setEnabled(false);
//                mFragmentPaymentBinding.txtCollected.setText(AmountCollected);
//                mFragmentPaymentBinding.txtChequeNo.setText(mGeneralRealmData.get(0).getChequeNo());
//                mFragmentPaymentBinding.txtBankname.setText(mGeneralRealmData.get(0).getBankName());
//                mFragmentPaymentBinding.txtDate.setText(mGeneralRealmData.get(0).getChequeDate());
//                type.clear();
//                type.add(0, "None");
//                arrayMode = new String[type.size()];
//                arrayMode = type.toArray(arrayMode);
//            }
//            else {
//                mFragmentPaymentBinding.txtCollected.setEnabled(true);
//                type.clear();
//                type.add(0, "None");
//                arrayMode = new String[type.size()];
//                arrayMode = type.toArray(arrayMode);
//            }
            ArrayAdapter<String> statusAdapter = new ArrayAdapter<String>(getActivity(),
                    R.layout.spinner_layout, arrayMode);
            statusAdapter.setDropDownViewResource(R.layout.spinner_popup);
            mFragmentPaymentBinding.spnPtmmode.setAdapter(statusAdapter);

            mFragmentPaymentBinding.spnPtmmode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        mFragmentPaymentBinding.btnSendlink.setVisibility(GONE);
                        mode = mFragmentPaymentBinding.spnPtmmode.getSelectedItem().toString();
                        if (mode.equals("None")) {
                            mCallback.mode("");
                        } else {
                            mCallback.mode(mode);
                        }
                        mCallback.amountToCollect(String.valueOf(amounttocollect));

                        if (mFragmentPaymentBinding.spnPtmmode.getSelectedItem().toString().equals("Cheque")) {
                            mFragmentPaymentBinding.lnrCheque.setVisibility(View.VISIBLE);
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                            imm.showSoftInput(mFragmentPaymentBinding.txtCollected, InputMethodManager.SHOW_IMPLICIT);
                            mFragmentPaymentBinding.txtCollected.requestFocus();
                            getValidated(amounttocollect);

                        } else {
                            mFragmentPaymentBinding.lnrCheque.setVisibility(View.GONE);
                        }

                        if (mFragmentPaymentBinding.spnPtmmode.getSelectedItem().toString().equalsIgnoreCase("Online Payment Link")) {
                            if (sta.equals("On-Site")) {
                                sendPaymentLink();
                            }
                        }

                        if (mFragmentPaymentBinding.spnPtmmode.getSelectedItem().toString().equals("PayTm")) {
                            mFragmentPaymentBinding.cardScanner.setVisibility(View.VISIBLE);
                            getValidated(amounttocollect);
                            Glide.with(getActivity()).load("http://52.74.65.15/MobileApi/images/PayTm.png").into(mFragmentPaymentBinding.imgPayscanner);
                        } else if (mFragmentPaymentBinding.spnPtmmode.getSelectedItem().toString().equals("Google Pay")) {
                            mFragmentPaymentBinding.cardScanner.setVisibility(View.VISIBLE);
                            getValidated(amounttocollect);
                            Glide.with(getActivity()).load("http://52.74.65.15/MobileApi/images/gpay.png").into(mFragmentPaymentBinding.imgPayscanner);
                        } else if (mFragmentPaymentBinding.spnPtmmode.getSelectedItem().toString().equals("PhonePe")) {
                            mFragmentPaymentBinding.cardScanner.setVisibility(View.VISIBLE);
                            getValidated(amounttocollect);
                            Glide.with(getActivity()).load("http://52.74.65.15/MobileApi/images/PhonePay.png").into(mFragmentPaymentBinding.imgPayscanner);
                        } else {
                            mFragmentPaymentBinding.cardScanner.setVisibility(View.GONE);
                        }


                        if (mFragmentPaymentBinding.spnPtmmode.getSelectedItem().toString().equals("None")) {
                            getValidated(amounttocollect);
                            mFragmentPaymentBinding.txtCollected.setEnabled(false);
                        }

                        if (mFragmentPaymentBinding.spnPtmmode.getSelectedItem().toString().equals("Cash")) {
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                            imm.showSoftInput(mFragmentPaymentBinding.txtCollected, InputMethodManager.SHOW_IMPLICIT);
                            mFragmentPaymentBinding.txtCollected.requestFocus();
                            getValidated(amounttocollect);
                        }
                    } catch (Exception e) {
                        String lineNo = String.valueOf(new Exception().getStackTrace()[0].getLineNumber());
                        AppUtils.sendErrorLogs(getActivity(), e.getMessage(), getActivity().getClass().getSimpleName(), "spinnerPaymentMode", lineNo);
                    }


                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }
    }

    private void sendPaymentLink() {
        LayoutInflater li = LayoutInflater.from(getActivity());

        View promptsView = li.inflate(R.layout.payment_link_dialog, null);

        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(getActivity());

        alertDialogBuilder.setView(promptsView);

        alertDialogBuilder.setTitle("Payment Link");

        // create alert dialog
        final android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();

        final AppCompatEditText edtmobile =
                (AppCompatEditText) promptsView.findViewById(R.id.edtmobile);
        final AppCompatEditText edtemail =
                (AppCompatEditText) promptsView.findViewById(R.id.edtemail);
        final AppCompatButton btn_send =
                (AppCompatButton) promptsView.findViewById(R.id.btn_send);
        final AppCompatButton btn_cancel =
                (AppCompatButton) promptsView.findViewById(R.id.btn_cancel);
        edtemail.setEnabled(false);
        edtmobile.setEnabled(false);

        try {
            edtmobile.setText(mask);
            edtemail.setText(Email);
        } catch (Exception e) {
            e.printStackTrace();
        }


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGeneralRealmData =
                        getRealm().where(GeneralData.class).findAll();

                if (mGeneralRealmData != null && mGeneralRealmData.size() > 0) {

                    PaymentLinkRequest request = new PaymentLinkRequest();
                    request.setAmount(mGeneralRealmData.get(0).getAmountToCollect());
                    request.setCustomerName(mGeneralRealmData.get(0).getCustName());
                    request.setEmail(mGeneralRealmData.get(0).getEmail());
                    request.setMobileNo(mGeneralRealmData.get(0).getMobileNumber());
                    request.setOrderNo(mGeneralRealmData.get(0).getOrderNumber());
                    request.setTaskId(taskId);

                    NetworkCallController controller = new NetworkCallController();
                    controller.setListner(new NetworkResponseListner() {
                        @Override
                        public void onResponse(int requestCode, Object response) {
                            PaymentLinkResponse refResponse = (PaymentLinkResponse) response;
                            if (refResponse.getIsSuccess()) {
                                Toasty.success(getActivity(), "Payment link sent successfully", Toasty.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(int requestCode) {

                        }
                    });
                    controller.sendPaymentLink(POST_PAYMENT_LINK, request);
                }

                alertDialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mode.equalsIgnoreCase("Online Payment Link")) {
                    alertDialog.dismiss();
                    mFragmentPaymentBinding.btnSendlink.setVisibility(View.VISIBLE);
                } else {
                    mFragmentPaymentBinding.btnSendlink.setVisibility(GONE);
                }
            }
        });
        alertDialog.setCancelable(false);
        alertDialog.setIcon(R.mipmap.logo);
        alertDialog.show();
    }

    @Override
    public void onCalendarClicked(View view) {
        if (sta.equals("On-Site")) {
            showDatePicker();
        }
    }

    @Override
    public void onBankNameClicked(View view) {
        if (sta.equals("On-Site")) {
            showBankDialog();
        }
    }

    @Override
    public void onUploadChequeClicked(View view) {
        if (images.size() < 1) {
            PickImageDialog.build(new PickSetup()).setOnPickResult(new IPickResult() {
                @Override
                public void onPickResult(PickResult pickResult) {
                    try {
                        if (pickResult.getError() == null) {
                            images.add(pickResult.getPath());
                            mFragmentPaymentBinding.lnrUpload.setVisibility(View.VISIBLE);

                            Bitmap myBitmap = BitmapFactory.decodeFile(pickResult.getPath());
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                            mFragmentPaymentBinding.imgUploadCheque.setImageBitmap(myBitmap);
                            selectedImagePath = pickResult.getPath();
                            if (selectedImagePath != null) {
                                Bitmap bit = new BitmapDrawable(getActivity().getResources(),
                                        selectedImagePath).getBitmap();
                                int i = (int) (bit.getHeight() * (512.0 / bit.getWidth()));
                                bitmap = Bitmap.createScaledBitmap(bit, 512, i, true);
                            }
                            mFragmentPaymentBinding.imgUploadCheque.setImageBitmap(bitmap);

                            if (mFragmentPaymentBinding.imgUploadCheque.getDrawable() != null) {
                                mFragmentPaymentBinding.lnrUpload.setVisibility(View.GONE);
                            }

                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] b = baos.toByteArray();
                            String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                            if (images.size() == 0) {
                                mCallback.isPaymentChanged(true);
                            } else {
                                mCallback.isPaymentChanged(false);
                                mCallback.chequeImage(encodedImage);
                                getValidated(amounttocollect);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }).show(getActivity());
        } else {
            Toast.makeText(getContext(), "You have already selected an Image", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSendPaymentLinkClicked(View view) {
        sendPaymentLink();
    }

    private void showDatePicker() {
        FragmentDatePicker mFragDatePicker = new FragmentDatePicker();
        mFragDatePicker.setmDatePickerListener(this);
        mFragDatePicker.show(getActivity().getSupportFragmentManager(), "datepicker");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        month++;
        mFragmentPaymentBinding.txtDate.setText("" + day + "-" + month + "-" + year);
        String date = mFragmentPaymentBinding.txtDate.getText().toString();
        mCallback.chequeDate("" + month + "-" + day + "-" + year);
        mGeneralRealmData =
                getRealm().where(GeneralData.class).findAll();


        if (mGeneralRealmData != null && mGeneralRealmData.size() > 0) {
            try {
                String amountToCollect = mGeneralRealmData.get(0).getAmountToCollect();
                int amount = Integer.parseInt(amountToCollect);
                getValidated(amount);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }


    private void showBankDialog() {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.bank_custom_dialog, null);
        dialogBuilder.setView(dialogView);

        final RecyclerView recycle = (RecyclerView) dialogView.findViewById(R.id.recycle);
        final TextView txt_close = (TextView) dialogView.findViewById(R.id.txt_close);
        final SearchView search = (SearchView) dialogView.findViewById(R.id.search);

        RecyclerView.LayoutManager lm = new LinearLayoutManager(getActivity());
        recycle.setLayoutManager(lm);
        recycle.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new BankSearchAdapter(getActivity(), bankList);
        recycle.setAdapter(mAdapter);


        recycle.addItemDecoration(new MyDividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL, 10));

        mAdapter.onBankSelected(new BankSearchAdapter.BankAdapterListener() {
            @Override
            public void onSelected(String item, int position) {
                mFragmentPaymentBinding.txtBankname.setText(item);
                mCallback.bankName(item);
                mGeneralRealmData =
                        getRealm().where(GeneralData.class).findAll();


                if (mGeneralRealmData != null && mGeneralRealmData.size() > 0) {
                    try {
                        String amountToCollect = mGeneralRealmData.get(0).getAmountToCollect();
                        int amount = Integer.parseInt(amountToCollect);
                        getValidated(amount);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                alertDialog.dismiss();
            }

        });
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                mAdapter.getFilter().filter(query);
                return false;
            }
        });


        txt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        dialogBuilder.setCancelable(false);
        alertDialog = dialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        alertDialog = dialogBuilder.create();
        alertDialog.show();

    }


    private void getValidated(int amounttocollect) {
        if (isTrue) {
            if (amounttocollect > 0) {
                mFragmentPaymentBinding.txtCollected.setEnabled(true);
                if (mFragmentPaymentBinding.txtCollected.getText().toString().trim().length() == 0) {
                    mCallback.isPaymentChanged(true);
                    mCallback.isAmountCollectedRequired(true);
                } else {
                    if (mFragmentPaymentBinding.txtCollected.getText().toString().trim().length() != 0) {
                        int amount = 0;
                        Log.i("amount", String.valueOf(amount));
                        try {
                            amount = Integer.parseInt(mFragmentPaymentBinding.txtCollected.getText().toString());
                            if (amounttocollect == amount) {
                                mCallback.isPaymentChanged(false);
                                mCallback.isACEquals(false);
                            } else {
                                mCallback.isPaymentChanged(true);
                                mCallback.isACEquals(true);
                            }

                        } catch (Exception e) {
                            e.getMessage();
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        mCallback.isPaymentChanged(true);
                        mCallback.isAmountCollectedRequired(true);
                    }

                }
            } else {
                mFragmentPaymentBinding.txtCollected.setEnabled(false);
            }

        } else {
            if (amounttocollect > 0) {
                mFragmentPaymentBinding.txtCollected.setEnabled(true);
                if (mFragmentPaymentBinding.txtCollected.getText().toString().trim().length() == 0) {
                    mCallback.isPaymentChanged(true);
                    mCallback.isAmountCollectedRequired(true);
                } else {
                    mCallback.isPaymentChanged(false);
                    mCallback.isAmountCollectedRequired(false);

                }
            } else {
                mFragmentPaymentBinding.txtCollected.setEnabled(false);
                mCallback.isPaymentChanged(false);
                mCallback.isAmountCollectedRequired(false);
            }
        }


        if (mFragmentPaymentBinding.spnPtmmode.getSelectedItem().toString().equals("Cheque")) {
            mFragmentPaymentBinding.lnrCheque.setVisibility(View.VISIBLE);
            if (isChequeRequired) {
                mFragmentPaymentBinding.lnrUploadChq.setVisibility(View.VISIBLE);
                if (mFragmentPaymentBinding.txtChequeNo.getText().length() == 6) {
                    mCallback.isInvalidChequeNumber(false);
                    if (images.size() == 0) {
                        mCallback.isPaymentChanged(true);
                        mCallback.isChequeImageRequired(true);
                    } else {
                        mCallback.isChequeImageRequired(false);
                        if (mFragmentPaymentBinding.txtCollected.getText().toString().trim().length() != 0) {
                            int amount = 0;
                            Log.i("amount", String.valueOf(amount));
                            try {
                                amount = Integer.parseInt(mFragmentPaymentBinding.txtCollected.getText().toString());
                                if (amounttocollect == amount) {
                                    mCallback.isPaymentChanged(false);
                                    mCallback.isAmountCollectedRequired(false);
                                } else {
                                    mCallback.isPaymentChanged(true);
                                    mCallback.isAmountCollectedRequired(true);
                                }

                            } catch (Exception e) {
                                e.getMessage();
                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            mCallback.isPaymentChanged(true);
                            mCallback.isAmountCollectedRequired(true);
                        }

                    }
                } else {
                    mCallback.isPaymentChanged(true);
                    mCallback.isChequeNumberRequired(true);
                }
            } else {
                mFragmentPaymentBinding.lnrUploadChq.setVisibility(View.GONE);
            }
            if (mFragmentPaymentBinding.txtChequeNo.getText().toString().length() == 0) {
                mCallback.isChequeNumberRequired(true);
            } else {
                mCallback.isChequeNumberRequired(false);
            }

            if (mFragmentPaymentBinding.txtChequeNo.getText().toString().length() < 6) {
                mCallback.isInvalidChequeNumber(true);
            } else {
                mCallback.isInvalidChequeNumber(false);
            }

            if (mFragmentPaymentBinding.txtBankname.getText().equals("Select Bank")) {
                mCallback.isBankNameRequired(true);
            } else {
                mCallback.isBankNameRequired(false);
            }

            if (mFragmentPaymentBinding.txtDate.getText().equals("Select Date")) {
                mCallback.isChequeDateRequired(true);
            } else {
                mCallback.isChequeDateRequired(false);
            }

        } else {
            mCallback.isBankNameRequired(false);
            mCallback.isChequeDateRequired(false);
            mCallback.isInvalidChequeNumber(false);
            mCallback.isChequeNumberRequired(false);

        }
        if (sta != null) {
            if (sta.equals("Completed") || sta.equals("Incompleted")) {
                mFragmentPaymentBinding.txtCollected.setEnabled(false);
                mFragmentPaymentBinding.lnrBank.setEnabled(false);
                mFragmentPaymentBinding.lnrDate.setEnabled(false);
                mFragmentPaymentBinding.txtChequeNo.setEnabled(false);
            }
        }
    }

}
