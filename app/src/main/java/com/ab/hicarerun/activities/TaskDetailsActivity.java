package com.ab.hicarerun.activities;

import android.app.ProgressDialog;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.ab.hicarerun.BaseActivity;
import com.ab.hicarerun.R;
import com.ab.hicarerun.adapter.TaskViewPagerAdapter;
import com.ab.hicarerun.databinding.ActivityTaskDetailsBinding;
import com.ab.hicarerun.fragments.ChemicalFragment;
import com.ab.hicarerun.fragments.GeneralFragment;
import com.ab.hicarerun.fragments.PaymentFragment;
import com.ab.hicarerun.fragments.ReferralFragment;
import com.ab.hicarerun.fragments.SignatureFragment;
import com.ab.hicarerun.handler.OnSaveEventHandler;
import com.ab.hicarerun.network.NetworkCallController;
import com.ab.hicarerun.network.NetworkResponseListner;
import com.ab.hicarerun.network.models.GeneralModel.GeneralResponse;
import com.ab.hicarerun.network.models.LoginResponse;
import com.ab.hicarerun.network.models.TaskModel.TaskChemicalList;
import com.ab.hicarerun.network.models.TaskModel.Tasks;
import com.ab.hicarerun.network.models.TaskModel.UpdateTaskResponse;
import com.ab.hicarerun.network.models.TaskModel.UpdateTasksRequest;
import com.ab.hicarerun.service.LocationManager;
import com.ab.hicarerun.service.listner.LocationManagerListner;
import com.ab.hicarerun.utils.AppUtils;

import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;
import hyogeun.github.com.colorratingbarlib.ColorRatingBar;
import in.myinnos.androidscratchcard.ScratchCard;
import io.realm.RealmResults;

import static com.ab.hicarerun.BaseApplication.getRealm;

public class TaskDetailsActivity extends BaseActivity implements LocationManagerListner, OnSaveEventHandler {
    ActivityTaskDetailsBinding mActivityTaskDetailsBinding;
    public static final String ARGS_TASKS = "ARGS_TASKS";
    private static final int UPDATE_REQUEST = 1000;
    private String UserId = "";
    private String taskId = "";
    private static final int TASK_BY_ID_REQUEST = 1000;
    private Location mLocation;
    private String Status = "", Payment_Mode = "", Amount_Collected = "", Amount_To_Collected = "", Actual_Size = "", Standard_Size = "", Feedback_Code = "", signatory = "", Signature = "", Duration = "";
    private boolean isGeneralChanged = false;
    private boolean isChemicalChanged = false;
    private boolean isPaymentChanged = false;
    private boolean isSignatureChanged = false;
    private boolean isFeedbackRequired = false;
    private boolean isCardRequired = false;
    private boolean isIncentiveEnable = false;
    private boolean isOTPValidated = false;
    private boolean isTechnicianFeedbackEnable = false;
    private int Incentive = 0;
    private int TechnicianRating = 0;
    private String incompleteReason = "";
    private HashMap<Integer, String> mMap = null;
    private List<TaskChemicalList> ChemReqList = null;
    private LocationManagerListner mListner;
    Tasks model;
    private String sta = "";
    private MenuItem menuItem;
    private Menu menu;
    private int Rate = 0;
    private TaskViewPagerAdapter mAdapter;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityTaskDetailsBinding =
                DataBindingUtil.setContentView(this, R.layout.activity_task_details);


        setSupportActionBar(mActivityTaskDetailsBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mActivityTaskDetailsBinding.viewpager.setOffscreenPageLimit(5);
        model = getIntent().getParcelableExtra(ARGS_TASKS);

        LocationManager.Builder builder = new LocationManager.Builder(this);
        builder.setLocationListner(this);
        builder.build();
        progress = new ProgressDialog(this, R.style.TransparentProgressDialog);
        progress.setCancelable(false);
        getTaskDetailsById();

    }

