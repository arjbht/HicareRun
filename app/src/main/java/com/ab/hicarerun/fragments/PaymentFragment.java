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
import com.ab.hicarerun.network.models.GeneralModel.GeneralData;
import com.ab.hicarerun.network.models.GeneralModel.GeneralPaymentMode;
import com.ab.hicarerun.utils.MyDividerItemDecoration;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.realm.RealmResults;

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


    public PaymentFragment() {
        // Required empty public constructor
    }

    public static PaymentFragment newInstance() {
        Bundle args = new Bundle();
        PaymentFragment fragment = new PaymentFragment();
        fragment.setArguments(args);
        return fragment;
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
            }
        });
    }

    private void getPaymentData() {
        final RealmResults<GeneralData> mGeneralRealmData =
                getRealm().where(GeneralData.class).findAll();


        if (mGeneralRealmData != null && mGeneralRealmData.size() > 0) {

            amounttocollect = Integer.parseInt(mGeneralRealmData.get(0).getAmountToCollect());

            mFragmentPaymentBinding.txtCollect.setText(String.valueOf(amounttocollect) + " " + "\u20B9");

            generalPaymentRealmModel = getRealm().where(GeneralPaymentMode.class).findAll().sort("Value");

            final ArrayList<String> type = new ArrayList<>();
            for (GeneralPaymentMode generalPaymentMode : generalPaymentRealmModel) {
                type.add(generalPaymentMode.getValue());
            }
            isTrue = mGeneralRealmData.get(0).getPaymentValidation();
            isChequeRequired = mGeneralRealmData.get(0).getChequeRequired();
            type.add(0, "None");
            arrayMode = new String[type.size()];
            arrayMode = type.toArray(arrayMode);
            String paymentmode = mGeneralRealmData.get(0).getSchedulingStatus();

            if (amounttocollect == 0) {
                mFragmentPaymentBinding.txtCollected.setEnabled(false);
                type.clear();
                type.add(0, "None");
                arrayMode = new String[type.size()];
                arrayMode = type.toArray(arrayMode);
            } else if (paymentmode.equals("Completed") || paymentmode.equals("Incomplete")) {
                mFragmentPaymentBinding.txtCollected.setEnabled(false);
                type.clear();
                type.add(0, "None");
                arrayMode = new String[type.size()];
                arrayMode = type.toArray(arrayMode);
            } else {
                mFragmentPaymentBinding.txtCollected.setEnabled(true);
            }
            ArrayAdapter<String> statusAdapter = new ArrayAdapter<String>(getActivity(),
                    R.layout.spinner_layout, arrayMode);
            statusAdapter.setDropDownViewResource(R.layout.spinner_popup);
            mFragmentPaymentBinding.spnPtmmode.setAdapter(statusAdapter);

            mFragmentPaymentBinding.spnPtmmode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    mode = mFragmentPaymentBinding.spnPtmmode.getSelectedItem().toString();
                    mCallback.mode(mode);
                    mCallback.amountCollected(mFragmentPaymentBinding.txtCollected.getText().toString());
                    mCallback.amountToCollect(String.valueOf(amounttocollect));


                    if (mFragmentPaymentBinding.spnPtmmode.getSelectedItem().toString().equals("Cheque")) {
                        mFragmentPaymentBinding.lnrCheque.setVisibility(View.VISIBLE);
                        getValidated(amounttocollect);

                    } else {
                        mFragmentPaymentBinding.lnrCheque.setVisibility(View.GONE);
                    }


                    if (mFragmentPaymentBinding.spnPtmmode.getSelectedItem().toString().equals("None")) {
                        getValidated(amounttocollect);
                        mFragmentPaymentBinding.txtCollected.setEnabled(false);
                    }

                    if (mFragmentPaymentBinding.spnPtmmode.getSelectedItem().toString().equals("Cash")) {
                        getValidated(amounttocollect);
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }
    }

    @Override
    public void onCalendarClicked(View view) {
        showDatePicker();
    }

    @Override
    public void onBankNameClicked(View view) {
        showBankDialog();
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

    private void showDatePicker() {
        FragmentDatePicker mFragDatePicker = new FragmentDatePicker();
        mFragDatePicker.setmDatePickerListener(this);
        mFragDatePicker.show(getActivity().getSupportFragmentManager(), "datepicker");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        month++;
        mFragmentPaymentBinding.txtDate.setText("" + day + "-" + month + "-" + year);
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
                } else {
                    if (mFragmentPaymentBinding.txtCollected.getText().toString().trim().length() != 0) {
                        int amount = 0;
                        Log.i("amount", String.valueOf(amount));
                        try {
                            amount = Integer.parseInt(mFragmentPaymentBinding.txtCollected.getText().toString());
                            if (amounttocollect == amount) {
                                mCallback.isPaymentChanged(false);
                            } else {
                                mCallback.isPaymentChanged(true);
                            }

                        } catch (Exception e) {
                            e.getMessage();
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        mCallback.isPaymentChanged(true);
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
                } else {
                    mCallback.isPaymentChanged(false);

                }
            } else {
                mFragmentPaymentBinding.txtCollected.setEnabled(false);
                mCallback.isPaymentChanged(false);
            }
//            mFragmentPaymentBinding.txtCollected.setEnabled(true);
//            if (mFragmentPaymentBinding.txtCollected.getText().toString().trim().length() == 0) {
//                mCallback.isPaymentChanged(true);
//            } else {
//                mCallback.isPaymentChanged(false);
//            }
        }


        if (mFragmentPaymentBinding.spnPtmmode.getSelectedItem().toString().equals("Cheque")) {
            mFragmentPaymentBinding.lnrCheque.setVisibility(View.VISIBLE);
            if (isChequeRequired) {
                mFragmentPaymentBinding.lnrUploadChq.setVisibility(View.VISIBLE);
                if (mFragmentPaymentBinding.txtChequeNo.getText().length() == 6) {
                    if (images.size() == 0) {
                        mCallback.isPaymentChanged(true);
                    } else {
                        mCallback.isPaymentChanged(false);
                    }
                } else {
                    mCallback.isPaymentChanged(true);
                }
            } else {
                mFragmentPaymentBinding.lnrUploadChq.setVisibility(View.GONE);
            }
        }
    }

}
