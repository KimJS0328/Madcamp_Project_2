package com.example.madcamp_project_2;

import android.graphics.Bitmap;

import java.io.Serializable;

public class ContactItem implements Serializable {
    private String user_phNumber, user_Name;
    private Bitmap user_photo;

    public ContactItem(){
    }
    public String getUser_phNumber(){
        return user_phNumber;
    }
    public  String getUser_Name(){
        return user_Name;
    }
    public void setUser_phNumber(String string){
        this.user_phNumber = string;
    }
    public void setUser_Name(String string){
        this.user_Name = string;
    }
    @Override
    public String toString(){
        return this.user_phNumber;
    }
    @Override
    public int hashCode(){
        return getPhNumberChanged().hashCode();
    }
    public String getPhNumberChanged(){
        return user_phNumber.replace("-", "");
    }
    @Override
    public boolean equals(Object o){
        if(o instanceof ContactItem)
            return getPhNumberChanged().equals((((ContactItem) o).getPhNumberChanged()));
        return false;
    }

    public Bitmap getUser_photo() {
        return user_photo;
    }

    public void setUser_photo(Bitmap user_photo) {
        this.user_photo = user_photo;
    }
}