    private void getTaskDetailsById() {
        if (this != null) {
            RealmResults<LoginResponse> LoginRealmModels =
                    getRealm().where(LoginResponse.class).findAll();

            if (LoginRealmModels != null && LoginRealmModels.size() > 0) {
                UserId = LoginRealmModels.get(0).getUserID();
                NetworkCallController controller = new NetworkCallController();
                controller.setListner(new NetworkResponseListner<GeneralResponse>() {

                    @Override
                    public void onResponse(int requestCode, GeneralResponse response) {
                        // add new record
                        getRealm().beginTransaction();
                        getRealm().copyToRealmOrUpdate(response.getData());
                        getRealm().commitTransaction();
                        sta = response.getData().getSchedulingStatus();
                        isIncentiveEnable = response.getData().getIncentiveEnable();
                        Incentive = Integer.parseInt(response.getData().getIncentivePoint());
                        isTechnicianFeedbackEnable = response.getData().getTechnicianFeedbackRequired();
                        TechnicianRating = Integer.parseInt(response.getData().getTechnicianRating());
                        setupNavigationView();
                        setViewPagerView();
                    }

                    @Override
                    public void onFailure(int requestCode) {
                    }
                });
                controller.getTaskDetailById(TASK_BY_ID_REQUEST, UserId, model.getTaskId());
            }
        }
    }


    private void setViewPagerView() {

        mAdapter = new TaskViewPagerAdapter(getSupportFragmentManager());
        mAdapter.addFragment(GeneralFragment.newInstance(model.getTaskId(), model.getStatus()), "General");
        mAdapter.addFragment(ChemicalFragment.newInstance(model.getTaskId()), "Chemical Required");
        mAdapter.addFragment(ReferralFragment.newInstance(model.getTaskId()), "Customer Referrals");
        mAdapter.addFragment(PaymentFragment.newInstance(), "Payment");
        mAdapter.addFragment(SignatureFragment.newInstance(model.getTaskId()), "Customer Signature");
        mActivityTaskDetailsBinding.viewpager.setAdapter(mAdapter);

        mActivityTaskDetailsBinding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {
                if (menuItem != null)
                    menuItem.setChecked(false);
                else
                    mActivityTaskDetailsBinding.bottomNavigation.getMenu().getItem(0).setChecked(false);

                mActivityTaskDetailsBinding.bottomNavigation.getMenu().getItem(position).setChecked(true);
                menuItem = mActivityTaskDetailsBinding.bottomNavigation.getMenu().getItem(position);
                switch (position) {
                    case 0:
                        mActivityTaskDetailsBinding.toolbar.setTitle("General");
                        break;
                    case 1:
                        mActivityTaskDetailsBinding.toolbar.setTitle("Chemicals Required");
                        break;
                    case 2:
                        mActivityTaskDetailsBinding.toolbar.setTitle("Customer Referrals");
                        break;
                    case 3:
                        mActivityTaskDetailsBinding.toolbar.setTitle("Payment");
                        break;
                    case 4:
                        mActivityTaskDetailsBinding.toolbar.setTitle("Customer Signature");
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });


    }

