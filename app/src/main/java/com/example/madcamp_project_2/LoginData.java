package com.example.madcamp_project_2;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class LoginData implements Serializable {
    private String userId;
    private String name;
    private String passwd;

    public String getName() {
        return name;
    }

    public String getUserId() {
        return userId;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }
}
