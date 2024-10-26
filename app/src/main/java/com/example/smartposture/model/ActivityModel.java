package com.example.smartposture.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ActivityModel {
    private String activitydescription;
    private String activityname;
    private String enddate;
    private String endtime;
    private int roomid;
    private String startdate;
    private String starttime;

    // Constructors, getters, and setters
    public ActivityModel() { }

    public ActivityModel(String activitydescription, String activityname, String enddate, String endtime, int roomid, String startdate, String starttime) {
        this.activitydescription = activitydescription;
        this.activityname = activityname;
        this.enddate = enddate;
        this.endtime = endtime;
        this.roomid = roomid;
        this.startdate = startdate;
        this.starttime = starttime;
    }

    public String getActivitydescription() {
        return activitydescription;
    }

    public void setActivitydescription(String activitydescription) {
        this.activitydescription = activitydescription;
    }

    public String getActivityname() {
        return activityname;
    }

    public void setActivityname(String activityname) {
        this.activityname = activityname;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public int getRoomid() {
        return roomid;
    }

    public void setRoomid(int roomid) {
        this.roomid = roomid;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }
}
