package com.umb.cs682.projectlupus.domain;


public class MoodLevelBO {

    private Long id;
    private long reminderId;
    private java.util.Date date;
    private int moodLevel;

    public MoodLevelBO() {
    }

    public MoodLevelBO(Long id) {
        this.id = id;
    }

    public MoodLevelBO(Long id, long reminderId, java.util.Date date, int moodLevel) {
        this.id = id;
        this.reminderId = reminderId;
        this.date = date;
        this.moodLevel = moodLevel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getReminderId() {
        return reminderId;
    }

    public void setReminderId(long reminderId) {
        this.reminderId = reminderId;
    }

    public java.util.Date getDate() {
        return date;
    }

    public void setDate(java.util.Date date) {
        this.date = date;
    }

    public int getMoodLevel() {
        return moodLevel;
    }

    public void setMoodLevel(int moodLevel) {
        this.moodLevel = moodLevel;
    }

}
