package com.ab.hicarerun.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ab.hicarerun.BaseApplication;
import com.ab.hicarerun.BaseFragment;
import com.ab.hicarerun.R;
import com.ab.hicarerun.activities.AttachmentActivity;
import com.ab.hicarerun.activities.HomeActivity;
import com.ab.hicarerun.activities.LoginActivity;
import com.ab.hicarerun.activities.TaskDetailsActivity;
import com.ab.hicarerun.adapter.AttachmentListAdapter;
import com.ab.hicarerun.adapter.ReferralListAdapter;
import com.ab.hicarerun.databinding.FragmentSignatureBinding;
import com.ab.hicarerun.handler.OnSaveEventHandler;
import com.ab.hicarerun.handler.UserSignatureClickHandler;
import com.ab.hicarerun.network.NetworkCallController;
import com.ab.hicarerun.network.NetworkResponseListner;
import com.ab.hicarerun.network.models.AttachmentModel.AttachmentDeleteRequest;
import com.ab.hicarerun.network.models.AttachmentModel.GetAttachmentList;
import com.ab.hicarerun.network.models.AttachmentModel.PostAttachmentResponse;
import com.ab.hicarerun.network.models.FeedbackModel.FeedbackRequest;
import com.ab.hicarerun.network.models.FeedbackModel.FeedbackResponse;
import com.ab.hicarerun.network.models.GeneralModel.GeneralData;
import com.ab.hicarerun.network.models.LoginResponse;
import com.ab.hicarerun.network.models.ReferralModel.ReferralList;
import com.ab.hicarerun.network.models.ReferralModel.ReferralRequest;
import com.ab.hicarerun.network.models.ReferralModel.ReferralResponse;
import com.ab.hicarerun.utils.SharedPreferencesUtility;
import com.ab.hicarerun.viewmodel.AttachmentListViewModel;
import com.bumptech.glide.Glide;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import net.igenius.customcheckbox.CustomCheckBox;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignatureFragment extends BaseFragment implements UserSignatureClickHandler {

    FragmentSignatureBinding mFragmentSignatureBinding;
    private static final int POST_FEEDBACK_LINK = 1000;
    private static final String ARG_TASK = "ARG_TASK";
    private static final String ARG_VAR = "ARG_VAR";

    private String status = "";
    private String state = "";
    static String mFeedback = "";
    static String mSignatory = "";
    static String mSignature = "";
    static String mActualProperty = "";
    static Boolean isCheck = false;
    private String path = "";
    private boolean jobCard = false;
    private int listSize = 0;
    private OnSaveEventHandler mCallback;
    private DrawingView dv;
    private Paint mPaint;
    private Bitmap bmp;
    private File file, mFile;
    private String Email = "", mobile = "", Order_Number = "", Service_Name = "", mask = "", UserId = "", name = "", code = "";
    private String taskId = "";
    private String actual_property = "", feedback_code = "", signatory = "", signature = "";
    private Boolean isJobcardEnable = false;
    private Boolean isFeedBack = false;
    private RealmResults<GeneralData> mGeneralRealmData = null;


    public SignatureFragment() {
    }

    public static SignatureFragment newInstance(String taskId) {
        Bundle args = new Bundle();
        args.putString(ARG_TASK, taskId);

        SignatureFragment fragment = new SignatureFragment();
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            taskId = getArguments().getString(ARG_TASK);
        }

    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("actual_property", mFragmentSignatureBinding.txtActualSize.getText().toString());
        outState.putString("feedback", mFragmentSignatureBinding.txtFeedback.getText().toString());
        outState.putString("signatory", mFragmentSignatureBinding.txtSignatory.getText().toString());
        outState.putString("signature", signature);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentSignatureBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_signature, container, false);
        actual_property = mFragmentSignatureBinding.txtActualSize.getText().toString();
        feedback_code = mFragmentSignatureBinding.txtFeedback.getText().toString();
        signatory = mFragmentSignatureBinding.txtSignatory.getText().toString();
        mFeedback = getArguments().getString(ARG_VAR);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(8);

        mFragmentSignatureBinding.checkProperty.setOnCheckedChangeListener(new CustomCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CustomCheckBox checkBox, boolean isChecked) {
                if (isChecked) {
                    mFragmentSignatureBinding.lnrActual.setVisibility(View.VISIBLE);

                } else {
                    mFragmentSignatureBinding.lnrActual.setVisibility(View.GONE);
                }
            }
        });
        mFragmentSignatureBinding.setHandler(this);
        return mFragmentSignatureBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getSignature();

        mFragmentSignatureBinding.txtFeedback.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                getValidate();
            }
        });

        mFragmentSignatureBinding.txtSignatory.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                getValidate();
            }
        });
    }

    private void getSignature() {
        mGeneralRealmData =
                getRealm().where(GeneralData.class).findAll();

        if (mGeneralRealmData != null && mGeneralRealmData.size() > 0) {
            status = mGeneralRealmData.get(0).getSchedulingStatus();
            if (status.equals("Completed")) {
                mFragmentSignatureBinding.txtSignatory.setEnabled(false);
                mFragmentSignatureBinding.txtHint.setVisibility(GONE);
                mFragmentSignatureBinding.btnSendlink.setVisibility(GONE);
                mFragmentSignatureBinding.btnUpload.setVisibility(GONE);
            } else if (status.equals("Incomplete")) {
                mFragmentSignatureBinding.txtSignatory.setEnabled(false);
                mFragmentSignatureBinding.imgSign.setEnabled(false);
                mFragmentSignatureBinding.txtHint.setVisibility(GONE);
                mFragmentSignatureBinding.btnSendlink.setVisibility(GONE);
                mFragmentSignatureBinding.btnUpload.setVisibility(GONE);
            } else {
                mFragmentSignatureBinding.txtSignatory.setEnabled(true);
                mFragmentSignatureBinding.imgSign.setEnabled(true);
                mFragmentSignatureBinding.txtHint.setVisibility(View.VISIBLE);
            }


            String amount = mGeneralRealmData.get(0).getAmountToCollect();
            mobile = mGeneralRealmData.get(0).getMobileNumber();
            Order_Number = mGeneralRealmData.get(0).getOrderNumber();
            Service_Name = mGeneralRealmData.get(0).getServicePlan();
            name = mGeneralRealmData.get(0).getCustName();
            code = mGeneralRealmData.get(0).getTechnicianOTP();
            try {
                if (mGeneralRealmData.get(0).getSignatureUrl() != null || !mGeneralRealmData.get(0).getSignatureUrl().equals("")) {
                    mFragmentSignatureBinding.txtHint.setVisibility(GONE);
                    Glide.with(getActivity())
                            .load(mGeneralRealmData.get(0).getSignatureUrl())
                            .error(android.R.drawable.stat_notify_error)
                            .into(mFragmentSignatureBinding.imgSign);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            mFragmentSignatureBinding.txtRecords.setText(mGeneralRealmData.get(0).getNumberOfBhk());
            mFragmentSignatureBinding.txtFeedback.setText(mGeneralRealmData.get(0).getTechnicianOTP());
            mFragmentSignatureBinding.txtSignatory.setText(mGeneralRealmData.get(0).getSignatory());
            try {
                mask = mobile.replaceAll("\\w(?=\\w{4})", "*");
            } catch (Exception e) {
                e.printStackTrace();
            }
            Email = mGeneralRealmData.get(0).getEmail();
            mFragmentSignatureBinding.txtAmount.setText(amount + " " + "\u20B9");
            actual_property = mFragmentSignatureBinding.txtActualSize.getText().toString();
            feedback_code = mFragmentSignatureBinding.txtFeedback.getText().toString();
            signatory = mFragmentSignatureBinding.txtSignatory.getText().toString();
            isJobcardEnable = mGeneralRealmData.get(0).getJobCardRequired();
            isFeedBack = mGeneralRealmData.get(0).getFeedBack();

            if (isJobcardEnable) {
                mFragmentSignatureBinding.btnUpload.setVisibility(View.VISIBLE);
            } else {
                mFragmentSignatureBinding.btnUpload.setVisibility(View.GONE);
            }
            getValidate();
            if (!mActualProperty.equals("")) {
                mFragmentSignatureBinding.txtActualSize.setText(mActualProperty);
            }
            if (!mSignatory.equals("")) {
                mFragmentSignatureBinding.txtSignatory.setText(mSignatory);
            }
            if (!mSignature.equals("")) {
                Bitmap myBitmap = BitmapFactory.decodeFile(mSignature);
                mFragmentSignatureBinding.txtHint.setVisibility(GONE);
                mFragmentSignatureBinding.imgSign.setImageBitmap(myBitmap);
            }

        }
        if (mFragmentSignatureBinding.imgSign.getDrawable() != null) {
            mFragmentSignatureBinding.txtSignatory.setEnabled(false);
        }

    }


    @Override
    public void onSignatureClicked(View view) {

        if (mFragmentSignatureBinding.imgSign.getDrawable() != null) {

        } else {
            LayoutInflater li = LayoutInflater.from(getActivity());

            View promptsView = li.inflate(R.layout.signature_dialog, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

            alertDialogBuilder.setView(promptsView);

            // set dialog message

            alertDialogBuilder.setTitle("Signature");

            // create alert dialog
            final AlertDialog alertDialog = alertDialogBuilder.create();

            final RelativeLayout lnr_screen =
                    (RelativeLayout) promptsView.findViewById(R.id.lnr_screen);
            final AppCompatImageView img_right =
                    (AppCompatImageView) promptsView.findViewById(R.id.img_right);
            final AppCompatImageView img_wrong =
                    (AppCompatImageView) promptsView.findViewById(R.id.img_wrong);
            final AppCompatButton btn_close =
                    (AppCompatButton) promptsView.findViewById(R.id.btn_close);
            final AppCompatTextView txt_hint =
                    (AppCompatTextView) promptsView.findViewById(R.id.txt_hint);
            dv = new DrawingView(getActivity(), txt_hint);
            lnr_screen.addView(dv);

            img_right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    View view = lnr_screen;
                    view.setDrawingCacheEnabled(true);
                    bmp = Bitmap.createBitmap(view.getDrawingCache());
                    view.setDrawingCacheEnabled(false);
                    try {
                        file = new File(Environment.getExternalStorageDirectory().toString(), "SCREEN"
                                + System.currentTimeMillis() + ".png");
                        Log.e("here", "------------" + file);
                        FileOutputStream fos = new FileOutputStream(file);
                        bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);

                        mFile = file;
                        fos.flush();
                        fos.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    onCallBack();
                    alertDialog.dismiss();

                }

            });

            img_wrong.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lnr_screen.removeAllViews();
                    dv = new DrawingView(getActivity(), txt_hint);
                    mPaint = new Paint();
                    mPaint.setAntiAlias(true);
                    mPaint.setDither(true);
                    mPaint.setColor(Color.BLACK);
                    mPaint.setStyle(Paint.Style.STROKE);
                    mPaint.setStrokeJoin(Paint.Join.ROUND);
                    mPaint.setStrokeCap(Paint.Cap.ROUND);
                    mPaint.setStrokeWidth(8);
                    lnr_screen.addView(dv);
                    txt_hint.setVisibility(View.VISIBLE);
                }
            });

            btn_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });

            alertDialog.setIcon(R.mipmap.logo);
            // show it
            alertDialog.show();
        }
    }

    @Override
    public void onSendLinkClicked(View view) {
        if (isFeedBack) {
            LayoutInflater li = LayoutInflater.from(getActivity());

            View promptsView = li.inflate(R.layout.link_confirm_dialog, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

            alertDialogBuilder.setView(promptsView);

            alertDialogBuilder.setTitle("Feedback Link");

            // create alert dialog
            final AlertDialog alertDialog = alertDialogBuilder.create();

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
            edtemail.setText(Email);
            edtmobile.setText(mask);

            btn_send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    FeedbackRequest request = new FeedbackRequest();
                    request.setName(name);
                    request.setTask_id(taskId);
                    request.setOrder_number(Order_Number);
                    request.setService_name(Service_Name);
                    NetworkCallController controller = new NetworkCallController();
                    controller.setListner(new NetworkResponseListner() {
                        @Override
                        public void onResponse(int requestCode, Object response) {
                            FeedbackResponse refResponse = (FeedbackResponse) response;
                            if (refResponse.getSuccess() == true) {
                                Toast.makeText(getActivity(), "Feedback link is sent successfully.", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(int requestCode) {

                        }
                    });
                    controller.postFeedbackLink(POST_FEEDBACK_LINK, request);

                    alertDialog.dismiss();
                }
            });


            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
            alertDialog.setIcon(R.mipmap.logo);

            alertDialog.show();

        } else {
            mFragmentSignatureBinding.btnSendlink.setEnabled(false);
        }

    }

    @Override
    public void onUploadAttachmentClicked(View view) {
        Intent intent = new Intent(getActivity(), AttachmentActivity.class);
        intent.putExtra(AttachmentActivity.ARGS_TASKS, taskId);
        startActivityForResult(intent, 1);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                listSize = bundle.getInt("listSize");
                jobCard = bundle.getBoolean("isJobCard");
            }
        }
    }


    private void onCallBack() {
        if (mFile != null) {
            if (mFile.exists()) {

                mFragmentSignatureBinding.txtHint.setVisibility(GONE);
                path = mFile.getAbsolutePath();
                Bitmap myBitmap = BitmapFactory.decodeFile(mFile.getAbsolutePath());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] b = baos.toByteArray();
                String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                signature = encodedImage;
                mCallback.signature(encodedImage);
//                mCallback.actualPropertySize(mFragmentSignatureBinding.txtActualSize.getText().toString());
//                mCallback.standardPropertySize(mFragmentSignatureBinding.txtRecords.getText().toString());
//                mCallback.feedbackCode(mFragmentSignatureBinding.txtFeedback.getText().toString());
                getValidate();
                mCallback.signatory(mFragmentSignatureBinding.txtSignatory.getText().toString());
                mFragmentSignatureBinding.imgSign.setImageBitmap(myBitmap);


            }
        }
    }


    public class DrawingView extends View {

        public int width;
        public int height;
        private Bitmap mBitmap;
        private Canvas mCanvas;
        private Path mPath;
        private Paint mBitmapPaint;
        Context context;
        private Paint circlePaint;
        private Path circlePath;
        AppCompatTextView txt_hint;

        public DrawingView(Context c, AppCompatTextView txt_hint) {
            super(c);
            context = c;
            this.txt_hint = txt_hint;
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
            circlePaint = new Paint();
            circlePath = new Path();
            circlePaint.setAntiAlias(true);
            circlePaint.setColor(Color.BLACK);
            circlePaint.setStyle(Paint.Style.STROKE);
            circlePaint.setStrokeJoin(Paint.Join.MITER);
            circlePaint.setStrokeWidth(2f);
            txt_hint.setVisibility(VISIBLE);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);

            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
            canvas.drawPath(mPath, mPaint);
            canvas.drawPath(circlePath, circlePaint);
        }

        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;

        private void touch_start(float x, float y) {
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
        }

        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                mX = x;
                mY = y;

                circlePath.reset();
                circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
            }
        }

        private void touch_up() {
            mPath.lineTo(mX, mY);
            circlePath.reset();
            // commit the path to our offscreen
            mCanvas.drawPath(mPath, mPaint);
            // kill this so we don't double draw
            mPath.reset();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    txt_hint.setVisibility(GONE);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
            return true;
        }

    }

    public void getValidate() {
        if (isFeedBack) {
            mFragmentSignatureBinding.lnrOtp.setVisibility(View.VISIBLE);
            String otp = mFragmentSignatureBinding.txtFeedback.getText().toString().trim();
            String sc_otp = mGeneralRealmData.get(0).getSc_OTP();
            String customer_otp = mGeneralRealmData.get(0).getCustomer_OTP();
            if (status.equals("Completed") || status.equals("Incomplete")) {
                mCallback.isFeedbackRequired(false);
                mFragmentSignatureBinding.txtFeedback.setEnabled(false);
                mFragmentSignatureBinding.btnSendlink.setVisibility(View.GONE);

            } else {
                mFragmentSignatureBinding.txtFeedback.setEnabled(true);
                mFragmentSignatureBinding.btnSendlink.setVisibility(View.VISIBLE);
                if (otp.length() != 0) {
                    if (otp.equals(sc_otp) || otp.equals(customer_otp)) {
                        mCallback.feedbackCode(otp);
                        mCallback.isOTPValidated(false);
                    } else {
                        mCallback.isOTPValidated(true);
                    }
                } else {
                    mCallback.isOTPValidated(true);
                }
            }
        } else {
            mFragmentSignatureBinding.txtFeedback.setEnabled(false);
            mFragmentSignatureBinding.lnrOtp.setVisibility(GONE);
            mFragmentSignatureBinding.btnSendlink.setVisibility(View.GONE);
            mCallback.isSignatureChanged(false);
        }

        if (isJobcardEnable) {
            if (listSize >= 0) {
                mCallback.isAttachmentError(false);
            } else {
                mCallback.isAttachmentError(true);
            }
        } else {
            mCallback.isAttachmentError(false);
        }
    }


}
