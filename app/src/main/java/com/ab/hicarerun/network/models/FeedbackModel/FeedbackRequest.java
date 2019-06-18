package com.ab.hicarerun.network.models.FeedbackModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FeedbackRequest {
    @SerializedName("name") @Expose
    private String name;
    @SerializedName("feedback_code") @Expose
    private String feedback_code;
    @SerializedName("service_name") @Expose
    private String service_name;
    @SerializedName("order_number") @Expose
    private String order_number;
    @SerializedName("service_step_number") @Expose
    private String service_step_number;
    @SerializedName("technician_phone") @Expose
    private String technician_phone;
    @SerializedName("task_id") @Expose
    private String task_id;


    public FeedbackRequest() {
        this.name = "";
        this.feedback_code = "";
        this.service_name = "";
        this.order_number = "";
        this.service_step_number = "";
        this.technician_phone = "";
        this.task_id = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFeedback_code() {
        return feedback_code;
    }

    public void setFeedback_code(String feedback_code) {
        this.feedback_code = feedback_code;
    }

    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public String getOrder_number() {
        return order_number;
    }

    public void setOrder_number(String order_number) {
        this.order_number = order_number;
    }

    public String getService_step_number() {
        return service_step_number;
    }

    public void setService_step_number(String service_step_number) {
        this.service_step_number = service_step_number;
    }

    public String getTechnician_phone() {
        return technician_phone;
    }

    public void setTechnician_phone(String technician_phone) {
        this.technician_phone = technician_phone;
    }

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }
}
