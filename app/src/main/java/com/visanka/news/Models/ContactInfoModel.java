package com.visanka.news.Models;

public class ContactInfoModel {
    String name;
    String number;

    public String getName() {
        return name;
    }

    public ContactInfoModel(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