    private void showRatingDialog() {
        LayoutInflater li = LayoutInflater.from(this);

        View promptsView = li.inflate(R.layout.dialog_rating, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setView(promptsView);

        alertDialogBuilder.setTitle("Customer Ratings");
        final AlertDialog alertDialog = alertDialogBuilder.create();

        final ColorRatingBar ratingBar =
                (ColorRatingBar) promptsView.findViewById(R.id.rating_bar);

        final AppCompatButton btn_submit =
                (AppCompatButton) promptsView.findViewById(R.id.btn_submit);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Rate = (int) ratingBar.getRating();
                alertDialog.dismiss();
                getSaveMenu();
            }

        });

        alertDialog.setIcon(R.mipmap.logo);

        alertDialog.show();
    }

    private void setupNavigationView() {
        if (mActivityTaskDetailsBinding.bottomNavigation != null) {

            // Select first menu item by default and show Fragment accordingly.
            Menu menu = mActivityTaskDetailsBinding.bottomNavigation.getMenu();

            selectFragment(menu.getItem(0));

            // Set action to perform when any menu-item is selected.
            mActivityTaskDetailsBinding.bottomNavigation.setOnNavigationItemSelectedListener(
                    new BottomNavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                            selectFragment(item);
                            return false;
                        }
                    });
        }


    }

    private void selectFragment(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_general:
//                getSupportFragmentManager().beginTransaction().replace(mActivityTaskDetailsBinding.container.getId(), GeneralFragment.newInstance(model.getTaskId(), model.getStatus())).addToBackStack(null).commit();
                mActivityTaskDetailsBinding.viewpager.setCurrentItem(0);
                break;

            case R.id.nav_chemicals:
//                getSupportFragmentManager().beginTransaction().replace(mActivityTaskDetailsBinding.container.getId(), ChemicalFragment.newInstance(model.getTaskId())).addToBackStack(null).commit();
                mActivityTaskDetailsBinding.viewpager.setCurrentItem(1);
                break;
            case R.id.nav_referral:
//                getSupportFragmentManager().beginTransaction().replace(mActivityTaskDetailsBinding.container.getId(), ReferralFragment.newInstance(model.getTaskId())).addToBackStack(null).commit();
                mActivityTaskDetailsBinding.viewpager.setCurrentItem(2);
                break;
            case R.id.nav_payment:
//                getSupportFragmentManager().beginTransaction().replace(mActivityTaskDetailsBinding.container.getId(), PaymentFragment.newInstance()).addToBackStack(null).commit();
                mActivityTaskDetailsBinding.viewpager.setCurrentItem(3);
                break;
            case R.id.nav_signature:
//                getSupportFragmentManager().beginTransaction().replace(mActivityTaskDetailsBinding.container.getId(), SignatureFragment.newInstance(model.getTaskId())).addToBackStack(null).commit();
                mActivityTaskDetailsBinding.viewpager.setCurrentItem(4);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_tasks, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
//        if (!isShowMenu) {
        if (Build.VERSION.SDK_INT > 11) {
            if (sta.equals("Completed") || sta.equals("Incomplete")) {
                invalidateOptionsMenu();
                menu.findItem(R.id.menu_save).setVisible(false);
            } else {
                invalidateOptionsMenu();
                menu.findItem(R.id.menu_save).setVisible(true);
            }
        }
//            isShowMenu = true;
//        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_save:
                getSaveMenu();
                return false;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getSaveMenu() {
        progress.show();
        if (isGeneralChanged) {
            mActivityTaskDetailsBinding.viewpager.setCurrentItem(0);
            Toasty.error(this, "Please change status.", Toast.LENGTH_SHORT, true).show();
            progress.dismiss();

        } else if (isChemicalChanged && Status.equals("Completed")) {
            mActivityTaskDetailsBinding.viewpager.setCurrentItem(1);
            Toasty.error(this, "Chemical entries are required.", Toast.LENGTH_SHORT, true).show();
            progress.dismiss();
        } else if (isPaymentChanged && Status.equals("Completed")) {
            mActivityTaskDetailsBinding.viewpager.setCurrentItem(3);
            Toasty.error(this, "Payment fields are required.", Toast.LENGTH_SHORT, true).show();
            progress.dismiss();
        } else if (isSignatureChanged && Status.equals("Completed")) {
            mActivityTaskDetailsBinding.viewpager.setCurrentItem(4);
            progress.dismiss();
            Toasty.error(this, "Signature fields are required.", Toast.LENGTH_SHORT, true).show();
        } else if (isCardRequired && Status.equals("Completed")) {
            mActivityTaskDetailsBinding.viewpager.setCurrentItem(4);
            progress.dismiss();
            Toasty.error(this, "Invalid OTP.", Toast.LENGTH_SHORT, true).show();
        } else if (isOTPValidated && Status.equals("Completed")) {
            mActivityTaskDetailsBinding.viewpager.setCurrentItem(4);
            progress.dismiss();
        } else {

            if (isTechnicianFeedbackEnable && Rate == 0 && Status.equals("Completed")) {
                showRatingDialog();
            } else {
                if (getmLocation() != null) {
                    RealmResults<LoginResponse> LoginRealmModels =
                            getRealm().where(LoginResponse.class).findAll();
                    if (LoginRealmModels != null && LoginRealmModels.size() > 0) {
                        String UserId = LoginRealmModels.get(0).getUserID();
                        UpdateTasksRequest request = new UpdateTasksRequest();
                        request.setSchedulingStatus(Status);
                        request.setPaymentMode(Payment_Mode);
                        request.setAmountCollected(Amount_Collected);
                        request.setAmountToCollect(Amount_To_Collected);
                        request.setActualPropertySize(Actual_Size);
                        request.setStandardPropertySize(Standard_Size);
                        request.setTechnicianRating(Rate);
                        request.setTechnicianOTP(Feedback_Code);
                        request.setSignatory(signatory);
                        request.setCustomerSign(Signature);
                        request.setLatitude(String.valueOf(getmLocation().getLatitude()));
                        request.setLongitude(String.valueOf(getmLocation().getLongitude()));
                        request.setTaskId(model.getTaskId());
                        request.setDuration(Duration);
                        request.setResourceId(UserId);
                        request.setChemicalList(ChemReqList);
                        request.setIncompleteReason(incompleteReason);

                        NetworkCallController controller = new NetworkCallController();
                        controller.setListner(new NetworkResponseListner() {
                            @Override
                            public void onResponse(int requestCode, Object response) {
                                UpdateTaskResponse updateResponse = (UpdateTaskResponse) response;
                                if (updateResponse.getSuccess() == true) {
                                    progress.dismiss();
                                    Toast.makeText(TaskDetailsActivity.this, "Data successfully saved.", Toast.LENGTH_LONG).show();
                                    if (Status.equals("Completed")) {
                                        getIncentiveDialog();
                                    } else {
                                        onBackPressed();
                                        finish();
                                    }

                                } else {
                                    progress.dismiss();
                                    Toast.makeText(TaskDetailsActivity.this, "Failed.", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(int requestCode) {

                            }
                        });
                        controller.updateTasks(UPDATE_REQUEST, request);
                    }
                }
            }

        }
    }

    private void getIncentiveDialog() {
//        FragmentManager fm = getSupportFragmentManager();
//        BlankFragment fragment = BlankFragment.newInstance(Incentive);
//        fragment.show(fm, "fragment");
//        fragment.setCancelable(false);
        LayoutInflater li = LayoutInflater.from(this);

        View promptsView = li.inflate(R.layout.dialog_incentive, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);


        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setCancelable(false);

        final AlertDialog alertDialog = alertDialogBuilder.create();
        final AppCompatTextView txtReward =
                (AppCompatTextView) promptsView.findViewById(R.id.txtReward);
        final AppCompatTextView txtIncentive =
                (AppCompatTextView) promptsView.findViewById(R.id.txtIncentive);
        final AppCompatTextView txtLose =
                (AppCompatTextView) promptsView.findViewById(R.id.txtLose);
        final ScratchCard scratchCard =
                (ScratchCard) promptsView.findViewById(R.id.scratchCard);
        final AppCompatImageView imgAward =
                (AppCompatImageView) promptsView.findViewById(R.id.imgAward);
        final AppCompatImageView imgNoAward =
                (AppCompatImageView) promptsView.findViewById(R.id.imgNoAward);
        final AppCompatButton btnOk =
                (AppCompatButton) promptsView.findViewById(R.id.btnOk);
//        final AppCompatImageView imgClose =
//                (AppCompatImageView) promptsView.findViewById(R.id.imgClose);

        if (Incentive == 0) {
            imgAward.setVisibility(View.INVISIBLE);
            imgNoAward.setVisibility(View.VISIBLE);
            txtLose.setVisibility(View.VISIBLE);
            txtIncentive.setVisibility(View.GONE);
            txtReward.setVisibility(View.GONE);
        } else {
            imgAward.setVisibility(View.VISIBLE);
            imgNoAward.setVisibility(View.INVISIBLE);
            txtLose.setVisibility(View.GONE);
            txtIncentive.setVisibility(View.VISIBLE);
            txtReward.setVisibility(View.VISIBLE);
            txtIncentive.setText(Incentive + " Points");

        }


        scratchCard.setOnScratchListener(new ScratchCard.OnScratchListener() {
            @Override
            public void onScratch(ScratchCard scratchCard, float visiblePercent) {
                if (visiblePercent > 0.3) {
                    btnOk.setVisibility(View.VISIBLE);
                }
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
                alertDialog.dismiss();
            }
        });
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = alertDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;

//        WindowManager.LayoutParams lp = alertDialog.getWindow().getAttributes();
//        lp.dimAmount = 0.0f;
//        alertDialog.getWindow().setAttributes(lp);
//        alertDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        window.setAttributes(wlp);
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    @Override
    public void locationFetched(Location mLocation, Location oldLocation, String time,
                                String locationProvider) {
        this.mLocation = mLocation;
        if (mListner != null) {
            mListner.locationFetched(mLocation, oldLocation, time, locationProvider);
        }
    }

    public Location getmLocation() {
        return mLocation;
    }

    @Override
    public void onBackPressed() {
        if (mActivityTaskDetailsBinding.viewpager.getCurrentItem() == 0) {
            AppUtils.getDataClean();
            finish();
            super.onBackPressed();
        } else {
            mActivityTaskDetailsBinding.viewpager.setCurrentItem(0, true);
            AppUtils.getDataClean();
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mActivityTaskDetailsBinding.viewpager.getCurrentItem() > 0) {
                mActivityTaskDetailsBinding.viewpager.setCurrentItem(0, true);
            } else {
                AppUtils.getDataClean();
                finish();
            }
            return true;

        } else {
            return super.onKeyDown(keyCode, event);
        }
    }


    @Override
    public void status(String s) {
        Status = s;
    }

    @Override
    public void mode(String s) {
        Payment_Mode = s;
    }

    @Override
    public void amountCollected(String s) {
        Amount_Collected = s;
    }

    @Override
    public void amountToCollect(String s) {
        Amount_To_Collected = s;
    }
//
//    @Override
//    public void actualPropertySize(String s) {
//        Actual_Size = s;
//    }
//
//    @Override
//    public void standardPropertySize(String s) {
//        Standard_Size = s;
//    }

    @Override
    public void feedbackCode(String s) {
        Feedback_Code = s;
    }

    @Override
    public void signatory(String s) {
        signatory = s;
    }

    @Override
    public void signature(String s) {
        Signature = s;
    }

    @Override
    public void duration(String s) {
        Duration = s;
    }

    @Override
    public void chemicalList(HashMap<Integer, String> map) {
        mMap = map;
    }

    @Override
    public void chemReqList(List<TaskChemicalList> mList) {
        ChemReqList = mList;
    }

    @Override
    public void isGeneralChanged(Boolean b) {
        isGeneralChanged = b;
    }

    @Override
    public void isChemicalChanged(Boolean b) {
        isChemicalChanged = b;
    }

    @Override
    public void isPaymentChanged(Boolean b) {
        isPaymentChanged = b;
    }

    @Override
    public void isSignatureChanged(Boolean b) {
        isSignatureChanged = b;
    }

    @Override
    public void isOTPValidated(Boolean b) {
        isOTPValidated = b;
    }

    @Override
    public void isFeedbackRequired(Boolean b) {
        isFeedbackRequired = b;
    }

    @Override
    public void getIncompleteReason(String s) {
        incompleteReason = s;
    }

    @Override
    public void isAttachmentError(Boolean b) {
        isCardRequired = b;
    }

}
