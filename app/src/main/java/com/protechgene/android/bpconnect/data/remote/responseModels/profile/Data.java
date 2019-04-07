package com.protechgene.android.bpconnect.data.remote.responseModels.profile;

import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("firstname")
    private String firstname;
    @SerializedName("gender")
    private String gender;
    @SerializedName("isGraduated")
    private boolean isGraduated;
    @SerializedName("address2")
    private String address2;
    @SerializedName("patientId")
    private Integer patientId;
    @SerializedName("address1")
    private String address1;
    @SerializedName("mobile1")
    private String mobile1;
    @SerializedName("isAlphanumericPatientId")
    private String isAlphanumericPatientId;
    @SerializedName("middlename")
    private String middlename;
    @SerializedName("userId")
    private Integer userId;
    @SerializedName("lastname")
    private String lastname;
    @SerializedName("dob")
    private String dob;
    @SerializedName("mobile2")
    private String mobile2;
    @SerializedName("email")
    private String email;
    @SerializedName("country")
    private String country;
    @SerializedName("accessRole")
    private String accessRole;
    @SerializedName("photo_url")
    private String photo_url;



    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isGraduated() {
        return isGraduated;
    }

    public void setGraduated(boolean graduated) {
        isGraduated = graduated;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getMobile1() {
        return mobile1;
    }

    public void setMobile1(String mobile1) {
        this.mobile1 = mobile1;
    }

    public String getIsAlphanumericPatientId() {
        return isAlphanumericPatientId;
    }

    public void setIsAlphanumericPatientId(String isAlphanumericPatientId) {
        this.isAlphanumericPatientId = isAlphanumericPatientId;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getMobile2() {
        return mobile2;
    }

    public void setMobile2(String mobile2) {
        this.mobile2 = mobile2;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAccessRole() {
        return accessRole;
    }

    public void setAccessRole(String accessRole) {
        this.accessRole = accessRole;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }
}
