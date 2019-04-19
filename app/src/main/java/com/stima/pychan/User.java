package com.stima.pychan;

public class User {
    private String nickname;
    String profileUrl;

    public User(String nickname) {
        setNickname(nickname);
    }

    public String getNickname() {
        return this.nickname;
    }

    public String getProfileUrl() {
        return this.profileUrl;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
}
