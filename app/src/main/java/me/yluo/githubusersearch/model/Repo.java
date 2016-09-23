package me.yluo.githubusersearch.model;

/**
 * Created by yang on 2016/9/23.
 * 用户 repo model
 */

public class Repo {
    private int id;
    private String name;
    private String language;

    public Repo(int id, String name, String language) {
        this.id = id;
        this.name = name;
        this.language = language;
    }

    public int getId() {
        return id;
    }

    public String getLanguage() {
        return language;
    }

    public String getName() {
        return name;
    }
}
