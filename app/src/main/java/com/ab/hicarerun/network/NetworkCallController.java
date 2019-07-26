package com.ab.hicarerun.network;

import com.ab.hicarerun.BaseApplication;
import com.ab.hicarerun.BaseFragment;
import com.ab.hicarerun.network.models.AttachmentModel.AttachmentDeleteRequest;
import com.ab.hicarerun.network.models.AttachmentModel.GetAttachmentResponse;
import com.ab.hicarerun.network.models.AttachmentModel.PostAttachmentRequest;
import com.ab.hicarerun.network.models.AttachmentModel.PostAttachmentResponse;
import com.ab.hicarerun.network.models.AttendanceModel.AttendanceRequest;
import com.ab.hicarerun.network.models.AttendanceModel.ProfilePicRequest;
import com.ab.hicarerun.network.models.BasicResponse;
import com.ab.hicarerun.network.models.ChemicalModel.ChemicalResponse;
import com.ab.hicarerun.network.models.ExotelModel.ExotelResponse;
import com.ab.hicarerun.network.models.FeedbackModel.FeedbackRequest;
import com.ab.hicarerun.network.models.FeedbackModel.FeedbackResponse;
import com.ab.hicarerun.network.models.GeneralModel.GeneralResponse;
import com.ab.hicarerun.network.models.HandShakeModel.ContinueHandShakeRequest;
import com.ab.hicarerun.network.models.HandShakeModel.ContinueHandShakeResponse;
import com.ab.hicarerun.network.models.HandShakeModel.HandShakeResponse;
import com.ab.hicarerun.network.models.LoggerModel.ErrorLoggerModel;
import com.ab.hicarerun.network.models.LoginResponse;
import com.ab.hicarerun.network.models.LogoutResponse;
import com.ab.hicarerun.network.models.OtpModel.SendOtpResponse;
import com.ab.hicarerun.network.models.PayementModel.PaymentLinkRequest;
import com.ab.hicarerun.network.models.PayementModel.PaymentLinkResponse;
import com.ab.hicarerun.network.models.ProfileModel.TechnicianProfileDetails;
import com.ab.hicarerun.network.models.ReferralModel.ReferralDeleteRequest;
import com.ab.hicarerun.network.models.ReferralModel.ReferralListResponse;
import com.ab.hicarerun.network.models.ReferralModel.ReferralRequest;
import com.ab.hicarerun.network.models.ReferralModel.ReferralResponse;
import com.ab.hicarerun.network.models.TaskModel.TaskListResponse;
import com.ab.hicarerun.network.models.TaskModel.UpdateTasksRequest;
import com.ab.hicarerun.network.models.TaskModel.UpdateTaskResponse;
import com.ab.hicarerun.network.models.TechnicianGroomingModel.TechGroomingRequest;
import com.ab.hicarerun.network.models.TechnicianGroomingModel.TechGroomingResponse;
import com.ab.hicarerun.network.models.TrainingModel.TrainingResponse;
import com.ab.hicarerun.network.models.UpdateAppModel.UpdateResponse;

import org.json.JSONObject;

