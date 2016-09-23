package me.yluo.githubusersearch.model;

import java.util.List;

/**
 * Created by yang on 2016/9/22.
 * 用户数据model
 */

public class User {
    private int userId;
    private String userName;
    private String userIamge;
    private String reposUrl;
    private List<String> programLang;


    public User(int userId, String userName, String userIamge,String reposUrl) {
        this.userId = userId;
        this.userName = userName;
        this.userIamge = userIamge;
        this.reposUrl = reposUrl;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getReposUrl() {
        return reposUrl;
    }

    public String getUserIamge() {
        return userIamge;
    }

    public List<String> getProgramLang() {
        return programLang;
    }

    public void setProgramLang(List<String> programLang) {
        this.programLang = programLang;
    }
}
