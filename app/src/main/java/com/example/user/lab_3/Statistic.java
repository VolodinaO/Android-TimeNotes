package com.example.user.lab_3;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class Statistic {

    public static List<String> getTopCount(String dateStart, String dateEnd){
        List<String> list = new ArrayList<>();

        List<Integer> category = new ArrayList<>();
        List<Integer> number = new ArrayList<>();

        SQLiteDatabase db = DbHelper.getInstance().getReadableDatabase();
        String query = "SELECT _id FROM Category";
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            category.add(cursor.getInt(0));
            number.add(0);
        }
        cursor.close();

        int k = 0;
        query = "SELECT category_id,date FROM Record";
        cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            if(compareDate(dateStart,cursor.getString(1))==0||compareDate(dateStart,cursor.getString(1))==-1)
                if(compareDate(dateEnd,cursor.getString(1))==0||compareDate(dateEnd,cursor.getString(1))==1)
                {
                    k = category.indexOf(cursor.getInt(0));
                    if(k!=-1)
                    number.set(k, number.get(k) + 1);
                }
        }
        cursor.close();

        int min = 1000;
        for(int i = 0; i<number.size();i++){
            if(number.get(i)<=min){
                min = category.get(i);
            }
        }

        int id1=min,id2=min,id3=min;
        int h1=0,h2=0,h3=0;
        for(int i = 0; i<number.size();i++){
            if(number.get(i)>h1){
                h3 = h2;
                h2 = h1;
                h1 = number.get(i);
                id3 = id2;
                id2 = id1;
                id1 = category.get(i);
            }
            else if(number.get(i)>h2){
                h3 = h2;
                h2 = number.get(i);
                id3 = id2;
                id2 = category.get(i);
            }
            else if(number.get(i)>h3){
                h3 = number.get(i);
                id3 = category.get(i);
            }
        }

        query = "SELECT name FROM Category where _id='"+id1+"'";
        cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            list.add(cursor.getString(0)+" - "+h1);
        }
        cursor.close();
        query = "SELECT name FROM Category where _id='"+id2+"'";
        cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            list.add(cursor.getString(0)+" - "+h2);
        }
        cursor.close();
        query = "SELECT name FROM Category where _id='"+id3+"'";
        cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            list.add(cursor.getString(0)+" - "+h3);
        }
        cursor.close();
        return list;
    }

    public static List<String> getTopTime(String dateStart, String dateEnd){
        List<String> list = new ArrayList<>();

        List<Integer> category = new ArrayList<>();
        List<String> time = new ArrayList<>();

        SQLiteDatabase db = DbHelper.getInstance().getReadableDatabase();
        String query = "SELECT _id FROM Category";
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            category.add(cursor.getInt(0));
            time.add("0:0");
        }
        cursor.close();

        int k = 0;
        query = "SELECT category_id,time,date FROM Record";
        cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            if(compareDate(dateStart,cursor.getString(2))==0||compareDate(dateStart,cursor.getString(2))==-1)
                if(compareDate(dateEnd,cursor.getString(2))==0||compareDate(dateEnd,cursor.getString(2))==1)
                {
                    k = category.indexOf(cursor.getInt(0));
                    if(k!=-1)
                    time.set(k, getSumTime(time.get(k), cursor.getString(1)));
                }
        }
        cursor.close();

        String min = "1000:0";
        int m = 1000;
        for(int i = 0; i<time.size();i++){
            if(!compareTime(time.get(i),min)){
                m = category.get(i);
                min = time.get(i);
            }
        }

        int id1=m,id2=m,id3=m;
        String t1="0:0",t2="0:0",t3="0:0";
        for(int i = 0; i<time.size();i++){
            if(compareTime(time.get(i),t1)){
                t3 = t2;
                t2 = t1;
                t1 = time.get(i);
                id3 = id2;
                id2 = id1;
                id1 = category.get(i);
            }
            else if(compareTime(time.get(i),t2)){
                t3 = t2;
                t2 = time.get(i);
                id3 = id2;
                id2 = category.get(i);
            }
            else if(compareTime(time.get(i),t3)){
                t3 = time.get(i);
                id3 = category.get(i);
            }
        }

        query = "SELECT name FROM Category where _id='"+id1+"'";
        cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            list.add(cursor.getString(0)+" - "+t1);
        }
        cursor.close();
        query = "SELECT name FROM Category where _id='"+id2+"'";
        cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            list.add(cursor.getString(0)+" - "+t2);
        }
        cursor.close();
        query = "SELECT name FROM Category where _id='"+id3+"'";
        cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            list.add(cursor.getString(0)+" - "+t3);
        }
        cursor.close();
        return list;
    }

    public static String getSumTime(List<Integer> category,String dateStart, String dateEnd){
        List<String> list = new ArrayList<>();
        String time ="0:0";
        String query;
        Cursor cursor;
        SQLiteDatabase db = DbHelper.getInstance().getReadableDatabase();

        for(Integer cat:category){
            query = "SELECT time,date FROM Record where category_id='"+cat+"'";
            cursor = db.rawQuery(query, null);
            while (cursor.moveToNext()) {
                if(compareDate(dateStart,cursor.getString(1))==0||compareDate(dateStart,cursor.getString(1))==-1)
                    if(compareDate(dateEnd,cursor.getString(1))==0||compareDate(dateEnd,cursor.getString(1))==1)
                    {
                        time = getSumTime(time, cursor.getString(0));
                    }
            }
            cursor.close();
        }
        return time;
    }

    public static double[] getTimeAllCatogory(String dateStart, String dateEnd){
        double[] times;
        String[] time;

        List<Integer> category = new ArrayList<>();

        SQLiteDatabase db = DbHelper.getInstance().getReadableDatabase();
        String query = "SELECT _id FROM Category";
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            category.add(cursor.getInt(0));
        }
        cursor.close();

        time = new String[category.size()];
        times = new double[category.size()];
        for(int i=0; i<times.length;i++) {
            times[i] = 0.0;
            time[i] ="0:0";
        }

        for(int i = 0; i<category.size();i++){
            query = "SELECT time,date FROM Record where category_id='"+category.get(i)+"'";
            cursor = db.rawQuery(query, null);
            while (cursor.moveToNext()) {
                if(compareDate(dateStart,cursor.getString(1))==0||compareDate(dateStart,cursor.getString(1))==-1)
                    if(compareDate(dateEnd,cursor.getString(1))==0||compareDate(dateEnd,cursor.getString(1))==1)
                    {
                        time[i] = getSumTime(time[i], cursor.getString(0));
                    }
            }
            cursor.close();
        }

        double f;
        double h,m;
        String t[];
        for(int i=0; i<times.length;i++) {
            t = time[i].split(":");
            h = Double.parseDouble(t[0]);
            m = Double.parseDouble(t[1]);
            f = h+m*0.01;
            times[i] = f;
        }

        return times;
    }

    public static boolean compareTime(String t1, String t2){
        //t1>t2 - true
        //t1<=t2 - false
        boolean b = false;
        int h1,m1, h2, m2;
        String t[];

        t = t1.split(":");
        h1 = Integer.valueOf(t[0]);
        m1 = Integer.valueOf(t[1]);

        t = t2.split(":");
        h2 = Integer.valueOf(t[0]);
        m2 = Integer.valueOf(t[1]);

        if(h1>h2)
            b = true;
        else if(h1<h2)
            b = false;
        else if(m1>m2)
            b = true;
        else b = false;

        return b;
    }

    public static String getSumTime(String t1, String t2){
        int h,m,h1,m1, h2, m2;
        String t[];

        t = t1.split(":");
        h1 = Integer.valueOf(t[0]);
        m1 = Integer.valueOf(t[1]);

        t = t2.split(":");
        h2 = Integer.valueOf(t[0]);
        m2 = Integer.valueOf(t[1]);

        h = h1+h2;

        if(m1+m2>=60){
            m = m1+m2-60;
            h++;
        }else m = m1 + m2;

        return String.valueOf(h+":"+m);
    }

    public static int compareDate(String d1, String d2){
        //d1>d2 - 1
        //d1=d2 - 0
        //d1<d2 - -1
        int day1 = Integer.valueOf(d1.substring(0, 2));
        int day2 = Integer.valueOf(d2.substring(0, 2));
        int month1, month2, year1, year2;
        if (d1.length() == 9) {
            month1 = Integer.valueOf(d1.substring(3, 4));
            year1 = Integer.valueOf(d1.substring(5));
        } else {
            month1 = Integer.valueOf(d1.substring(3, 5));
            year1 = Integer.valueOf(d1.substring(6));
        }
        if (d2.length() == 9) {
            month2 = Integer.valueOf(d2.substring(3, 4));
            year2 = Integer.valueOf(d2.substring(5));
        } else {
            month2 = Integer.valueOf(d2.substring(3, 5));
            year2 = Integer.valueOf(d2.substring(6));
        }

        if(year1>year2) return 1;
        else if(year1<year2) return -1;
        else if(month1>month2) return 1;
        else if(month1<month2) return -1;
        else if(day1>day2) return 1;
        else if(day1<day2) return -1;
        else return 0;
    }

}
