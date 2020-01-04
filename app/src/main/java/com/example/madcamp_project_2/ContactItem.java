package com.example.madcamp_project_2;

import java.io.Serializable;

public class ContactItem implements Serializable {
    private String user_phNumber, user_Name;
    private long photo_id = 0, person_id = 0;
    private int id;

    public ContactItem(){
    }
    public long getPhoto_id(){
        return photo_id;
    }
    public long getPerson_id(){
        return person_id;
    }
    public void setPhoto_id(long id){
        this.photo_id = id;
    }
    public void setPerson_id(long id){
        this.person_id = id;
    }
    public String getUser_phNumber(){
        return user_phNumber;
    }
    public  String getUser_Name(){
        return user_Name;
    }
    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return id;
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
}

