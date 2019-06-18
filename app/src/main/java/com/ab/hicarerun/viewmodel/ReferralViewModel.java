package com.ab.hicarerun.viewmodel;

public class ReferralViewModel {
    public String fname;
    public String lname;
    public String mobile;
    public String altmobile;
    public String interested;
    public String email;

    public ReferralViewModel() {
        this.fname = "NA";
        this.lname = "NA";
        this.mobile = "NA";
        this.altmobile = "NA";
        this.interested = "NA";
        this.email = "NA";
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAltmobile() {
        return altmobile;
    }

    public void setAltmobile(String altmobile) {
        this.altmobile = altmobile;
    }

    public String getInterested() {
        return interested;
    }

    public void setInterested(String interested) {
        this.interested = interested;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
