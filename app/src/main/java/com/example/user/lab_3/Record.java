package com.example.user.lab_3;

import java.io.Serializable;

public class Record implements Serializable {
    private int id;
    private int categoryId;
    private String description;
    private String timeStart;
    private String timeEnd;
    private String time;
    private String date;
    private String photoIdList;

    public Record(int id, int categoryId, String date, String description, String timeStart, String timeEnd, String time, String photoIdList){
        this.id = id;
        this.categoryId = categoryId;
        this.date = date;
        this.description = description;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.photoIdList = photoIdList;
        this.time = time;
    }

    public int getId(){
        return id;
    }

    public int getCategoryId(){
        return categoryId;
    }

    public String getDate(){
        return date;
    }

    public String getDescription(){
        return description;
    }

    public String getTimeStart(){
        return timeStart;
    }

    public String getTimeEnd(){
        return timeEnd;
    }

    public String getTime(){
        return time;
    }

    public String getPhotoIdList(){
        return photoIdList;
    }

}
