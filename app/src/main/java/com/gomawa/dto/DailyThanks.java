package com.gomawa.dto;

import java.time.LocalDateTime;

public class DailyThanks {

    private Long id;
    private String content1;
    private String content2;
    private String content3;
    private LocalDateTime regDate;
    private Member regMember;

    public DailyThanks() {

    }

    public DailyThanks(Long id, String content1, String content2, String content3, LocalDateTime regDate, Member regMember) {
        this.id = id;
        this.content1 = content1;
        this.content2 = content2;
        this.content3 = content3;
        this.regDate = regDate;
        this.regMember = regMember;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent1() {
        return content1;
    }

    public void setContent1(String content1) {
        this.content1 = content1;
    }

    public String getContent2() {
        return content2;
    }

    public void setContent2(String content2) {
        this.content2 = content2;
    }

    public String getContent3() {
        return content3;
    }

    public void setContent3(String content3) {
        this.content3 = content3;
    }

    public LocalDateTime getRegDate() {
        return regDate;
    }

    public void setRegDate(LocalDateTime regDate) {
        this.regDate = regDate;
    }

    public Member getRegMember() {
        return regMember;
    }

    public void setRegMember(Member regMember) {
        this.regMember = regMember;
    }

    @Override
    public String toString() {
        return "DailyThanks{" +
                "id=" + id +
                ", content1='" + content1 + '\'' +
                ", content2='" + content2 + '\'' +
                ", content3='" + content3 + '\'' +
                ", regDate=" + regDate +
                '}';
    }
}
