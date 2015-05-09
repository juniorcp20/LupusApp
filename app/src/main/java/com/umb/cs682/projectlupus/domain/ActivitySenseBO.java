package com.umb.cs682.projectlupus.domain;

public class ActivitySenseBO {

    private Long id;
    private int stepCount;
    private java.util.Date date;

    public ActivitySenseBO() {
    }

    public ActivitySenseBO(Long id) {
        this.id = id;
    }

    public ActivitySenseBO(Long id, int stepCount, java.util.Date date) {
        this.id = id;
        this.stepCount = stepCount;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getStepCount() {
        return stepCount;
    }

    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }

    public java.util.Date getDate() {
        return date;
    }

    public void setDate(java.util.Date date) {
        this.date = date;
    }

}
