package com.example.finaldesigntest;

import android.support.annotation.NonNull;

/**
 * Created by 达明 on 2018/3/29.
 */

public class toPlayTaskList implements Comparable{

    private int hour;
    private int minute;
    private String path;
    public toPlayTaskList(int hour, int minute, String path){
        this.hour = hour;
        this.minute = minute;
        this.path = path;
    }
    public int getHour(){
        return hour;
    }
    public int getMinute(){
        return minute;
    }
    public String getPath(){
        return path;
    }
    public void setHour(int hour){
        this.hour = hour;
    }
    public void setMinute(int minute){
        this.minute = minute;
    }
    public void setPath(String path){
        this.path = path;
    }
    @Override
    public int compareTo(@NonNull Object o) {
        toPlayTaskList s = (toPlayTaskList) o;
        if(this.hour > s.hour){
            return 1;
        }
        else if(this.hour < s.hour){
            return -1;
        }
        else{
            if(this.minute >= s.minute){
                return 1;
            }
            else{
                return -1;
            }
        }
    }
}

