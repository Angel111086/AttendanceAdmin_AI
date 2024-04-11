package com.testing.attendanceadmin_ai.model;

import java.io.Serializable;

public class CompanyDataModel implements Serializable {

    String _id,CompanyName,CompanyAddress,CompanyWebsite,CompanyEmail,CompanyContactNumber;

    public CompanyDataModel(String companyName, String companyAddress, String companyWebsite, String companyEmail, String companyContactNumber) {
        CompanyName = companyName;
        CompanyAddress = companyAddress;
        CompanyWebsite = companyWebsite;
        CompanyEmail = companyEmail;
        CompanyContactNumber = companyContactNumber;
    }

    public CompanyDataModel(String _id, String companyName) {
        this._id = _id;
        CompanyName = companyName;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCompanyName() {
        return CompanyName;
    }

    public void setCompanyName(String companyName) {
        CompanyName = companyName;
    }

    public String getCompanyAddress() {
        return CompanyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        CompanyAddress = companyAddress;
    }

    public String getCompanyWebsite() {
        return CompanyWebsite;
    }

    public void setCompanyWebsite(String companyWebsite) {
        CompanyWebsite = companyWebsite;
    }

    public String getCompanyEmail() {
        return CompanyEmail;
    }

    public void setCompanyEmail(String companyEmail) {
        CompanyEmail = companyEmail;
    }

    public String getCompanyContactNumber() {
        return CompanyContactNumber;
    }

    public void setCompanyContactNumber(String companyContactNumber) {
        CompanyContactNumber = companyContactNumber;
    }


    public String toString() {
        return CompanyName;
    }
}
