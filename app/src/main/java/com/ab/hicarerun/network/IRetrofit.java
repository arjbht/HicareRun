package com.ab.hicarerun.network;

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
import com.ab.hicarerun.network.models.TaskModel.UpdateTaskResponse;
import com.ab.hicarerun.network.models.TaskModel.UpdateTasksRequest;
import com.ab.hicarerun.network.models.TechnicianGroomingModel.TechGroomingRequest;
import com.ab.hicarerun.network.models.TechnicianGroomingModel.TechGroomingResponse;
import com.ab.hicarerun.network.models.TrainingModel.TrainingResponse;
import com.ab.hicarerun.network.models.UpdateAppModel.UpdateResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface IRetrofit {
    //    String BASE_URL = "http://52.74.65.15/mobileapi/api/";
    //    String ERROR_LOG_URL = "http://52.74.65.15/logging/api/";
    String BASE_URL = "http://run.hicare.in/mobile/api/";
    String EXOTEL_URL = "http://apps.hicare.in/api/api/";
    String ERROR_LOG_URL = "http://run.hicare.in/logging/api/";

    /*[Verify User]*/

    @GET("userverification/VerifyUser")
    Call<SendOtpResponse> sendOtp(@Query("mobileno") String mobile, @Query("resendOtp") String isResend);

    /*[Login]*/

    @FormUrlEncoded
    @POST("Login")
    Call<LoginResponse> login(@Field("grant_type") String grantType,
                              @Field("UserName") String username, @Field("Password") String password,
                              @Header("Content-Type") String content_type,
                              @Header("IMEINo") String imei, @Header("AppVersion") String version, @Header("DeviceInfo") String deviceinfo, @Header("PlayerId") String mStrPlayerId);
    /*[Refresh Token]*/

    @FormUrlEncoded
    @POST("login")
    Call<LoginResponse> refreshToken(@Field("grant_type") String grantType,
                                     @Field("refresh_token") String refreshToken);

    /*[Task Details]*/

    @GET("Task/GetTaskList")
    Call<TaskListResponse> getTasksList(@Query("resourceId") String resourceId, @Query("deviceId") String IMEI);

    /*[Task Details By ID]*/

    @GET("Task/GetTaskDetailsById")
    Call<GeneralResponse> getTasksDetailById(@Query("resourceId") String resourceId, @Query("taskId") String taskId);

    /*[Save Referral]*/

    @POST("CustomerReferral/SaveCustomerReferralDetails")
    Call<ReferralResponse> postReferrals(@Body ReferralRequest request);

    /*[Referral Details]*/

    @GET("CustomerReferral/GetReferralDetailsByTaskId")
    Call<ReferralListResponse> getReferrals(@Query("taskId") String taskId);

    /*[Delete Referral]*/

    @POST("CustomerReferral/DeleteCustomerReferralDetails")
    Call<ReferralResponse> getDeleteReferrals(@Body ReferralDeleteRequest request);

    /*[Send Feedback]*/

    @POST("Feedback/SendFeedbackLink")
    Call<FeedbackResponse> postFeedBackLink(@Body FeedbackRequest request);

    /*[Upload Attachment]*/

    @POST("Attachment/UploadAttachment")
    Call<PostAttachmentResponse> postAttachments(@Body PostAttachmentRequest request);

    /*[Delete Attachment]*/

    @POST("Attachment/DeleteAttachmentDetails")
    Call<PostAttachmentResponse> getDeleteAttachments(@Body List<AttachmentDeleteRequest> request);

    /*[Attachment Details]*/

    @GET("Attachment/GetAttachmentDetailsByTaskId")
    Call<GetAttachmentResponse> getAttachments(@Query("resourceId") String resourceId, @Query("taskId") String taskId);

    /*[Update Tasks]*/

    @POST("Task/UpdateTaskDetails")
    Call<UpdateTaskResponse> updateTasks(@Body UpdateTasksRequest request);

    /*[HandShake]*/

    @GET("ResourceActivity/InitializeActivityHandshake")
    Call<HandShakeResponse> getHandShake();

    /*[Continue HandShake]*/

    @POST("ResourceActivity/PostResourceActivity")
    Call<ContinueHandShakeResponse> getContinueHandShake(@Body ContinueHandShakeRequest request);

    /*[Chemicals Details]*/

    @GET("ChemicalConsumption/GetChemimcalDetails")
    Call<ChemicalResponse> getChemicals(@Query("taskId") String taskId);

    /*[Logout]*/

    @POST("ResourceActivity/LogOut")
    Call<LogoutResponse> getLogout(@Query("resourceId") String UserId);

    /*[Dial Customer]*/

    @GET("applicationlogic/DialExotelNumber")
    Call<ExotelResponse> getCallExotel(@Query("customerNo") String customerNo, @Query("techNo") String techNo);

    /*[Mark Attendance]*/
    @POST("ResourceActivity/PostResourceAttendance")
    Call<ContinueHandShakeResponse> getTechAttendance(@Body AttendanceRequest request);

    /*[Register Profile]*/

    @POST("ResourceActivity/PostResourceProfilePic")
    Call<HandShakeResponse> getProfilePic(@Body ProfilePicRequest request);

    /*[Training Videos]*/

    @GET("VideoUploader/GettrainingVideoDetails")
    Call<TrainingResponse> getTrainingVideos();

    /*[Error Log]*/

    @POST("Log/Publish")
    Call<String> sendErrorLog(@Body ErrorLoggerModel request);

    /*[Update APP api]*/

    @GET("ResourceActivity/VersionCheck")
    Call<UpdateResponse> getUpdateApp();

    /*[Send Payment Link]*/

    @POST("Payment/SendPaymentLink")
    Call<PaymentLinkResponse> sendPaymentLink(@Body PaymentLinkRequest request);

    /*[Get Technician Grooming]*/

    @GET("TechnicianGrooming/GetDetails")
    Call<TechGroomingResponse> getGroomingTechnicians(@Query("resourceId") String customerNo);

    /*[Post Grooming Image]*/

    @POST("TechnicianGrooming/UploadImage")
    Call<BasicResponse> postGroomingImage(@Body TechGroomingRequest request);

    /*[Resource Profile]*/

    @GET("ResourceActivity/GetResourceProfileDetails")
    Call<TechnicianProfileDetails> getTechnicianProfile(@Query("resourceId") String customerNo);


}
