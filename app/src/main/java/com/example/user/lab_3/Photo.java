package com.example.user.lab_3;

public class Photo {

    private int id;
    private String name;
    private String title;

    public Photo(int id, String name){
        this.id = id;
        this.name = name;
        this.title = getTitle(name);
    }

    public static String getTitle(String path){
        String ph[] = path.split("/");
        return ph[ph.length-1];
    }

    public int getId(){
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }
}
