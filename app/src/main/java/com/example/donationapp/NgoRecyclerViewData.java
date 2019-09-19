package com.example.donationapp;

public class NgoRecyclerViewData {


    private String NgoName;
    private String WorkingDays;
    private String NgoFromTime;
    private String NgoToTime;
    private String Address;
    private String NgoPhone;

    public NgoRecyclerViewData(String ngoName, String workingDays, String ngoFromTime, String ngoToTime, String availableCategory, String ngoPhone) {
      NgoName = ngoName;
        WorkingDays = workingDays;
        NgoFromTime = ngoFromTime;
        NgoToTime = ngoToTime;
        Address = availableCategory;
        NgoPhone = ngoPhone;
    }

    public String getNgoName() {
        return NgoName;
    }

    public void setNgoName(String ngoName) {
        NgoName = ngoName;
    }

    public String getWorkingDays() {
        return WorkingDays;
    }

    public void setWorkingDays(String workingDays) {
        WorkingDays = workingDays;
    }

    public String getNgoFromTime() {
        return NgoFromTime;
    }

    public void setNgoFromTime(String ngoFromTime) {
        NgoFromTime = ngoFromTime;
    }

    public String getNgoToTime() {
        return NgoToTime;
    }

    public void setNgoToTime(String ngoToTime) {
        NgoToTime = ngoToTime;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String availableCategory) {
        Address = availableCategory;
    }

    public String getNgoPhone() {
        return NgoPhone;
    }

    public void setNgoPhone(String ngoPhone) {
        NgoPhone = ngoPhone;
    }

public NgoRecyclerViewData(){

}
}
