package com.ab.hicarerun.network.models.TaskModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UpdateTasksRequest {
    @SerializedName("UserId")
    @Expose
    private String UserId;
    @SerializedName("ResourceId")
    @Expose
    private String ResourceId;
    @SerializedName("TaskId")
    @Expose
    private String TaskId;
    @SerializedName("SchedulingStatus")
    @Expose
    private String SchedulingStatus;
    @SerializedName("ScheduledDateTime")
    @Expose
    private String ScheduledDateTime;
    @SerializedName("Latitude")
    @Expose
    private String Latitude;
    @SerializedName("Longitude")
    @Expose
    private String Longitude;
    @SerializedName("IncompleteReason")
    @Expose
    private String IncompleteReason;
    @SerializedName("PaymentMode")
    @Expose
    private String PaymentMode;
    @SerializedName("AmountToCollect")
    @Expose
    private String AmountToCollect;
    @SerializedName("AmountCollected")
    @Expose
    private String AmountCollected;
    @SerializedName("BankName")
    @Expose
    private String BankName;
    @SerializedName("ChequeNo")
    @Expose
    private String ChequeNo;
    @SerializedName("ChequeImage")
    @Expose
    private String ChequeImage;
    @SerializedName("ChequeDate")
    @Expose
    private String ChequeDate;
    @SerializedName("Modified_DateTime")
    @Expose
    private String Modified_DateTime;
    @SerializedName("Duration")
    @Expose
    private String Duration;
    @SerializedName("InJeopardy")
    @Expose
    private String InJeopardy;
    @SerializedName("JeopardyStatus")
    @Expose
    private String JeopardyStatus;
    @SerializedName("JeopardyDateTime")
    @Expose
    private String JeopardyDateTime;
    @SerializedName("IsIncomplete")
    @Expose
    private String IsIncomplete;
    @SerializedName("Signatory")
    @Expose
    private String Signatory;
    @SerializedName("MobileNo")
    @Expose
    private String MobileNo;
    @SerializedName("AlternateMobileNo")
    @Expose
    private String AlternateMobileNo;
    @SerializedName("InterestedService")
    @Expose
    private String InterestedService;
    @SerializedName("Email")
    @Expose
    private String Email;
    @SerializedName("NumberOfBhk")
    @Expose
    private String NumberOfBhk;

    @SerializedName("ActualPropertySize")
    @Expose
    private String ActualPropertySize;

    @SerializedName("StandardPropertySize")
    @Expose
    private String StandardPropertySize;

    @SerializedName("TechnicianOTP")
    @Expose
    private String TechnicianOTP;
    @SerializedName("CustomerSign")
    @Expose
    private String CustomerSign;
    @SerializedName("Comment")
    @Expose
    private String Comment;

    @SerializedName("ChemicalList")
    @Expose
    private List<TaskChemicalList> ChemicalList = null;
    @SerializedName("TechnicianRating")
    @Expose
    private Integer TechnicianRating;

    public UpdateTasksRequest() {
        UserId = "";
        ResourceId = "";
        TaskId = "";
        SchedulingStatus = "";
        ScheduledDateTime = "";
        Latitude = "";
        Longitude = "";
        IncompleteReason = "";
        PaymentMode = "";
        AmountToCollect = "";
        AmountCollected = "";
        BankName = "";
        ChequeNo = "";
        ChequeDate = "";
        Modified_DateTime = "";
        Duration = "";
        InJeopardy = "";
        JeopardyStatus = "";
        JeopardyDateTime = "";
        IsIncomplete = "";
        Signatory = "";
        MobileNo = "";
        AlternateMobileNo = "";
        InterestedService = "";
        Email = "";
        NumberOfBhk = "";
        TechnicianOTP = "";
        CustomerSign = "";
        Comment = "";
        ActualPropertySize = "";
        StandardPropertySize = "";
        ChemicalList = null;
        TechnicianRating = 0;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getResourceId() {
        return ResourceId;
    }

    public void setResourceId(String resourceId) {
        ResourceId = resourceId;
    }

    public String getTaskId() {
        return TaskId;
    }

    public void setTaskId(String taskId) {
        TaskId = taskId;
    }

    public String getSchedulingStatus() {
        return SchedulingStatus;
    }

    public void setSchedulingStatus(String schedulingStatus) {
        SchedulingStatus = schedulingStatus;
    }

    public String getScheduledDateTime() {
        return ScheduledDateTime;
    }

    public void setScheduledDateTime(String scheduledDateTime) {
        ScheduledDateTime = scheduledDateTime;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getIncompleteReason() {
        return IncompleteReason;
    }

    public void setIncompleteReason(String incompleteReason) {
        IncompleteReason = incompleteReason;
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

    public String getModified_DateTime() {
        return Modified_DateTime;
    }

    public void setModified_DateTime(String modified_DateTime) {
        Modified_DateTime = modified_DateTime;
    }

    public String getDuration() {
        return Duration;
    }

    public void setDuration(String duration) {
        Duration = duration;
    }

    public String getInJeopardy() {
        return InJeopardy;
    }

    public void setInJeopardy(String inJeopardy) {
        InJeopardy = inJeopardy;
    }

    public String getJeopardyStatus() {
        return JeopardyStatus;
    }

    public void setJeopardyStatus(String jeopardyStatus) {
        JeopardyStatus = jeopardyStatus;
    }

    public String getJeopardyDateTime() {
        return JeopardyDateTime;
    }

    public void setJeopardyDateTime(String jeopardyDateTime) {
        JeopardyDateTime = jeopardyDateTime;
    }

    public String getIsIncomplete() {
        return IsIncomplete;
    }

    public void setIsIncomplete(String isIncomplete) {
        IsIncomplete = isIncomplete;
    }


    public String getMobileNo() {
        return MobileNo;
    }

    public void setMobileNo(String mobileNo) {
        MobileNo = mobileNo;
    }

    public String getAlternateMobileNo() {
        return AlternateMobileNo;
    }

    public void setAlternateMobileNo(String alternateMobileNo) {
        AlternateMobileNo = alternateMobileNo;
    }

    public String getInterestedService() {
        return InterestedService;
    }

    public void setInterestedService(String interestedService) {
        InterestedService = interestedService;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getNumberOfBhk() {
        return NumberOfBhk;
    }

    public void setNumberOfBhk(String numberOfBhk) {
        NumberOfBhk = numberOfBhk;
    }


    public String getCustomerSign() {
        return CustomerSign;
    }

    public void setCustomerSign(String customerSign) {
        CustomerSign = customerSign;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public String getSignatory() {
        return Signatory;
    }

    public void setSignatory(String signatory) {
        Signatory = signatory;
    }

    public String getActualPropertySize() {
        return ActualPropertySize;
    }

    public void setActualPropertySize(String actualPropertySize) {
        ActualPropertySize = actualPropertySize;
    }

    public String getStandardPropertySize() {
        return StandardPropertySize;
    }

    public void setStandardPropertySize(String standardPropertySize) {
        StandardPropertySize = standardPropertySize;
    }

    public String getTechnicianOTP() {
        return TechnicianOTP;
    }

    public void setTechnicianOTP(String technicianOTP) {
        TechnicianOTP = technicianOTP;
    }

    public List<TaskChemicalList> getChemicalList() {
        return ChemicalList;
    }

    public void setChemicalList(List<TaskChemicalList> chemicalList) {
        ChemicalList = chemicalList;
    }

    public Integer getTechnicianRating() {
        return TechnicianRating;
    }

    public void setTechnicianRating(Integer technicianRating) {
        TechnicianRating = technicianRating;
    }

    public String getChequeImage() {
        return ChequeImage;
    }

    public void setChequeImage(String chequeImage) {
        ChequeImage = chequeImage;
    }
}
