package com.ab.hicarerun.network.models.GeneralModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class GeneralData implements RealmModel {
    @SerializedName("SchedulingStatus")
    @Expose
    private String SchedulingStatus;

    @SerializedName("PaymentMode")
    @Expose
    private String PaymentMode;

    @SerializedName("AmountToCollect")
    @Expose
    private String AmountToCollect;

    @SerializedName("IncompleteReason")
    @Expose
    private String IncompleteReason;

    @SerializedName("AmountCollected")
    @Expose
    private String AmountCollected;

    @SerializedName("MobileNumber")
    @Expose
    private String MobileNumber;

    @SerializedName("CustName")
    @Expose
    private String CustName;

    @SerializedName("Email")
    @Expose
    private String Email;

    @SerializedName("Duration")
    @Expose
    private String Duration;
    @PrimaryKey
    @SerializedName("OrderNumber")
    @Expose
    private String OrderNumber;

    @SerializedName("NumberOfBhk")
    @Expose
    private String NumberOfBhk;

    @SerializedName("Technician_OTP")
    @Expose
    private String TechnicianOTP;

    @SerializedName("Sc_OTP")
    @Expose
    private String Sc_OTP;

    @SerializedName("Customer_OTP")
    @Expose
    private String Customer_OTP;

    @SerializedName("CustomerSign")
    @Expose
    private String Signatory;

    @SerializedName("SignatureUrl")
    @Expose
    private String SignatureUrl;

    @SerializedName("ServicePlan")
    @Expose
    private String ServicePlan;


    @SerializedName("ServiceType")
    @Expose
    private String ServiceType;

    @SerializedName("TaskAssignmentStartTime")
    @Expose
    private String TaskAssignmentStartTime;

    @SerializedName("IsFeedBack")
    @Expose
    private Boolean IsFeedBack;

    @SerializedName("IsPaymentValidation")
    @Expose
    private Boolean IsPaymentValidation;

    @SerializedName("IsJobCardRequired")
    @Expose
    private Boolean IsJobCardRequired;

    @SerializedName("AutoSubmitChemicals")
    @Expose
    private Boolean IsAutoSubmitChemicals;

    @SerializedName("IsChequeRequired")
    @Expose
    private Boolean IsChequeRequired;

    @SerializedName("TaskAssignmentEndTime")
    @Expose
    private String TaskAssignmentEndTime;

    @SerializedName("StandardPropertySize")
    @Expose
    private String StandardPropertySize;

    @SerializedName("ActualPropertySize")
    @Expose
    private String ActualPropertySize;

    @SerializedName("IsTechnicianFeedbackRequired")
    @Expose
    private Boolean IsTechnicianFeedbackRequired;

    @SerializedName("TechnicianRating")
    @Expose
    private String TechnicianRating;

    @SerializedName("IsIncentiveEnable")
    @Expose
    private Boolean IsIncentiveEnable;

    @SerializedName("IncentivePoint")
    @Expose
    private String IncentivePoint;
    @SerializedName("BankName")
    @Expose
    private String BankName;

    @SerializedName("ChequeNo")
    @Expose
    private String ChequeNo;

    @SerializedName("ChequeDate")
    @Expose
    private String ChequeDate;

    @SerializedName("ChequeImageUrl")
    @Expose
    private String ChequeImageUrl;

    @SerializedName("SchedulingStatusList")
    @Expose
    private RealmList<GeneralTaskStatus> SchedulingStatusModel = null;

    @SerializedName("PaymentModeList")
    @Expose
    private RealmList<GeneralPaymentMode> PaymentModeList = null;
    @SerializedName("IncompleteReasonList")
    @Expose
    private RealmList<IncompleteReason> IncompleteReasonList = null;

    public GeneralData() {
        SchedulingStatus = "NA";
        PaymentMode = "NA";
        AmountToCollect = "NA";
        IncompleteReason = "NA";
        AmountCollected = "NA";
        MobileNumber = "NA";
        CustName = "NA";
        Email = "NA";
        Duration = "NA";
        OrderNumber = "NA";
        NumberOfBhk = "NA";
        TechnicianOTP = "NA";
        Sc_OTP = "NA";
        Customer_OTP = "NA";
        Signatory = "NA";
        SignatureUrl = "NA";
        ServicePlan = "NA";
        ServiceType = "NA";
        TaskAssignmentStartTime = "NA";
        IsFeedBack = false;
        IsPaymentValidation = false;
        IsJobCardRequired = false;
        IsAutoSubmitChemicals = false;
        IsChequeRequired = false;
        TaskAssignmentEndTime = "NA";
        StandardPropertySize = "NA";
        ActualPropertySize = "NA";
        IsTechnicianFeedbackRequired = false;
        TechnicianRating = "NA";
        IsIncentiveEnable = false;
        IncentivePoint = "NA";
        BankName = "NA";
        ChequeNo = "NA";
        ChequeDate = "NA";
        ChequeImageUrl = "NA";
    }

    public String getOrderNumber() {
        return OrderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        OrderNumber = orderNumber;
    }

    public String getTaskAssignmentStartTime() {
        return TaskAssignmentStartTime;
    }

    public void setTaskAssignmentStartTime(String taskAssignmentStartTime) {
        TaskAssignmentStartTime = taskAssignmentStartTime;
    }

    public String getTaskAssignmentEndTime() {
        return TaskAssignmentEndTime;
    }

    public void setTaskAssignmentEndTime(String taskAssignmentEndTime) {
        TaskAssignmentEndTime = taskAssignmentEndTime;
    }

    public String getSchedulingStatus() {
        return SchedulingStatus;
    }

    public void setSchedulingStatus(String schedulingStatus) {
        SchedulingStatus = schedulingStatus;
    }

    public String getPaymentMode() {
        return PaymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        PaymentMode = paymentMode;
    }

    public String getAmountToCollect() {
        return AmountToCollect;
    }

    public void setAmountToCollect(String amountToCollect) {
        AmountToCollect = amountToCollect;
    }

    public String getAmountCollected() {
        return AmountCollected;
    }

    public void setAmountCollected(String amountCollected) {
        AmountCollected = amountCollected;
    }

    public String getMobileNumber() {
        return MobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        MobileNumber = mobileNumber;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getDuration() {
        return Duration;
    }

    public void setDuration(String duration) {
        Duration = duration;
    }

    public RealmList<GeneralTaskStatus> getSchedulingStatusModel() {
        return SchedulingStatusModel;
    }

    public void setSchedulingStatusModel(RealmList<GeneralTaskStatus> schedulingStatusModel) {
        SchedulingStatusModel = schedulingStatusModel;
    }

    public String getServicePlan() {
        return ServicePlan;
    }

    public void setServicePlan(String servicePlan) {
        ServicePlan = servicePlan;
    }

    public String getServiceType() {
        return ServiceType;
    }

    public void setServiceType(String serviceType) {
        ServiceType = serviceType;
    }

    public String getNumberOfBhk() {
        return NumberOfBhk;
    }

    public void setNumberOfBhk(String numberOfBhk) {
        NumberOfBhk = numberOfBhk;
    }

    public String getTechnicianOTP() {
        return TechnicianOTP;
    }

    public void setTechnicianOTP(String technicianOTP) {
        TechnicianOTP = technicianOTP;
    }

    public String getSignatureUrl() {
        return SignatureUrl;
    }

    public void setSignatureUrl(String signatureUrl) {
        SignatureUrl = signatureUrl;
    }

    public RealmList<GeneralPaymentMode> getPaymentModeList() {
        return PaymentModeList;
    }

    public void setPaymentModeList(RealmList<GeneralPaymentMode> paymentModeList) {
        PaymentModeList = paymentModeList;
    }

    public String getSignatory() {
        return Signatory;
    }

    public void setSignatory(String signatory) {
        Signatory = signatory;
    }

    public Boolean getFeedBack() {
        return IsFeedBack;
    }

    public void setFeedBack(Boolean feedBack) {
        IsFeedBack = feedBack;
    }

    public Boolean getPaymentValidation() {
        return IsPaymentValidation;
    }

    public void setPaymentValidation(Boolean paymentValidation) {
        IsPaymentValidation = paymentValidation;
    }

    public Boolean getJobCardRequired() {
        return IsJobCardRequired;
    }

    public void setJobCardRequired(Boolean jobCardRequired) {
        IsJobCardRequired = jobCardRequired;
    }

    public Boolean getAutoSubmitChemicals() {
        return IsAutoSubmitChemicals;
    }

    public void setAutoSubmitChemicals(Boolean autoSubmitChemicals) {
        IsAutoSubmitChemicals = autoSubmitChemicals;
    }

    public Boolean getChequeRequired() {
        return IsChequeRequired;
    }

    public void setChequeRequired(Boolean chequeRequired) {
        IsChequeRequired = chequeRequired;
    }

    public String getCustName() {
        return CustName;
    }

    public void setCustName(String custName) {
        CustName = custName;
    }

    public String getStandardPropertySize() {
        return StandardPropertySize;
    }

    public void setStandardPropertySize(String standardPropertySize) {
        StandardPropertySize = standardPropertySize;
    }

    public String getActualPropertySize() {
        return ActualPropertySize;
    }

    public void setActualPropertySize(String actualPropertySize) {
        ActualPropertySize = actualPropertySize;
    }

    public String getSc_OTP() {
        return Sc_OTP;
    }

    public void setSc_OTP(String sc_OTP) {
        Sc_OTP = sc_OTP;
    }

    public String getCustomer_OTP() {
        return Customer_OTP;
    }

    public void setCustomer_OTP(String customer_OTP) {
        Customer_OTP = customer_OTP;
    }

    public RealmList<IncompleteReason> getIncompleteReasonList() {
        return IncompleteReasonList;
    }

    public void setIncompleteReasonList(RealmList<IncompleteReason> incompleteReasonList) {
        IncompleteReasonList = incompleteReasonList;
    }

    public Boolean getTechnicianFeedbackRequired() {
        return IsTechnicianFeedbackRequired;
    }

    public void setTechnicianFeedbackRequired(Boolean technicianFeedbackRequired) {
        IsTechnicianFeedbackRequired = technicianFeedbackRequired;
    }

    public String getTechnicianRating() {
        return TechnicianRating;
    }

    public void setTechnicianRating(String technicianRating) {
        TechnicianRating = technicianRating;
    }

    public Boolean getIncentiveEnable() {
        return IsIncentiveEnable;
    }

    public void setIncentiveEnable(Boolean incentiveEnable) {
        IsIncentiveEnable = incentiveEnable;
    }

    public String getIncentivePoint() {
        return IncentivePoint;
    }

    public void setIncentivePoint(String incentivePoint) {
        IncentivePoint = incentivePoint;
    }

    public String getChequeImageUrl() {
        return ChequeImageUrl;
    }

    public void setChequeImageUrl(String chequeImageUrl) {
        ChequeImageUrl = chequeImageUrl;
    }

    public String getIncompleteReason() {
        return IncompleteReason;
    }

    public void setIncompleteReason(String incompleteReason) {
        IncompleteReason = incompleteReason;
    }

    public String getBankName() {
        return BankName;
    }

    public void setBankName(String bankName) {
        BankName = bankName;
    }

    public String getChequeNo() {
        return ChequeNo;
    }

    public void setChequeNo(String chequeNo) {
        ChequeNo = chequeNo;
    }

    public String getChequeDate() {
        return ChequeDate;
    }

    public void setChequeDate(String chequeDate) {
        ChequeDate = chequeDate;
    }
}
