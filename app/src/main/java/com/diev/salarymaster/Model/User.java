package com.diev.salarymaster.Model;

public class User {
    String uuid, email,fullname, birthday,phone;


    public User() {
    }

    public User(String uuid, String email, String fullname, String birthday, String phone) {
        this.uuid = uuid;
        this.email = email;
        this.fullname = fullname;
        this.birthday = birthday;
        this.phone = phone;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