import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NetworkCallController {

    private final BaseFragment mContext;
    private NetworkResponseListner mListner;

    public NetworkCallController(BaseFragment context) {
        this.mContext = context;
    }

    public NetworkCallController() {
        this.mContext = null;
    }


    public void setListner(NetworkResponseListner listner) {
        this.mListner = listner;
    }

    public void getHandShake(final int requestCode) {
        BaseApplication.getRetrofitAPI(true)
                .getHandShake()
                .enqueue(new Callback<HandShakeResponse>() {
                    @Override
                    public void onResponse(Call<HandShakeResponse> call, Response<HandShakeResponse> response) {
                        if (response != null) {
                            if (response.body() != null) {
                                mListner.onResponse(requestCode, response.body().getData());
                            } else if (response.errorBody() != null) {
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
//                            mContext.showServerError();
                        }
                    }

                    @Override
                    public void onFailure(Call<HandShakeResponse> call, Throwable t) {
//                        mContext.dismissProgressDialog();
//                        mContext.showServerError("Please try again !!!");
                    }
                });
    }

    public void getContinueHandShake(final int requestCode, ContinueHandShakeRequest request) {
//        mContext.showProgressDialog();
        BaseApplication.getRetrofitAPI(true)
                .getContinueHandShake(request)
                .enqueue(new Callback<ContinueHandShakeResponse>() {
                    @Override
                    public void onResponse(Call<ContinueHandShakeResponse> call, Response<ContinueHandShakeResponse> response) {
//                        mContext.dismissProgressDialog();
                        if (response != null) {
                            if (response.body() != null) {
                                mListner.onResponse(requestCode, response.body());
                            } else if (response.errorBody() != null) {
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());
//                                    mContext.showServerError(jObjError.getString("ErrorMessage"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
//                            mContext.showServerError();
                        }
                    }

                    @Override
                    public void onFailure(Call<ContinueHandShakeResponse> call, Throwable t) {
//                        mContext.dismissProgressDialog();
//                        mContext.showServerError("Please try again !!!");
                    }
                });
    }


    public void sendOtp(final int requestCode, final String mobile, final String isResend) {
        mContext.showProgressDialog();
        BaseApplication.getRetrofitAPI(false)
                .sendOtp(mobile, isResend)
                .enqueue(new Callback<SendOtpResponse>() {
                    @Override
                    public void onResponse(Call<SendOtpResponse> call,
                                           Response<SendOtpResponse> response) {
                        mContext.dismissProgressDialog();

                        if (response != null) {
                            if (response.body() != null) {
                                mListner.onResponse(requestCode, response.body());

                            } else if (response.errorBody() != null) {
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                                    mContext.showServerError(jObjError.getString("Message"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            mContext.showServerError();
                        }
                    }

                    @Override
                    public void onFailure(Call<SendOtpResponse> call, Throwable t) {
                        mContext.dismissProgressDialog();
                        mContext.showServerError("Please try again !!!");
                    }
                });
    }


    public void login(final int requestCode, String username, String password, String versionName, final String imei, final String device_info, String mStrPlayerId) {

        mContext.showProgressDialog();

        BaseApplication.getRetrofitAPI(false)
                .login("password", username, password, "application/x-www-form-urlencoded", imei, versionName, device_info, mStrPlayerId)
                .enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        mContext.dismissProgressDialog();

                        if (response != null) {
                            if (response.body() != null) {
                                mListner.onResponse(requestCode, response.body());
                            } else if (response.errorBody() != null) {
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                                    mContext.showServerError(response.errorBody().string());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            mContext.showServerError();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        mContext.dismissProgressDialog();
                        mContext.showServerError("Please try again !!!");
                    }
                });
    }

    public void refreshToken(final int requestCode, final String refreshToken) {
        BaseApplication.getRetrofitAPI(false)
                .refreshToken("refresh_token", refreshToken)
                .enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        if (response != null) {
                            if (response.body() != null) {
                                mListner.onResponse(requestCode, response.body());
                            } else {
                                mListner.onFailure(requestCode);
                            }
                        } else {
                            mListner.onFailure(requestCode);
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        mListner.onFailure(requestCode);
                    }
                });
    }


    public void getTasksList(final int requestCode, final String userId, final String IMEI) {
        BaseApplication.getRetrofitAPI(true)
                .getTasksList(userId, IMEI)
                .enqueue(new Callback<TaskListResponse>() {
                    @Override
                    public void onResponse(Call<TaskListResponse> call,
                                           Response<TaskListResponse> response) {
                        if (response != null) {
                            if (response.code() == 401) { // Unauthorised Access
                                NetworkCallController controller = new NetworkCallController();
                                controller.setListner(new NetworkResponseListner<LoginResponse>() {
                                    @Override
                                    public void onResponse(int reqCode, LoginResponse response) {
                                        // delete all previous record
                                        Realm.getDefaultInstance().beginTransaction();
                                        Realm.getDefaultInstance().deleteAll();
                                        Realm.getDefaultInstance().commitTransaction();

                                        // add new record
                                        Realm.getDefaultInstance().beginTransaction();
                                        Realm.getDefaultInstance().copyToRealmOrUpdate(response);
                                        Realm.getDefaultInstance().commitTransaction();
                                        getTasksList(requestCode, userId, IMEI);
                                    }

                                    @Override
                                    public void onFailure(int requestCode) {

                                    }
                                });
                                controller.refreshToken(100, getRefreshToken());
                            } else if (response.body() != null) {
                                mListner.onResponse(requestCode, response.body());

                            } else if (response.errorBody() != null) {
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                                    mContext.showServerError(jObjError.getString("Message"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            mContext.showServerError();
                        }
                    }

                    @Override
                    public void onFailure(Call<TaskListResponse> call, Throwable t) {
                        mContext.showServerError("Please try again !!!");
                    }
                });
    }

    public void getTaskDetailById(final int requestCode, final String userId, final String taskId) {
        BaseApplication.getRetrofitAPI(true)
                .getTasksDetailById(userId, taskId)
                .enqueue(new Callback<GeneralResponse>() {
                    @Override
                    public void onResponse(Call<GeneralResponse> call,
                                           Response<GeneralResponse> response) {
                        if (response != null) {
                            if (response.code() == 401) { // Unauthorised Access
                                NetworkCallController controller = new NetworkCallController();
                                controller.setListner(new NetworkResponseListner<LoginResponse>() {
                                    @Override
                                    public void onResponse(int reqCode, LoginResponse response) {
                                        // delete all previous record
                                        Realm.getDefaultInstance().beginTransaction();
                                        Realm.getDefaultInstance().deleteAll();
                                        Realm.getDefaultInstance().commitTransaction();

                                        // add new record
                                        Realm.getDefaultInstance().beginTransaction();
                                        Realm.getDefaultInstance().copyToRealmOrUpdate(response);
                                        Realm.getDefaultInstance().commitTransaction();
                                        getTaskDetailById(requestCode, userId, taskId);
                                    }

                                    @Override
                                    public void onFailure(int requestCode) {

                                    }
                                });
                                controller.refreshToken(100, getRefreshToken());
                            } else if (response.body() != null) {
                                mListner.onResponse(requestCode, response.body());

                            } else if (response.errorBody() != null) {
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());
//                                    mContext.showServerError(jObjError.getString("ErrorMessage"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
//                            mContext.showServerError();
                        }
                    }

                    @Override
                    public void onFailure(Call<GeneralResponse> call, Throwable t) {
//                        mContext.showServerError("Please try again !!!");
                    }
                });
    }

    public void postReferrals(final int requestCode, ReferralRequest request) {
//        mContext.showProgressDialog();
        BaseApplication.getRetrofitAPI(true)
                .postReferrals(request)
                .enqueue(new Callback<ReferralResponse>() {
                    @Override
                    public void onResponse(Call<ReferralResponse> call, Response<ReferralResponse> response) {
//                        mContext.dismissProgressDialog();
                        if (response != null) {
                            if (response.body() != null) {
                                mListner.onResponse(requestCode, response.body());
                            } else if (response.errorBody() != null) {
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                                    mContext.showServerError(jObjError.getString("ErrorMessage"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            mContext.showServerError();
                        }
                    }

                    @Override
                    public void onFailure(Call<ReferralResponse> call, Throwable t) {
//                        mContext.dismissProgressDialog();
                        mContext.showServerError("Please try again !!!");
                    }
                });
    }


    public void getReferrals(final int requestCode, final String taskId) {
        BaseApplication.getRetrofitAPI(true)
                .getReferrals(taskId)
                .enqueue(new Callback<ReferralListResponse>() {
                    @Override
                    public void onResponse(Call<ReferralListResponse> call,
                                           Response<ReferralListResponse> response) {
                        if (response != null) {
                            if (response.code() == 401) { // Unauthorised Access
                                NetworkCallController controller = new NetworkCallController();
                                controller.setListner(new NetworkResponseListner<LoginResponse>() {
                                    @Override
                                    public void onResponse(int reqCode, LoginResponse response) {
                                        // delete all previous record
                                        Realm.getDefaultInstance().beginTransaction();
                                        Realm.getDefaultInstance().deleteAll();
                                        Realm.getDefaultInstance().commitTransaction();

                                        // add new record
                                        Realm.getDefaultInstance().beginTransaction();
                                        Realm.getDefaultInstance().copyToRealmOrUpdate(response);
                                        Realm.getDefaultInstance().commitTransaction();
                                        getReferrals(requestCode, taskId);
                                    }

                                    @Override
                                    public void onFailure(int requestCode) {

                                    }
                                });
                                controller.refreshToken(100, getRefreshToken());
                            } else if (response.body() != null) {
                                mListner.onResponse(requestCode, response.body().getData());

                            } else if (response.errorBody() != null) {
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                                    mContext.showServerError(jObjError.getString("ErrorMessage"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            mContext.showServerError();
                        }
                    }

                    @Override
                    public void onFailure(Call<ReferralListResponse> call, Throwable t) {
                        mContext.showServerError("Please try again !!!");
                    }
                });
    }

    public void getDeleteReferrals(final int requestCode, ReferralDeleteRequest request) {
        mContext.showProgressDialog();
        BaseApplication.getRetrofitAPI(true)
                .getDeleteReferrals(request)
                .enqueue(new Callback<ReferralResponse>() {
                    @Override
                    public void onResponse(Call<ReferralResponse> call, Response<ReferralResponse> response) {
                        mContext.dismissProgressDialog();
                        if (response != null) {
                            if (response.body() != null) {
                                mListner.onResponse(requestCode, response.body());
                            } else if (response.errorBody() != null) {
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                                    mContext.showServerError(jObjError.getString("ErrorMessage"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            mContext.showServerError();
                        }
                    }

                    @Override
                    public void onFailure(Call<ReferralResponse> call, Throwable t) {
                        mContext.dismissProgressDialog();
                        mContext.showServerError("Please try again !!!");
                    }
                });
    }


    public void postFeedbackLink(final int requestCode, FeedbackRequest request) {
//        mContext.showProgressDialog();
        BaseApplication.getRetrofitAPI(true)
                .postFeedBackLink(request)
                .enqueue(new Callback<FeedbackResponse>() {
                    @Override
                    public void onResponse(Call<FeedbackResponse> call, Response<FeedbackResponse> response) {
//                        mContext.dismissProgressDialog();
                        if (response != null) {
                            if (response.body() != null) {
                                mListner.onResponse(requestCode, response.body());
                            } else if (response.errorBody() != null) {
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());
//                                    mContext.showServerError(jObjError.getString("ErrorMessage"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
//                            mContext.showServerError();
                        }
                    }

                    @Override
                    public void onFailure(Call<FeedbackResponse> call, Throwable t) {
//                        mContext.dismissProgressDialog();
//                        mContext.showServerError("Please try again !!!");
                    }
                });
    }

    public void postAttachments(final int requestCode, PostAttachmentRequest request) {

        BaseApplication.getRetrofitAPI(true)
                .postAttachments(request)
                .enqueue(new Callback<PostAttachmentResponse>() {
                    @Override
                    public void onResponse(Call<PostAttachmentResponse> call, Response<PostAttachmentResponse> response) {
                        if (response != null) {
                            if (response.body() != null) {
                                mListner.onResponse(requestCode, response.body());
                            } else if (response.errorBody() != null) {
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                        }
                    }

                    @Override
                    public void onFailure(Call<PostAttachmentResponse> call, Throwable t) {

                    }
                });
    }

    public void getAttachments(final int requestCode, final String taskId, final String userId) {
        BaseApplication.getRetrofitAPI(true)
                .getAttachments(userId, taskId)
                .enqueue(new Callback<GetAttachmentResponse>() {
                    @Override
                    public void onResponse(Call<GetAttachmentResponse> call,
                                           Response<GetAttachmentResponse> response) {
                        if (response != null) {
                            if (response.code() == 401) { // Unauthorised Access
                                NetworkCallController controller = new NetworkCallController();
                                controller.setListner(new NetworkResponseListner<LoginResponse>() {
                                    @Override
                                    public void onResponse(int reqCode, LoginResponse response) {
                                        // delete all previous record
                                        Realm.getDefaultInstance().beginTransaction();
                                        Realm.getDefaultInstance().deleteAll();
                                        Realm.getDefaultInstance().commitTransaction();

                                        // add new record
                                        Realm.getDefaultInstance().beginTransaction();
                                        Realm.getDefaultInstance().copyToRealmOrUpdate(response);
                                        Realm.getDefaultInstance().commitTransaction();
                                        getTaskDetailById(requestCode, userId, taskId);
                                    }

                                    @Override
                                    public void onFailure(int requestCode) {

                                    }
                                });
                                controller.refreshToken(100, getRefreshToken());
                            } else if (response.body() != null) {
                                mListner.onResponse(requestCode, response.body().getData());

                            } else if (response.errorBody() != null) {
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                                    mContext.showServerError(jObjError.getString("ErrorMessage"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            mContext.showServerError();
                        }
                    }

                    @Override
                    public void onFailure(Call<GetAttachmentResponse> call, Throwable t) {
                        mContext.showServerError("Please try again !!!");
                    }
                });
    }

    public void getDeleteAttachments(final int requestCode, List<AttachmentDeleteRequest> request) {
        mContext.showProgressDialog();
        BaseApplication.getRetrofitAPI(true)
                .getDeleteAttachments(request)
                .enqueue(new Callback<PostAttachmentResponse>() {
                    @Override
                    public void onResponse(Call<PostAttachmentResponse> call, Response<PostAttachmentResponse> response) {
                        mContext.dismissProgressDialog();
                        if (response != null) {
                            if (response.body() != null) {
                                mListner.onResponse(requestCode, response.body());
                            } else if (response.errorBody() != null) {
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                                    mContext.showServerError(jObjError.getString("ErrorMessage"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            mContext.showServerError();
                        }
                    }

                    @Override
                    public void onFailure(Call<PostAttachmentResponse> call, Throwable t) {
                        mContext.dismissProgressDialog();
                        mContext.showServerError("Please try again !!!");
                    }
                });
    }


    public void updateTasks(final int requestCode, UpdateTasksRequest request) {
//        mContext.showProgressDialog();
        BaseApplication.getRetrofitAPI(true)
                .updateTasks(request)
                .enqueue(new Callback<UpdateTaskResponse>() {
                    @Override
                    public void onResponse(Call<UpdateTaskResponse> call, Response<UpdateTaskResponse> response) {
//                        mContext.dismissProgressDialog();
                        if (response != null) {
                            if (response.body() != null) {
                                mListner.onResponse(requestCode, response.body());
                            } else if (response.errorBody() != null) {
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());
//                                    mContext.showServerError(jObjError.getString("ErrorMessage"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
//                            mContext.showServerError();
                        }
                    }

                    @Override
                    public void onFailure(Call<UpdateTaskResponse> call, Throwable t) {
//                        mContext.dismissProgressDialog();
//                        mContext.showServerError("Please try again !!!");
                    }
                });
    }


    public void getExotelCalled(final int requestCode, String customerNo, String techNo) {
        BaseApplication.getExotelApi()
                .getCallExotel(customerNo, techNo)
                .enqueue(new Callback<ExotelResponse>() {
                    @Override
                    public void onResponse(Call<ExotelResponse> call, Response<ExotelResponse> response) {
                        if (response != null) {
                            if (response.body() != null) {
                                mListner.onResponse(requestCode, response.body());
                            } else if (response.errorBody() != null) {
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
//                            mContext.showServerError();
                        }
                    }

                    @Override
                    public void onFailure(Call<ExotelResponse> call, Throwable t) {
//                        mContext.dismissProgressDialog();
//                        mContext.showServerError("Please try again !!!");
                    }
                });
    }


    public void getChemicals(final int requestCode, final String taskId) {
        mContext.showProgressDialog();
        BaseApplication.getRetrofitAPI(true)
                .getChemicals(taskId)
                .enqueue(new Callback<ChemicalResponse>() {
                    @Override
                    public void onResponse(Call<ChemicalResponse> call, Response<ChemicalResponse> response) {
                        mContext.dismissProgressDialog();
                        if (response != null) {
                            if (response.body() != null) {
                                mListner.onResponse(requestCode, response.body().getData());
                            } else if (response.errorBody() != null) {
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                                    mContext.showServerError(jObjError.getString("ErrorMessage"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            mContext.showServerError();
                        }
                    }

                    @Override
                    public void onFailure(Call<ChemicalResponse> call, Throwable t) {
                        mContext.dismissProgressDialog();
                        mContext.showServerError("Please try again !!!");
                    }
                });
    }


    public void getLogout(final int requestCode, final String UserId) {
        BaseApplication.getRetrofitAPI(true)
                .getLogout(UserId)
                .enqueue(new Callback<LogoutResponse>() {
                    @Override
                    public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
                        if (response != null) {
                            if (response.body() != null) {
                                mListner.onResponse(requestCode, response.body());
                            } else if (response.errorBody() != null) {
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                        }
                    }

                    @Override
                    public void onFailure(Call<LogoutResponse> call, Throwable t) {

                    }
                });
    }

    public void postResourceProfilePic(final int requestCode, final ProfilePicRequest request) {
        mContext.showProgressDialog();
        BaseApplication.getRetrofitAPI(true)
                .getProfilePic(request)
                .enqueue(new Callback<HandShakeResponse>() {
                    @Override
                    public void onResponse(Call<HandShakeResponse> call,
                                           Response<HandShakeResponse> response) {
                        mContext.dismissProgressDialog();

                        if (response != null) {
                            if (response.code() == 401) { // Unauthorised Access
                                NetworkCallController controller = new NetworkCallController();
                                controller.setListner(new NetworkResponseListner<LoginResponse>() {
                                    @Override
                                    public void onResponse(int reqCode, LoginResponse response) {
                                        // delete all previous record
                                        Realm.getDefaultInstance().beginTransaction();
                                        Realm.getDefaultInstance().deleteAll();
                                        Realm.getDefaultInstance().commitTransaction();

                                        // add new record
                                        Realm.getDefaultInstance().beginTransaction();
                                        Realm.getDefaultInstance().copyToRealmOrUpdate(response);
                                        Realm.getDefaultInstance().commitTransaction();
                                        postResourceProfilePic(requestCode, request);
                                    }

                                    @Override
                                    public void onFailure(int requestCode) {

                                    }
                                });
                                controller.refreshToken(100, getRefreshToken());
                            } else if (response.body() != null) {
                                mListner.onResponse(requestCode, response.body());

                            } else if (response.errorBody() != null) {
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());
//                                    mContext.showServerError(jObjError.getString("Message"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
//                            mContext.showServerError();
                        }
                    }

                    @Override
                    public void onFailure(Call<HandShakeResponse> call, Throwable t) {
//                        mContext.showServerError("Please try again !!!");
                    }
                });
    }

    public void getTechAttendance(final int requestCode, final AttendanceRequest request) {
        BaseApplication.getRetrofitAPI(true)
                .getTechAttendance(request)
                .enqueue(new Callback<ContinueHandShakeResponse>() {
                    @Override
                    public void onResponse(Call<ContinueHandShakeResponse> call,
                                           Response<ContinueHandShakeResponse> response) {
                        if (response != null) {
                            if (response.code() == 401) { // Unauthorised Access
                                NetworkCallController controller = new NetworkCallController();
                                controller.setListner(new NetworkResponseListner<LoginResponse>() {
                                    @Override
                                    public void onResponse(int reqCode, LoginResponse response) {
                                        // delete all previous record
                                        Realm.getDefaultInstance().beginTransaction();
                                        Realm.getDefaultInstance().deleteAll();
                                        Realm.getDefaultInstance().commitTransaction();

                                        // add new record
                                        Realm.getDefaultInstance().beginTransaction();
                                        Realm.getDefaultInstance().copyToRealmOrUpdate(response);
                                        Realm.getDefaultInstance().commitTransaction();
                                        getTechAttendance(requestCode, request);
                                    }

                                    @Override
                                    public void onFailure(int requestCode) {

                                    }
                                });
                                controller.refreshToken(100, getRefreshToken());
                            } else if (response.body() != null) {
                                mListner.onResponse(requestCode, response.body());

                            } else if (response.errorBody() != null) {
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());
//                                    mContext.showServerError(jObjError.getString("Message"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
//                            mContext.showServerError();
                        }
                    }

                    @Override
                    public void onFailure(Call<ContinueHandShakeResponse> call, Throwable t) {
//                        mContext.showServerError("Please try again !!!");
                    }
                });
    }

    public void getTrainingVideos(final int requestCode) {
        mContext.showProgressDialog();
        BaseApplication.getRetrofitAPI(true)
                .getTrainingVideos()
                .enqueue(new Callback<TrainingResponse>() {
                    @Override
                    public void onResponse(Call<TrainingResponse> call, Response<TrainingResponse> response) {
                        mContext.dismissProgressDialog();
                        if (response != null) {
                            if (response.body() != null) {
                                mListner.onResponse(requestCode, response.body().getData());
                            } else if (response.errorBody() != null) {
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            mContext.showServerError();
                        }
                    }

                    @Override
                    public void onFailure(Call<TrainingResponse> call, Throwable t) {
                        mContext.dismissProgressDialog();
                        mContext.showServerError("Please try again !!!");
                    }
                });
    }

    /*[Error Log]*/

    public void sendErrorLog(final int requestCode, final ErrorLoggerModel request) {
        BaseApplication.getLoggerApi()
                .sendErrorLog(request)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call,
                                           Response<String> response) {
                        if (response != null) {
                            if (response.body() != null) {
                                mListner.onResponse(requestCode, response.body());
                            } else if (response.errorBody() != null) {
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            mContext.showServerError();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                    }
                });
    }

    /*[update APP]*/

    public void getUpdateApp(final int requestCode) {
        BaseApplication.getRetrofitAPI(true)
                .getUpdateApp()
                .enqueue(new Callback<UpdateResponse>() {
                    @Override
                    public void onResponse(Call<UpdateResponse> call, Response<UpdateResponse> response) {
                        if (response != null) {
                            if (response.body() != null) {
                                mListner.onResponse(requestCode, response.body().getData());
                            } else if (response.errorBody() != null) {
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
//                            mContext.showServerError();
                        }
                    }

                    @Override
                    public void onFailure(Call<UpdateResponse> call, Throwable t) {
//                        mContext.dismissProgressDialog();
//                        mContext.showServerError("Please try again !!!");
                    }
                });
    }

    /*[Send Payment Link]*/

    public void sendPaymentLink(final int requestCode, final PaymentLinkRequest request) {
        BaseApplication.getRetrofitAPI(true)
                .sendPaymentLink(request)
                .enqueue(new Callback<PaymentLinkResponse>() {
                    @Override
                    public void onResponse(Call<PaymentLinkResponse> call,
                                           Response<PaymentLinkResponse> response) {
                        if (response != null) {
                            if (response.body() != null) {
                                mListner.onResponse(requestCode, response.body());
                            } else if (response.errorBody() != null) {
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            mContext.showServerError();
                        }
                    }

                    @Override
                    public void onFailure(Call<PaymentLinkResponse> call, Throwable t) {
                    }
                });
    }

    public void getGroomingTechnicians(final int requestCode, final String userId) {
        BaseApplication.getRetrofitAPI(true)
                .getGroomingTechnicians(userId)
                .enqueue(new Callback<TechGroomingResponse>() {
                    @Override
                    public void onResponse(Call<TechGroomingResponse> call,
                                           Response<TechGroomingResponse> response) {
                        if (response != null) {
                            if (response.code() == 401) { // Unauthorised Access
                                NetworkCallController controller = new NetworkCallController();
                                controller.setListner(new NetworkResponseListner<LoginResponse>() {
                                    @Override
                                    public void onResponse(int reqCode, LoginResponse response) {
                                        // delete all previous record
                                        Realm.getDefaultInstance().beginTransaction();
                                        Realm.getDefaultInstance().deleteAll();
                                        Realm.getDefaultInstance().commitTransaction();

                                        // add new record
                                        Realm.getDefaultInstance().beginTransaction();
                                        Realm.getDefaultInstance().copyToRealmOrUpdate(response);
                                        Realm.getDefaultInstance().commitTransaction();
                                        getGroomingTechnicians(requestCode, userId);
                                    }

                                    @Override
                                    public void onFailure(int requestCode) {

                                    }
                                });
                                controller.refreshToken(100, getRefreshToken());
                            } else if (response.body() != null) {
                                mListner.onResponse(requestCode, response.body().getData());

                            } else if (response.errorBody() != null) {
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                                    mContext.showServerError(jObjError.getString("ErrorMessage"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            mContext.showServerError();
                        }
                    }

                    @Override
                    public void onFailure(Call<TechGroomingResponse> call, Throwable t) {
                        mContext.showServerError("Please try again !!!");
                    }
                });
    }

    /*[Post Grooming Image]*/

    public void postGroomingImage(final int requestCode, final TechGroomingRequest request) {
        mContext.showProgressDialog();
        BaseApplication.getRetrofitAPI(true)
                .postGroomingImage(request)
                .enqueue(new Callback<BasicResponse>() {
                    @Override
                    public void onResponse(Call<BasicResponse> call,
                                           Response<BasicResponse> response) {
                        mContext.dismissProgressDialog();
                        if (response != null) {

                            if (response.code() == 401) { // Unauthorised Access
                                NetworkCallController controller = new NetworkCallController();
                                controller.setListner(new NetworkResponseListner<LoginResponse>() {
                                    @Override
                                    public void onResponse(int reqCode, LoginResponse response) {
                                        // delete all previous record
                                        Realm.getDefaultInstance().beginTransaction();
                                        Realm.getDefaultInstance().deleteAll();
                                        Realm.getDefaultInstance().commitTransaction();

                                        // add new record
                                        Realm.getDefaultInstance().beginTransaction();
                                        Realm.getDefaultInstance().copyToRealmOrUpdate(response);
                                        Realm.getDefaultInstance().commitTransaction();
                                        postGroomingImage(requestCode, request);
                                    }

                                    @Override
                                    public void onFailure(int requestCode) {

                                    }
                                });
                                controller.refreshToken(100, getRefreshToken());
                            }
                           else if (response.body() != null) {
                                mListner.onResponse(requestCode, response.body());
                            } else if (response.errorBody() != null) {
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            mContext.showServerError();
                        }
                    }

                    @Override
                    public void onFailure(Call<BasicResponse> call, Throwable t) {
                        mContext.dismissProgressDialog();
mContext.showServerError();
                    }
                });
    }


    /*[Resource Profile]*/

    public void getTechnicianProfile(final int requestCode, final String userId) {
        BaseApplication.getRetrofitAPI(true)
                .getTechnicianProfile(userId)
                .enqueue(new Callback<TechnicianProfileDetails>() {
                    @Override
                    public void onResponse(Call<TechnicianProfileDetails> call,
                                           Response<TechnicianProfileDetails> response) {
                        if (response != null) {
                            if (response.code() == 401) { // Unauthorised Access
                                NetworkCallController controller = new NetworkCallController();
                                controller.setListner(new NetworkResponseListner<LoginResponse>() {
                                    @Override
                                    public void onResponse(int reqCode, LoginResponse response) {
                                        // delete all previous record
                                        Realm.getDefaultInstance().beginTransaction();
                                        Realm.getDefaultInstance().deleteAll();
                                        Realm.getDefaultInstance().commitTransaction();

                                        // add new record
                                        Realm.getDefaultInstance().beginTransaction();
                                        Realm.getDefaultInstance().copyToRealmOrUpdate(response);
                                        Realm.getDefaultInstance().commitTransaction();
                                        getTechnicianProfile(requestCode, userId);
                                    }

                                    @Override
                                    public void onFailure(int requestCode) {

                                    }
                                });
                                controller.refreshToken(100, getRefreshToken());
                            } else if (response.body() != null) {
                                mListner.onResponse(requestCode, response.body().getData());

                            } else if (response.errorBody() != null) {
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                                    mContext.showServerError(jObjError.getString("ErrorMessage"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            mContext.showServerError();
                        }
                    }

                    @Override
                    public void onFailure(Call<TechnicianProfileDetails> call, Throwable t) {
                        mContext.showServerError("Please try again !!!");
                    }
                });
    }


    public String getRefreshToken() {
        String refreshToken = null;
        try {
            LoginResponse loginResponse =
                    Realm.getDefaultInstance().where(LoginResponse.class).findAll().get(0);
            refreshToken = loginResponse.getRefreshToken();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return refreshToken;
    }

}
