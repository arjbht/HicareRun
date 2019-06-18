package com.ab.hicarerun.viewmodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.ab.hicarerun.network.models.ReferralModel.ReferralList;
import com.ab.hicarerun.network.models.TaskModel.Tasks;

public class ReferralListViewModel implements Parcelable {
    private Integer id;
    private String TaskId;
    private String FirstName;
    private String LastName;
    private String MobileNo;
    private String AlternateMobileNo;
    private String Email;
    private String InterestedService;

    public ReferralListViewModel() {
        id = 0;
        TaskId = "NA";
        FirstName = "NA";
        LastName = "NA";
        MobileNo = "NA";
        AlternateMobileNo = "NA";
        Email = "NA";
        InterestedService = "NA";
    }

    protected ReferralListViewModel(Parcel in) {
        id = in.readInt();
        TaskId = in.readString();
        FirstName = in.readString();
        LastName = in.readString();
        MobileNo = in.readString();
        AlternateMobileNo = in.readString();
        Email = in.readString();
        InterestedService = in.readString();
    }

    public static final Creator<ReferralListViewModel> CREATOR = new Creator<ReferralListViewModel>() {
        @Override
        public ReferralListViewModel createFromParcel(Parcel in) {
            return new ReferralListViewModel(in);
        }

        @Override
        public ReferralListViewModel[] newArray(int size) {
            return new ReferralListViewModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(TaskId);
        dest.writeString(FirstName);
        dest.writeString(LastName);
        dest.writeString(MobileNo);
        dest.writeString(AlternateMobileNo);
        dest.writeString(Email);
        dest.writeString(InterestedService);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTaskId() {
        return TaskId;
    }

    public void setTaskId(String taskId) {
        TaskId = taskId;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
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

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getInterestedService() {
        return InterestedService;
    }

    public void setInterestedService(String interestedService) {
        InterestedService = interestedService;
    }

    public void clone(ReferralList referralList) {
        this.id = referralList.getId();
        this.TaskId = referralList.getTaskId();
        this.FirstName = referralList.getFirstName();
        this.LastName = referralList.getLastName();
        this.MobileNo = referralList.getMobileNo();
        this.AlternateMobileNo= referralList.getAlternateMobileNo();
        this.Email = referralList.getEmail();
        this.InterestedService = referralList.getInterestedService();

    }

}
