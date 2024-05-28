package com.diev.salarymaster.Model;

public class TimeWork {
    String id, uuid, business, date, start, finish, note;
    double total, wage;

    public TimeWork() {
    }

    public TimeWork(String id, String uuid, String business, String date, String start, String finish, String note, double total, double wage) {
        this.id = id;
        this.uuid = uuid;
        this.business = business;
        this.date = date;
        this.start = start;
        this.finish = finish;
        this.note = note;
        this.total = total;
        this.wage = wage;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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

    public String getBusiness() {
        return business;
    }

    public void setBusiness(String business) {
        this.business = business;
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

    public String getFormattedTime() {
        if (!note.isEmpty()) {
            return start + " - " + finish + " (" + note + ")";
        } else {
            return start + " - " + finish;
        }
    }

}
