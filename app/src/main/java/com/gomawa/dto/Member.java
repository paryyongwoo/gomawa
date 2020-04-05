package com.gomawa.dto;

import java.time.LocalDateTime;

public class Member {

    private Long key; // 네이버, 카카오 로그인에서 제공하는 식별자 id
    private String email;
    private String gender;
    private String nickName; // 닉네임
    private LocalDateTime regDate;

    public Member() {

    }

    public Long getKey() {
        return key;
    }

    public void setKey(Long key) {
        this.key = key;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNickName() { return nickName; }

    public void setNickName(String nickName) { this.nickName = nickName; }

    public LocalDateTime getRegDate() {
        return regDate;
    }

    public void setRegDate(LocalDateTime regDate) {
        this.regDate = regDate;
    }

    @Override
    public String toString() {
        return "Member{" +
                "key=" + key +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", nickName='" + nickName + '\'' +
                ", regDate=" + regDate +
                '}';
    }

    public Member(Long key, String email, String gender, String nickName, LocalDateTime regDate) {
        this.key = key;
        this.email = email;
        this.gender = gender;
        this.nickName = nickName;
        this.regDate = regDate;
    }
}
