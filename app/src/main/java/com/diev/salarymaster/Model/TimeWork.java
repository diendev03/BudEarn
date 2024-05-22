package com.diev.salarymaster.Model;

public class TimeWork {
    String id, uuid, company, date, start, finish;
    double total,wage;

    public TimeWork() {
    }

    public TimeWork(String id, String uuid, String company, String date, String start, String finish, double total, double wage) {
        this.id = id;
        this.uuid = uuid;
        this.company = company;
        this.date = date;
        this.start = start;
        this.finish = finish;
        this.total = total;
        this.wage = wage;
    }

    public double getTotal() {
        return total;
    }

    public double getWage() {
        return wage;
    }

    public void setWage(double wage) {
        this.wage = wage;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getFinish() {
        return finish;
    }

    public void setFinish(String finish) {
        this.finish = finish;
    }
}
