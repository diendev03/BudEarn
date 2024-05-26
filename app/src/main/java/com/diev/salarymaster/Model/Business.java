package com.diev.salarymaster.Model;

public class Business {
    String id, avatar, name, salary, dateStart, uuid;

    public Business(String id, String avatar, String name, String salary, String dateStart, String uuid) {
        this.id = id;
        this.avatar = avatar;
        this.name = name;
        this.salary = salary;
        this.dateStart = dateStart;
        this.uuid = uuid;
    }

    public Business() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }
}
