package com.umb.cs682.projectlupus.domain;


public class ProfileBO {

    private Long id;
    private String userName;
    private String age;
    private String gender;
    private String ethnicity;

    public ProfileBO() {
    }

    public ProfileBO(Long id) {
        this.id = id;
    }

    public ProfileBO(Long id, String userName, String age, String gender, String ethnicity) {
        this.id = id;
        this.userName = userName;
        this.age = age;
        this.gender = gender;
        this.ethnicity = ethnicity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAge() {
        return age;
    } //NK

    public void setAge(String age) {
        this.age = age;
    } //NK

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(String ethnicity) {
        this.ethnicity = ethnicity;
    }

}
