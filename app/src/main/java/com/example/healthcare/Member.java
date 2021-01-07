package com.example.healthcare;

public class Member {
    private String Name;
    private Integer Age;
    private String Gender;
    private String Address;
    private Long Phone;
    private String Other;
    private String Other1;
    private  String Covid;


    public Member() {
    }


    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Integer getAge() {
        return Age;
    }

    public void setAge(Integer age) {
        Age = age;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public Long getPhone() {
        return Phone;
    }

    public void setPhone(Long phone) {
        Phone = phone;
    }

    public String getOther() {
        return Other;
    }

    public void setOther(String other) {
        Other = other;
    }

    public String getOther1() {
        return Other1;
    }

    public void setOther1(String other1) {
        Other1 = other1;
    }

    public String getCovid() {
        return Covid;
    }

    public void setCovid(String covid) {
        Covid = covid;
    }
}
