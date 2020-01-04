package com.example.madcamp_project_2;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class LoginData implements Serializable {
    private String userId;
    private String name;
    private String email;
    private String gender;
    private String birthday;

    public String getBirthday() {
        return birthday;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    public String getName() {
        return name;
    }

    public String getUserId() {
        return userId;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUser(JSONObject object) {
        try {
            setUserId(object.getString("id"));
        } catch (JSONException e) {
            setUserId("");
        }

        try {
            setName(object.getString("name"));
        } catch (JSONException e) {
            setName("");
        }

        try {
            setEmail(object.getString("email"));
        } catch (JSONException e) {
            setEmail("");
        }

        try {
            setGender(object.getString("gender"));
        } catch (JSONException e) {
            setGender("");
        }

        try {
            setBirthday(object.getString("birthday"));
        } catch (JSONException e) {
            setBirthday("");
        }
    }
}
