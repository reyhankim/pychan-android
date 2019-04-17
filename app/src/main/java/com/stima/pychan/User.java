package com.stima.pychan;

public class User {
    private String nickname;

    public User(String nickname) {
        setNickname(nickname);
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
